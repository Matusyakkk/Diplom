package com.example.myapplication.ui.screen.details_asset

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import java.math.BigInteger

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssetDetailRoute(
    navController: NavController,
    assetId: String
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    NavigationEventEffect(assetViewModel.navigationManager, navController)
    NavigationEventEffect(walletViewModel.navigationManager, navController)

    val uiState by walletViewModel.uiState.collectAsState()
    val assetData = assetViewModel.findById(assetId.toBigInteger())

    val isConnected = uiState.isConnected
    val address = walletViewModel.address
    val isUserAllowed = assetViewModel.isUserOwnerOrHighestBidder(assetData, address)

    AssetDetailScreen(
        assetData = assetData,
        isConnected = isConnected,
        address = address,
        isUserAllowed = isUserAllowed,
        onConnectClick = { walletViewModel.performLogout() },
        onBidClick = { assetViewModel.navigateToMakeBid(BigInteger(assetId)) },
        onBuyoutClick = { assetViewModel.navigateToBuyOut(BigInteger(assetId)) },
        onBack = { assetViewModel.navigateToAssetsForSale() },
        onEndAuction = {
            assetViewModel.finalizeAuction(BigInteger(assetId))
            assetViewModel.navigateToAssetsForSale()
        }
    )
}
