package com.example.myapplication.domain.usecase.asset

import android.util.Log
import com.example.myapplication.domain.reopsitory.EthereumRepository
import javax.inject.Inject
import io.metamask.androidsdk.Result
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.Function

class MintAssetUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(metadataUri: String): String {
        val function = Function(
            "mintAsset",
            listOf(Utf8String(metadataUri)),
            emptyList()
        )
        val request = ethereumRepo.createTransaction(function)
        val result = ethereumRepo.sendTransaction(request)

        return when (result) {
            is Result.Success.Item -> result.value //hash
            else -> throw Exception("Minting failed")
        }
    }
}