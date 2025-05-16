package com.example.myapplication.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch
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
import org.web3j.tx.gas.StaticGasProvider
import java.io.File
import java.math.BigInteger
import kotlin.math.pow

var CONTRACT_ADDRESS = "123"

@HiltViewModel
class ViewModel @Inject constructor(private val ethereum: Ethereum): ViewModel() {

    // is wallet connected state
    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    //Metadata assets fetched from smart-contract(assets)
    var assetsDataList by mutableStateOf<List<Asset>>(emptyList())

    //Assets parsed from Pinata
    private val _parsedAssets = MutableStateFlow<List<AssetData>>(emptyList())
    val parsedAssets: StateFlow<List<AssetData>> = _parsedAssets

    //Smart-contract
    private var contract: MyTokenizedAssets ?= null

    //Navigation event
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    //wallet address connected
    var address by mutableStateOf("")
        private set

    private var compositeDisposable = CompositeDisposable()
    var uploadStatus by mutableStateOf<String?>(null)
        private set
    var parseStatus by mutableStateOf<String?>(null)
        private set
    var uiStatus by mutableStateOf("ConnectWalletComponent")
        internal set


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
                    contract?.let { fetchAssetData() }
                    //navigate to
                    _uiState.value = _uiState.value.copy(walletConnected = true)
                } else {
                    Log.e("TESTWALLET", "Failed to connect to wallet")
                }
            }
        }
    }

    //Connecting to contract
    private fun createContract(address: String) {
        if (contract == null) {
            val httpService = HttpService("https://sepolia.infura.io/v3/$INFURA_KEY")
            val web3j = Web3j.build(httpService)
            contract = MyTokenizedAssets.load(
                CONTRACT_ADDRESS,
                web3j,
                ClientTransactionManager(web3j, address),
                StaticGasProvider(BigInteger.ZERO, BigInteger.valueOf(16000000))
            )
            checkContractConnection("ViewModel.createContract()")
            initEventListeners()
        }
    }

    //Minting asset to block-chain
    fun mintAsset(assetURI: String, onSuccess: () -> Unit) {
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
                    onSuccess()
                    //fetchAssetData()
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
                }
                else -> { }
            }
        }
    }

    //User list his asset for auction
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
                }
                else -> { }
            }
        }
    }

    //Fetching asset from smart-contract
    fun fetchAssetData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                contract?.let {
                    assetsDataList = it.allAssets.send() as List<Asset>
                    parseAsset(assetsDataList)
                }
            } catch (e: Exception) {
                Log.e("FAIL", "ViewModel.fetchNftData() Error fetching Assets ${e.message}")
            }
        }
    }

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

                uploadStatus = "NFT metadata uploaded: ipfs://$metadataHash"

                mintAsset("ipfs://$metadataHash") { uiStatus = "NFTListScreen" }

            } catch (e: Exception) {
                uploadStatus = "Upload failed: ${e.message}"
                Log.e("FAIL", "ViewModel.createAsset() $uploadStatus")
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
            parseStatus = if (resultList.size == assetsDataList.size) {
                "Parsing complete. Parsed all ${resultList.size} assets."
            } else {
                "Parsing complete. Parsed ${resultList.size} out of ${assetsDataList.size} assets."
            }
        }
    }

    //Change ui state to continue without wallet connection
    fun onContinueWithoutWalletClick() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = true)
    }

    //Reset Ui state to back to wallet connect screen
    fun resetWalletConnectUiState() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = false)
        _uiState.value = _uiState.value.copy(walletConnected = false)
    }

    //Logout from account
    fun logOut(){
        resetWalletConnectUiState()
        address = ""
        ethereum.clearSession()
    }

    //Initialization Event Listeners
    private fun initEventListeners() {
        contract?.let { contract ->

            //Event Listener on asset mint
            contract.assetMintedEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe({ event ->
                    viewModelScope.launch {
                        Log.i("EVENT", "AssetMinted: ${event.assetId}, ${event.uri}, ${event.owner}")
                        val newAsset = Asset(
                            event.assetId,
                            event.uri,
                            event.owner,
                            BigInteger("0"),
                            BigInteger("0"),
                            BigInteger("0"),
                            "0x0000000000000000000000000000000000000000"
                        )
                        val newAssetData = parseSingleAssetWithRetry(newAsset, OkHttpClient()) ?: AssetData()
                        _parsedAssets.update { it + newAssetData }
                        _navigationEvent.value = NavigationEvent.GoToProfileScreen

                        // Повільне повне оновлення у фоновому потоці
                        launch(Dispatchers.Default) {
                            fetchAssetData() // Для гарантії актуальності
                        }
                    }
                }, { error ->
                    Log.e("EVENT_ERROR", "AssetMinted listener error", error)
                }).let { disposable -> compositeDisposable.add(disposable as Disposable) }

            //Event Listener on asset list for auction
            contract.assetListedForAuctionEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe({ event ->
                    viewModelScope.launch {
                        Log.i("EVENT", "AssetListedForAuction: ${event.assetId}, ${event.buyoutPrice}, ${event.auctionDuration}")
                        //find asset by event.assetId in _parsedAssets to update buyoutPrice and auctionDuration
                        _parsedAssets.update { assets ->
                            assets.map { asset ->
                                if (asset.assetId == event.assetId) {
                                    asset.copy(buyoutPrice = event.buyoutPrice, auctionEndTime = event.auctionDuration)
                                } else asset
                            }
                        }
                        _navigationEvent.value = NavigationEvent.GoToAssetDetailScreen

                        fetchAssetData()
                    }
                }, { error ->
                    Log.e("EVENT_ERROR", "AssetListedForAuction listener error", error)
                }).let { disposable -> compositeDisposable.add(disposable as Disposable) }

            //Event Listener on place bid
            contract.bidPlacedEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe({ event ->
                    viewModelScope.launch {
                        Log.i("EVENT", "BidPlaced: ${event.assetId}, ${event.bidder}, ${event.amount}")
                        //find asset by event.assetId in _parsedAssets to update bidder and amount
                        _parsedAssets.update { assets ->
                            assets.map { asset ->
                                if (asset.assetId == event.assetId) {
                                    asset.copy(highestBid = event.amount, highestBidder = event.bidder)
                                } else asset
                            }
                        }
                        _navigationEvent.value = NavigationEvent.GoToAssetDetailScreen

                        fetchAssetData()
                    }
                }, { error ->
                    Log.e("EVENT_ERROR", "BidPlaced listener error", error)
                }).let { disposable -> compositeDisposable.add(disposable as Disposable) }

            //Event Listener on bought asset
            contract.assetBoughtEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe({ event ->
                    viewModelScope.launch {
                        Log.i("EVENT", "AssetBought: ${event.assetId}, ${event.buyer}, ${event.amount}")
                        //find asset by event.assetId in _parsedAssets to update owner and reset buyoutPrice, auctionEndTime, highestBid, highestBidder
                        _parsedAssets.update { assets ->
                            assets.map { asset ->
                                if (asset.assetId == event.assetId) {
                                    asset.copy(
                                        owner = event.buyer,
                                        buyoutPrice = BigInteger.ZERO,
                                        auctionEndTime = BigInteger.ZERO,
                                        highestBid = BigInteger.ZERO,
                                        highestBidder = "0x0000000000000000000000000000000000000000"
                                    )
                                } else asset
                            }
                        }
                        _navigationEvent.value = NavigationEvent.GoToProfileScreen

                        // Повільне повне оновлення у фоновому потоці
                        launch(Dispatchers.Default) {
                            fetchAssetData() // Для гарантії актуальності
                        }
                    }
                }, { error ->
                    Log.e("EVENT_ERROR", "AssetBought listener error", error)
                }).let { disposable -> compositeDisposable.add(disposable as Disposable) }
        }
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
            addressToCut ?: "No Address"

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