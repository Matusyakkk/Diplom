package com.example.myapplication.domain.usecase.asset

data class AssetUseCases(
    val getAssets: GetAssetsUseCase,
    val placeAssetBid: PlaceBidUseCase,
    val executeAssetBuyout: BuyoutUseCase,
    val createAndUploadAsset: CreateAssetUseCase,
    val listAssetForAuction: ListAssetUseCase,
    val finalizeAuction: FinalizeAuctionUseCase
)
