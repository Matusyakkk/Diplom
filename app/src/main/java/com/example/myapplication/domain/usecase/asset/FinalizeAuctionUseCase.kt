package com.example.myapplication.domain.usecase.asset

import com.example.myapplication.data.model.AssetData
import com.example.myapplication.domain.reopsitory.EthereumRepository
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import javax.inject.Inject

class FinalizeAuctionUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(asset: AssetData?) {
        if(ethereumRepo.isOwnerOrBidder(asset)){
            val function = Function(
                "finalizeAuction",
                listOf(Uint256(asset?.assetId)),
                emptyList()
            )
            val request = ethereumRepo.createTransaction(function)
            ethereumRepo.sendTransaction(request)
        }
    }
}