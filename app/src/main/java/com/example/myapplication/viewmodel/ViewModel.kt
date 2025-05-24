package com.example.myapplication.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.metamask.androidsdk.Ethereum
import androidx.lifecycle.viewModelScope
import com.example.myapplication.contract.MyTokenizedAssets
import com.example.myapplication.contract.MyTokenizedAssets.Asset
import com.example.myapplication.data.AssetData
import com.example.myapplication.di.INFURA_KEY
import com.example.myapplication.data.WalletConnectUiState
import io.metamask.androidsdk.EthereumMethod
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Result
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.StaticGasProvider
import java.io.File
import java.math.BigInteger
import java.math.BigDecimal
import kotlin.math.pow
import java.time.Instant

var CONTRACT_ADDRESS = "XXX"

@HiltViewModel
class ViewModel @Inject constructor(private val ethereum: Ethereum): ViewModel() {

    var isLoading by mutableStateOf(false)

    // is wallet connected state
    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    //Metadata assets fetched from smart-contract(assets)
    var assetsDataList by mutableStateOf<List<Asset>>(emptyList())

    //Assets parsed from Pinata
    private val _parsedAssets = MutableStateFlow<List<AssetData>>(emptyList())
    val parsedAssets: StateFlow<List<AssetData>> = _parsedAssets

    //Save search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    //Smart-contract
    private var contract: MyTokenizedAssets ?= null

    //Navigation event
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    //wallet address connected
    var address by mutableStateOf("")
        private set

    //connected wallet balance
    var balance by mutableStateOf(BigInteger("0"))
        private set

    private var compositeDisposable = CompositeDisposable()


    //Connecting metamask wallet
    fun connectWallet() {
        viewModelScope.launch {
            ethereum.connect {
                address = ethereum.selectedAddress
                if (!address.isNullOrBlank()) {
                    //create contract
                    createContract(address)
                    checkContractConnection("ViewModel.connectWallet()")
                    //fetch data
                    Log.i("FETCH","Fetch data connect wallet")
                    contract?.let { fetchAssetData() }
                    //navigate to
                    _uiState.value = _uiState.value.copy(walletConnected = true)
                    //get balance
//                    getBalance()
                } else {
                    Log.e("TESTWALLET", "Failed to connect to wallet")
                }
            }
        }
    }

    //Connecting to contract
    private fun createContract(address: String? = null) {
        if (contract == null) {
            val httpService = HttpService("https://sepolia.infura.io/v3/$INFURA_KEY")
            val web3j = Web3j.build(httpService)

            val transactionManager = if (_uiState.value.continueWithoutWallet){
                ReadonlyTransactionManager(web3j, CONTRACT_ADDRESS)
            } else {
                ClientTransactionManager(web3j, address)
            }
            contract = MyTokenizedAssets.load(
                CONTRACT_ADDRESS,
                web3j,
                transactionManager,
                StaticGasProvider(BigInteger.ZERO, BigInteger.valueOf(16000000))
            )
            checkContractConnection("ViewModel.createContract()")
            //initEventListeners()
        }
    }

    //Minting asset to block-chain
    fun mintAsset(assetURI: String) {
        val mintFunction = Function(
            "mintAsset",
            listOf(Utf8String(assetURI)),
            emptyList()
        )
        val contractMetaData = FunctionEncoder.encode(mintFunction)
        val params: Map<String, Any> = mutableMapOf(
            "from" to address,
            "to" to CONTRACT_ADDRESS,
            "data" to contractMetaData
        )
        val sendTransactionRequest = EthereumRequest(
            method = EthereumMethod.ETH_SEND_TRANSACTION.value,
            params = listOf(params)
        )
        ethereum.connectWith(sendTransactionRequest) { result ->
            when(result) {
                is Result.Error -> {
                    Log.e("FAIL", "ViewModel.mintAsset() Error ${result.error.message}")
                }
                is Result.Success.Item -> {
                    val transactionHash = result.value
                    Log.i("INFO","ViewModel.mintAsset() Mint successful, transaction hash $transactionHash")
//                    viewModelScope.launch(Dispatchers.IO) {
//                        fetchAssetData() // Для гарантії актуальності
//                    }
                    _navigationEvent.value = NavigationEvent.GoToProfileScreen
                }
                else -> {  }
            }
        }
    }

    //User place bid for asset
    fun placeBid(assetId: BigInteger, bidAmount: BigInteger) {
        val placeBidFunction = Function(
            "placeBid",
            listOf(Uint256(assetId)),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(placeBidFunction)

        val params: Map<String, Any> = mutableMapOf(
            "from" to address,
            "to" to CONTRACT_ADDRESS,
            "value" to bidAmount.toString(16),
            "data" to encodedFunction
        )

        val sendTransactionRequest = EthereumRequest(
            method = EthereumMethod.ETH_SEND_TRANSACTION.value,
            params = listOf(params)
        )

        ethereum.connectWith(sendTransactionRequest) { result ->
            when(result) {
                is Result.Error -> {
                    Log.e("FAIL", "ViewModel.placeBid() Error ${result.error.message}")
                }
                is Result.Success.Item -> {
                    Log.i("INFO","ViewModel.placeBid() Bid placed, transaction hash ${result.value}")
                    _parsedAssets.update { assets ->
                        assets.map { asset ->
                            if (asset.assetId == assetId) {
                                asset.copy(highestBid = bidAmount, highestBidder = address)
                            } else asset
                        }
                    }
                    _navigationEvent.value = NavigationEvent.GoToAssetDetailScreen

                    viewModelScope.launch {
                        delay(1500)
                        Log.i("FETCH", "Fetch data from place bid....")
                        fetchAssetData()
                    }
                }
                else -> { }
            }
        }
    }

    //User buyout asset
    fun buyout(assetId: BigInteger, buyoutAmount: BigInteger) {
        val buyoutFunction = Function(
            "buyout",
            listOf(Uint256(assetId)),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(buyoutFunction)

        val params: Map<String, Any> = mutableMapOf(
            "from" to address,
            "to" to CONTRACT_ADDRESS,
            "value" to buyoutAmount.toString(16),
            "data" to encodedFunction
        )

        val sendTransactionRequest = EthereumRequest(
            method = EthereumMethod.ETH_SEND_TRANSACTION.value,
            params = listOf(params)
        )

        ethereum.connectWith(sendTransactionRequest) { result ->
            when(result) {
                is Result.Error -> {
                    Log.e("FAIL", "ViewModel.buyout() Error ${result.error.message}")
                }
                is Result.Success.Item -> {
                    Log.i("INFO","ViewModel.buyout() Buyout successful, transaction hash ${result.value}")
                    _parsedAssets.update { assets ->
                        assets.map { asset ->
                            if (asset.assetId == assetId) {
                                asset.copy(
                                    owner = address,
                                    buyoutPrice = BigInteger.ZERO,
                                    auctionEndTime = BigInteger.ZERO,
                                    highestBid = BigInteger.ZERO,
                                    highestBidder = "0x0000000000000000000000000000000000000000"
                                )
                            } else asset
                        }
                    }
                    _navigationEvent.value = NavigationEvent.GoToProfileScreen

                    viewModelScope.launch(Dispatchers.IO) {
                        delay(1500)
                        Log.i("FETCH", "Fetch data from buyout....")
                        fetchAssetData()
                    }
                }
                else -> { }
            }
        }
    }

    //User list his asset for auction
    @RequiresApi(Build.VERSION_CODES.O)
    fun listAssetForAuction(assetId: BigInteger, buyoutPrice: BigInteger, auctionEndTime: BigInteger,) {
        val listFunction = Function(
            "listAssetForAuction",
            listOf(
                Uint256(assetId),
                Uint256(buyoutPrice),
                Uint256(auctionEndTime)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(listFunction)

        val params: Map<String, Any> = mutableMapOf(
            "from" to address,
            "to" to CONTRACT_ADDRESS,
            "data" to encodedFunction
        )

        val sendTransactionRequest = EthereumRequest(
            method = EthereumMethod.ETH_SEND_TRANSACTION.value,
            params = listOf(params)
        )

        ethereum.connectWith(sendTransactionRequest) { result ->
            when(result) {
                is Result.Error -> {
                    Log.e("FAIL", "ViewModel.listAssetForAuction() Error ${result.error.message}")
                }
                is Result.Success.Item -> {
                    Log.i("INFO","ViewModel.listAssetForAuction() Listing successful, transaction hash ${result.value}")
                    _parsedAssets.update { assets ->
                        assets.map { asset ->
                            if (asset.assetId == assetId) {
                                asset.copy(
                                    buyoutPrice = buyoutPrice,
                                    auctionEndTime = (Instant.now().epochSecond.toBigInteger() + auctionEndTime)
                                )
                            } else asset
                        }
                    }
                    _navigationEvent.value = NavigationEvent.GoToAssetDetailScreen
                    viewModelScope.launch(Dispatchers.IO) {
                        delay(1500)
                        Log.i("FETCH", "Fetch data from list asset....")
                        fetchAssetData()
                    }
                }
                else -> { }
            }
        }
    }

    //Fetching asset from smart-contract
    fun fetchAssetData() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                contract?.let {
                    assetsDataList = it.allAssets.send() as List<Asset>
                    Log.d("FETCH", "id ${assetsDataList[0].assetId} auctionEndTime ${assetsDataList[0].auctionEndTime}")
                    Log.d("FETCH", "id ${assetsDataList[1].assetId} auctionEndTime ${assetsDataList[1].auctionEndTime}")
                    Log.d("FETCH", "id ${assetsDataList[2].assetId} auctionEndTime ${assetsDataList[2].auctionEndTime}")
                    parseAsset(assetsDataList)
                    Log.i("FETCH","_parsedAssets ${_parsedAssets.value.size} assetsDataList ${assetsDataList.size}")
                }
            } catch (e: Exception) {
                isLoading = false
                Log.e("FAIL", "ViewModel.fetchNftData() Error fetching Assets ${e.message}")
            }
        }
    }

    //Function to upload query entered by user
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    //Filter assets by query
    val filteredAssets: StateFlow<List<AssetData>> = combine(
        _searchQuery, _parsedAssets
    ) { query, assets ->
        assets.filter {
            (it.auctionEndTime > BigInteger.ZERO) && (
                it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //Creation main data of asset and upload to Pinata
    fun createAsset(imageFile: File, name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val mediaType = "image/jpeg".toMediaTypeOrNull()
                val body = imageFile.asRequestBody(mediaType)

                val multipartBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", imageFile.name, body)
                    .build()

                val request = Request.Builder()
                    .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiJjNmExY2QxOC0xNWI3LTQxYzEtODgyYS0xOTgwYjNlNjQxMTUiLCJlbWFpbCI6Im1hdHVzeWFrLm1ha3N5bUBsbGwua3BpLnVhIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiRlJBMSJ9LHsiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiTllDMSJ9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZSwic3RhdHVzIjoiQUNUSVZFIn0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6IjAyZGFiNWM1NDhjMDNiNzUxNmIzIiwic2NvcGVkS2V5U2VjcmV0IjoiMGRmMzZkMTJkNGNiODRhNTBkYzkyYTk2NTA5ODE0MmM3MzAzOTczODlkOTFjNTE3MDgwYjhiZTM5NWM5ZTcwZSIsImV4cCI6MTc3ODQxODgwNH0.Lc_giNfGb3fMtAftABCA7_6BY9dIquYGCNhoqxaPLq0")
                    .post(multipartBody)
                    .build()

                val response = client.newCall(request).execute()
                val json = JSONObject(response.body?.string() ?: "")
                val imageHash = json.getString("IpfsHash")

                val metadata = JSONObject().apply {
                    put("name", name)
                    put("description", description)
                    put("image", "ipfs://$imageHash")
                }

                val metadataBody = metadata.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val metadataRequest = Request.Builder()
                    .url("https://api.pinata.cloud/pinning/pinJSONToIPFS")
                    .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySW5mb3JtYXRpb24iOnsiaWQiOiJjNmExY2QxOC0xNWI3LTQxYzEtODgyYS0xOTgwYjNlNjQxMTUiLCJlbWFpbCI6Im1hdHVzeWFrLm1ha3N5bUBsbGwua3BpLnVhIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInBpbl9wb2xpY3kiOnsicmVnaW9ucyI6W3siZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiRlJBMSJ9LHsiZGVzaXJlZFJlcGxpY2F0aW9uQ291bnQiOjEsImlkIjoiTllDMSJ9XSwidmVyc2lvbiI6MX0sIm1mYV9lbmFibGVkIjpmYWxzZSwic3RhdHVzIjoiQUNUSVZFIn0sImF1dGhlbnRpY2F0aW9uVHlwZSI6InNjb3BlZEtleSIsInNjb3BlZEtleUtleSI6IjAyZGFiNWM1NDhjMDNiNzUxNmIzIiwic2NvcGVkS2V5U2VjcmV0IjoiMGRmMzZkMTJkNGNiODRhNTBkYzkyYTk2NTA5ODE0MmM3MzAzOTczODlkOTFjNTE3MDgwYjhiZTM5NWM5ZTcwZSIsImV4cCI6MTc3ODQxODgwNH0.Lc_giNfGb3fMtAftABCA7_6BY9dIquYGCNhoqxaPLq0")
                    .post(metadataBody)
                    .build()

                val metadataResponse = client.newCall(metadataRequest).execute()
                val metaJson = JSONObject(metadataResponse.body?.string() ?: "")
                val metadataHash = metaJson.getString("IpfsHash")

                mintAsset("ipfs://$metadataHash")

            } catch (e: Exception) {
                Log.e("FAIL", "ViewModel.createAsset() Upload failed: ${e.message}")
            }
        }
    }

    //Parse asset from Pinata buy assetsDataList from smart-contract
    fun parseAsset(assetsDataList: List<Asset>){
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()

            val jobs: List<Deferred<AssetData?>> = assetsDataList.map { assetData ->
                async {
                    parseSingleAssetWithRetry(assetData, client)
                }
            }

            val resultList = jobs.awaitAll().filterNotNull()

            _parsedAssets.value = resultList
            isLoading = false
            if (resultList.size == assetsDataList.size) {
                Log.i("FETCH","Parsing complete. Parsed all ${resultList.size} assets.")
            } else {
                Log.i("FETCH","Parsing complete. Parsed ${resultList.size} out of ${assetsDataList.size} assets.")
            }
        }
    }

    //Change ui state to continue without wallet connection
    fun onContinueWithoutWalletClick() {
        viewModelScope.launch {
            createContract(null) // Без адреси
            checkContractConnection("connectWithoutWallet()")
            Log.i("FETCH", "Fetch data without wallet")
            contract?.let { fetchAssetData() }
            _uiState.value = _uiState.value.copy(continueWithoutWallet = true)
        }
    }

    //Reset Ui state to back to wallet connect screen
    fun resetWalletConnectUiState() {
        contract = null
        _uiState.value = _uiState.value.copy(continueWithoutWallet = false)
        _uiState.value = _uiState.value.copy(walletConnected = false)
    }

    //Logout from account
    fun logOut(){
        resetWalletConnectUiState()
        address = ""
        ethereum.clearSession()
    }

    fun getBalance(): BigDecimal {
        fetchBalance()
        return BigDecimal(balance)
    }

    //fetch balance by smart-contract
    fun fetchBalance(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                contract?.let {
                    balance = it.getETHBalance(address).send()
                    Log.i("GETBALANCE", "getBalance(): balace fetched $balance")
                }
            } catch (e: Exception) {
                Log.e("ERROR", "getBalance(): Error getting balance ${e.message}")
            }
        }
    }

    //Convert wei balance to ETH
    fun weiToEth(wei: BigDecimal): BigDecimal {
        val ethInWei = BigDecimal("1000000000000000000")  // 10^18
        return wei.divide(ethInWei)
    }

    //Convert ETH to wei
    fun ethToWei(eth: BigDecimal): BigDecimal {
        val ethInWei = BigDecimal("1000000000000000000")  // 10^18
        return eth.multiply(ethInWei)
    }

    //help-method to parse asset from pinata
    suspend fun parseSingleAssetWithRetry(assetData: Asset, client: OkHttpClient): AssetData? {
        val jsonUrl = assetData.metaDataUri.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")
        val imageUrl: String

        // Fetch and parse metadata JSON
        val jsonResponse = executeWithRetry(Request.Builder().url(jsonUrl).build(), client) ?: return null
        val json = try {
            JSONObject(jsonResponse.body?.string() ?: return null)
        } catch (e: Exception) {
            Log.e("FAIL", "ViewModel.parseSingleNFTWithRetry() Invalid JSON for asset ${assetData.assetId} ${e.message}")
            return null
        }

        val name = json.optString("name", "Unnamed")
        val description = json.optString("description", "")
        val imageUri = json.optString("image", "")
        imageUrl = imageUri.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")

        // Fetch image
        val imageResponse = executeWithRetry(Request.Builder().url(imageUrl).build(), client) ?: return null
        val imageBody = imageResponse.body ?: return null

        // Save to temp file
        val imageExt = imageUrl.substringAfterLast('.', "jpg").take(5)
        val safeName = "asset_${assetData.assetId}_${System.currentTimeMillis()}"
        val tempFile = File.createTempFile(safeName, ".$imageExt").apply {
            outputStream().use { imageBody.byteStream().copyTo(it) }
        }

        Log.i("PARSE_FILE", "id = ${assetData.assetId} name $name + auctionEndTime ${assetData.auctionEndTime}")

        return AssetData(
            assetId = assetData.assetId,
            name = name,
            description = description,
            imageFile = tempFile,
            owner = assetData.owner,
            buyoutPrice = assetData.buyoutPrice,
            auctionEndTime = assetData.auctionEndTime,
            highestBid = assetData.highestBid,
            highestBidder = assetData.highestBidder
        )
    }

    //help-method to do more try for parse
    suspend fun executeWithRetry(request: Request, client: OkHttpClient, maxAttempts: Int = 3): Response? {
        repeat(maxAttempts) { attempt ->
            Log.i("PARSE_W_RETRY", "attempt $attempt")
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) return response
                response.close()
            } catch (e: Exception) {
                Log.e("FAIL", "ViewModel.executeWithRetry() Network error: ${e.message}")
            }
            delay(500L * 2.0.pow(attempt.toDouble()).toLong()) // exponential backoff
        }
        return null
    }

    //help-method to test is contract connected successful
    private fun checkContractConnection(where: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val name = contract?.name()?.send()
                Log.d("TEST", "$where + checkContractConnection(): Contract connection check successful, name: $name")
            } catch (e: Exception) {
                Log.e("FAIL", "$where + checkContractConnection(): Contract connection check failed: ${e.message}")
            }
        }
    }

    //Clear view model resources
    override fun onCleared() {
        compositeDisposable.dispose() //stop all RxJava subscriptions
        super.onCleared()
    }

    //Find asset by id
    fun findById(assetId: BigInteger) = _parsedAssets.value.find { it.assetId == assetId }

    //Find all not listed for auction assets where user address == owner (Моя колекція)
    fun findAssetsOwnedByUser() = _parsedAssets.value.filter {
        it.owner == address && it.auctionEndTime == 0.toBigInteger()
    }

    //Find all listed for auction assets where user address == owner (Мої предмети на продажі)
    fun findAssetsListedByUser() = _parsedAssets.value.filter {
        it.owner == address && it.auctionEndTime != 0.toBigInteger()
    }

    //Find all assets where user address == highest bidder (Мої ставки)
    fun findAssetsByHighestBidder() = _parsedAssets.value.filter { it.highestBidder == address }

    //Function to show only part of address
    fun shortenAddress(addressToCut: String) =
        if (addressToCut.length > 10)
            "${addressToCut.take(5)}...${addressToCut.takeLast(3)}"
        else
            "No Address"

    //Return is current user r owner or highestBidder for asset in param
    fun isUserOwnerOrHighestBidder(asset: AssetData?) = address != asset?.owner && address != asset?.highestBidder

    //Sealed class for navigate by event
    sealed class NavigationEvent {
        object GoToAssetDetailScreen : NavigationEvent()
        object GoToAssetsForSaleScreen : NavigationEvent()
        object GoToBuyOutScreen : NavigationEvent()
        object GoToCreateAssetScreen : NavigationEvent()
        object GoToListAssetScreen : NavigationEvent()
        object GoToMakeBidScreen : NavigationEvent()
        object GoToProfileScreen : NavigationEvent()
        object GoToWalletConnectScreen : NavigationEvent()
    }

    //Clear event state
    fun onEventHandled() {
        _navigationEvent.value = null
    }
}