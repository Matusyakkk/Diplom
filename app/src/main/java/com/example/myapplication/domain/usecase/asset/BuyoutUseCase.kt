package com.example.myapplication.domain.usecase.asset

import android.util.Log
import com.example.myapplication.domain.reopsitory.EthereumRepository
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigInteger
import io.metamask.androidsdk.Result
import javax.inject.Inject

class BuyoutUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(assetId: BigInteger, amount: BigInteger): String {
        val function = Function(
            "buyout",
            listOf(Uint256(assetId)),
            emptyList()
        )
        val request = ethereumRepo.createTransaction(function, amount)
        val result = ethereumRepo.sendTransaction(request)

        return when (result) {
            is Result.Success.Item -> result.value // transaction hash
            is Result.Error -> throw Exception("Buyout failed: ${result.error.message}")
            else -> throw Exception("Unexpected result")
        }
    }
}