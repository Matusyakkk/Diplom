package com.example.myapplication.data.repository

import android.util.Log
import org.web3j.abi.datatypes.Function
import com.example.myapplication.contract.MyTokenizedAssets
import com.example.myapplication.contract.MyTokenizedAssets.Asset
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.domain.reopsitory.EthereumRepository
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.EthereumMethod
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.RequestError
import kotlinx.coroutines.Dispatchers
import io.metamask.androidsdk.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.protocol.Web3j
import org.web3j.tx.ClientTransactionManager
import org.web3j.tx.ReadonlyTransactionManager
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class EthereumRepositoryImpl @Inject constructor(
    private val ethereum: Ethereum,
    @Named("CONTRACT_ADDRESS") private val contractAddress: String,
    private val web3j: Web3j,
    private val gasProvider: StaticGasProvider
) : EthereumRepository {

    private var contract: MyTokenizedAssets? = null
    private var currentAddress: String? = null

    override suspend fun connectWallet(): String? = withContext(Dispatchers.IO) {
        try {
            suspendCancellableCoroutine { continuation ->
                ethereum.connect { result ->
                    when (result) {
                        is Result.Success -> {
                            currentAddress = ethereum.selectedAddress
                            if (currentAddress != null) continuation.resumeWith(kotlin.Result.success(currentAddress))
                            else continuation.resumeWith(kotlin.Result.failure(Exception("Address is null")))
                        }
                        is Result.Error -> {
                            val error = Exception("Error ${result.error.code}: ${result.error.message}")
                            Log.e("EthereumRepo", "Connection error", error)
                            continuation.resumeWith(kotlin.Result.failure(error))
                        }
                    }
                }
                continuation.invokeOnCancellation {
                    Log.d("EthereumRepo", "Connection cancelled")
                }
            }
        } catch (e: Exception) {
            Log.e("EthereumRepo", "Connect wallet failed", e)
            null
        }
    }

    override suspend fun getContract(): MyTokenizedAssets {
        return contract ?: createNewContractInstance().also { contract = it }
    }

    private suspend fun createNewContractInstance(): MyTokenizedAssets {
        return withContext(Dispatchers.IO) {
            val transactionManager = currentAddress?.let {
                ClientTransactionManager(web3j, it)
            } ?: ReadonlyTransactionManager(web3j, contractAddress)

            MyTokenizedAssets.load(
                contractAddress,
                web3j,
                transactionManager,
                gasProvider
            ).also {
                Log.d("EthereumRepo", "Contract initialized for address: $currentAddress")
            }
        }
    }

    override suspend fun sendTransaction(request: EthereumRequest): Result {
        return withContext(Dispatchers.IO) {
            try {
                suspendCancellableCoroutine<Result> { continuation ->
                    ethereum.connectWith(request) { result ->
                        when (result) {
                            is Result.Success -> continuation.resumeWith(kotlin.Result.success(result))
                            is Result.Error -> {
                                Log.e(
                                    "EthereumRepo",
                                    "Send transaction error",
                                    Exception("Error ${result.error.code}: ${result.error.message}")
                                )
                                continuation.resumeWith(kotlin.Result.failure(Exception("Error ${result.error.code}: ${result.error.message}")))
                            }
                        }
                    }
                    continuation.invokeOnCancellation {
                        Log.d("EthereumRepo", "Send transaction cancelled")
                    }
                }
            } catch (e: Exception) {
                Log.e("EthereumRepo", "Transaction failed", e)
                Result.Error(RequestError(-1, "Transaction execution error: ${e.message}"))
            }
        }
    }

    override suspend fun getAssets(): List<Asset> = withContext(Dispatchers.IO) {
        try {
            val contract = getContract()
            contract.allAssets.send() as List<Asset>
        } catch (e: Exception) {
            Log.e("Repo", "Failed to fetch assets", e)
            emptyList()
        }
    }

    override suspend fun isOwnerOrBidder(asset: AssetData?): Boolean =
        asset?.highestBidder == currentAddress || asset?.owner == currentAddress
        /*withContext(Dispatchers.IO) {
            try {
                Log.d("FINALIZE", "FINALIZE AUCTION FOR ASSET WITH ID $assetId")
                val contract = getContract()
                contract.finalizeAuction(assetId).send()
            } catch (e: Exception) {
                Log.e("Repo", "Failed to finalize auction", e)
            }
        }*/

    override suspend fun getBalance(address: String): BigDecimal = withContext(Dispatchers.IO) {
        try {
            Log.d("FetchBalance", " address   ::   $address ")
            val contract = getContract()
            Log.d("FetchBalance", " contract   ::   $contract ")
            Log.d("FetchBalance", " contract name   ::   ${contract.name().send()} ")
            Log.d("FetchBalance", " balanceOfTokens   ::   ${contract.balanceOf(address).send()} ")
            val balanceWei = getContract().getETHBalance(address).send()
            BigDecimal(balanceWei)
        } catch (e: Exception) {
            Log.e("EthereumRepo", "Get balance failed", e)
            BigDecimal.ZERO
        }
    }

    override suspend fun createTransaction(
        function: Function,
        value: BigInteger?
    ): EthereumRequest {
        val params = mutableMapOf(
            "from" to currentAddress,
            "to" to contractAddress,
            "data" to FunctionEncoder.encode(function)
        )

        value?.let {
            params["value"] = it.toString(16)
        }

        return EthereumRequest(
            method = EthereumMethod.ETH_SEND_TRANSACTION.value,
            params = listOf(params)
        )
    }

    override fun clearSession() {
        contract = null
        currentAddress = null
        ethereum.clearSession()
    }
}