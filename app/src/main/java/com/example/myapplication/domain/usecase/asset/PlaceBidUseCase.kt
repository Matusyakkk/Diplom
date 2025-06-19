package com.example.myapplication.domain.usecase.asset

import android.util.Log
import com.example.myapplication.domain.reopsitory.EthereumRepository
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigInteger
import javax.inject.Inject
import org.web3j.abi.datatypes.Function
import io.metamask.androidsdk.Result

class PlaceBidUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(assetId: BigInteger, bidAmount: BigInteger): String {
        val function = Function(
            "placeBid",
            listOf(Uint256(assetId)),
            emptyList()
        )
        val request = ethereumRepo.createTransaction(function, bidAmount)
        val result = ethereumRepo.sendTransaction(request)

        return when (result) {
            is Result.Success.Item -> result.value // hash of transaction
            is Result.Error -> throw Exception("Bid failed: ${result.error.message}")
            else -> throw Exception("Unexpected result in bidding")
        }
    }
}