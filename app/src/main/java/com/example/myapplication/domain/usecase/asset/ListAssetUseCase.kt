package com.example.myapplication.domain.usecase.asset

import com.example.myapplication.domain.reopsitory.EthereumRepository
import io.metamask.androidsdk.Result
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import java.math.BigInteger
import javax.inject.Inject

class ListAssetUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(
        assetId: BigInteger,
        buyoutPrice: BigInteger,
        auctionEndTime: BigInteger): String {

        val function = Function(
            "listAssetForAuction",
            listOf(
                Uint256(assetId),
                Uint256(buyoutPrice),
                Uint256(auctionEndTime)
            ),
            emptyList()
        )
        val request = ethereumRepo.createTransaction(function)
        val result = ethereumRepo.sendTransaction(request)

        return when (result) {
            is Result.Success.Item -> result.value //hash
            else -> throw Exception("Listing asset failed")
        }
    }
}