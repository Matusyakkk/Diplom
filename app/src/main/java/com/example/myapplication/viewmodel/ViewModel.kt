package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.metamask.androidsdk.Ethereum
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TestItem
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import java.io.File
import kotlin.math.pow

@HiltViewModel
class ViewModel @Inject constructor(private val ethereum: Ethereum): ViewModel() {

    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    private val _parsedNFTs = MutableStateFlow<List<TestItem>>(emptyList())
    val parsedNFTs: StateFlow<List<TestItem>> = _parsedNFTs

    var nftDataList by mutableStateOf<List<String>>(emptyList())

    var uploadStatus by mutableStateOf<String?>(null)
        private set
    var parseStatus by mutableStateOf<String?>(null)
        private set
    var uiStatus by mutableStateOf("ConnectWalletComponent")
        internal set

    var address by mutableStateOf("")
        private set

    fun connectWallet() {
        viewModelScope.launch {
            ethereum.connect {
                address = ethereum.selectedAddress
                if (!address.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(walletConnected = true)
                    //create contract
                    //fetch data
                } else {
                    Log.e("TESTWALLET", "Failed to connect to wallet")
                }
            }
        }
    }

    fun createNft(imageFile: File, name: String, description: String) {
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

                nftDataList = nftDataList + "ipfs://$metadataHash"

                mintNFT("ipfs://$metadataHash") { uiStatus = "NFTListScreen" }

            } catch (e: Exception) {
                uploadStatus = "Upload failed: ${e.message}"
                Log.e("TESTWALLET", uploadStatus.toString())
            }
        }
    }

    fun parseNft(nftDataList: List<String>){
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()

            val jobs: List<Deferred<TestItem?>> = nftDataList.map { nftData ->
                async {
                    parseSingleNFTWithRetry(nftData, client)
                }
            }

            val resultList = jobs.awaitAll().filterNotNull()

            _parsedNFTs.value = resultList// = resultList
            parseStatus = if (resultList.size == nftDataList.size) {
                "Parsing complete. Parsed all ${resultList.size} items."
            } else {
                "Parsing complete. Parsed ${resultList.size} out of ${nftDataList.size} items."
            }
        }
    }

    fun fetchNftData() {
        parseNft(nftDataList)
    }

    fun mintNFT(tokenURI: String, onSuccess: () -> Unit) {

    }

    fun onContinueWithoutWalletClick() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = true)
    }

    fun resetWalletConnectUiState() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = false)
        _uiState.value = _uiState.value.copy(walletConnected = false)
    }

    fun logOut(){
        resetWalletConnectUiState()
        address = ""
        ethereum.clearSession()
    }

    //help-method to parse nft from pinata
    suspend fun parseSingleNFTWithRetry(nftData: String, client: OkHttpClient): TestItem? {
        val jsonUrl = nftData.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")//nftData.tokenURI.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")
        val imageUrl: String

        // Fetch and parse metadata JSON
        val jsonResponse = executeWithRetry(Request.Builder().url(jsonUrl).build(), client) ?: return null
        val json = try {
            JSONObject(jsonResponse.body?.string() ?: return null)
        } catch (e: Exception) {
            Log.e("TESTWALLET", "Invalid JSON for token " +
                    /*"${nftData.tokenId}" +*/
                    ": ${e.message}")
            return null
        }

        val name = json.optString("name", "Unnamed")
        val description = json.optString("description", "")
        val imageUri = json.optString("image", "")
        imageUrl = imageUri.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")

        // Fetch image
        val imageResponse = executeWithRetry(Request.Builder().url(imageUrl).build(), client) ?: return null
        val imageBody = imageResponse.body ?: return null
        var i = 0
        // Save to temp file
        val imageExt = imageUrl.substringAfterLast('.', "jpg").take(5)
        val safeName = "nft_${i++}_${System.currentTimeMillis()}"
        val tempFile = File.createTempFile(safeName, ".$imageExt").apply {
            outputStream().use { imageBody.byteStream().copyTo(it) }
        }

        return TestItem(
            name = name,
            description = description,
            imageFile = tempFile
        )
    }

    //help-method to do more try for parse
    suspend fun executeWithRetry(request: Request, client: OkHttpClient, maxAttempts: Int = 3): Response? {
        repeat(maxAttempts) { attempt ->
            try {
                Log.w("TESTWALLET", "Attempt $attempt for URL: ${request.url}")
                val response = client.newCall(request).execute()
                if (response.isSuccessful) return response
                response.close()
            } catch (e: Exception) {
                Log.e("TESTWALLET", "Network error: ${e.message}")
            }
            delay(500L * 2.0.pow(attempt.toDouble()).toLong()) // exponential backoff
        }
        return null
    }

    data class WalletConnectUiState(
        val walletConnected: Boolean = false,
        val continueWithoutWallet: Boolean = false
    )
}