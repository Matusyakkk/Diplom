package com.example.myapplication.domain.reopsitory

import com.example.myapplication.contract.MyTokenizedAssets
import com.example.myapplication.contract.MyTokenizedAssets.Asset
import com.example.myapplication.data.model.AssetData
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Result
import java.math.BigDecimal
import java.math.BigInteger
import org.web3j.abi.datatypes.Function

interface EthereumRepository {
    suspend fun connectWallet(): String?
    suspend fun getContract(): MyTokenizedAssets
    suspend fun createTransaction(function: Function, value: BigInteger? = null): EthereumRequest
    suspend fun sendTransaction(request: EthereumRequest): Result
    suspend fun getAssets(): List<Asset>
    suspend fun getBalance(address: String): BigDecimal
    suspend fun isOwnerOrBidder(asset: AssetData?): Boolean
    fun clearSession()
}