package com.example.myapplication.ui.navigation

sealed class NavigationEvent(val route: String) {
    object GoToWalletConnectScreen : NavigationEvent("walletConnect")
    object GoToAssetsForSaleScreen : NavigationEvent("itemList")
    object GoToCreateAssetScreen : NavigationEvent("createItem")
    object GoToProfileScreen : NavigationEvent("profile")
    data class GoToAssetDetailScreen(val assetId: String) :
        NavigationEvent("itemDetail/$assetId")

    data class GoToListAssetScreen(val assetId: String) :
        NavigationEvent("listAsset/$assetId")

    data class GoToMakeBidScreen(val assetId: String) :
        NavigationEvent("makeBid/$assetId")

    data class GoToBuyOutScreen(val assetId: String) :
        NavigationEvent("buyout/$assetId")
}