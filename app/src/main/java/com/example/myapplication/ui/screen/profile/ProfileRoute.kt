package com.example.myapplication.ui.screen.profile


import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel

@Composable
fun ProfileRoute(
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    //val assetViewModel: AssetViewModel = hiltViewModel()
    //val walletViewModel: WalletViewModel = hiltViewModel()

    NavigationEventEffect(assetViewModel.navigationManager, navController)
    NavigationEventEffect(walletViewModel.navigationManager, navController)

    val address = walletViewModel.address

    val assetsOwned = remember(address) {
        assetViewModel.findAssetsOwnedByUser(address)
    }

    val assetsListed = remember(address) {
        assetViewModel.findAssetsListedByUser(address)
    }

    val assetsHighestBidder = remember(address) {
        assetViewModel.findAssetsByHighestBidder(address)
    }

    ProfileScreen(
        address = address,
        assetsOwnedByUser = assetsOwned,
        assetsListedByUser = assetsListed,
        assetsByHighestBidder = assetsHighestBidder,
        onBackClick = { assetViewModel.navigateToAssetsForSale() },
        onLogoutClick = { walletViewModel.performLogout() },
        onClickListAsset = { assetId -> assetViewModel.navigateToListAsset(assetId) },
        onClickDetails = { assetId -> assetViewModel.navigateToDetail(assetId) }
    )
}
