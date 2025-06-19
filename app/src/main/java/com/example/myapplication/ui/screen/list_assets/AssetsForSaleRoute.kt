package com.example.myapplication.ui.screen.list_assets

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AssetsForSaleRoute(
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    NavigationEventEffect(walletViewModel.navigationManager, navController)
    NavigationEventEffect(assetViewModel.navigationManager, navController)

    LaunchedEffect(Unit) {
        assetViewModel.initializeIfNeeded()
    }

    val assets by assetViewModel.filteredAssets.collectAsState()
    val walletState by walletViewModel.uiState.collectAsState()
    val searchQuery by assetViewModel.searchQuery.collectAsState()

    AssetsForSaleScreen(
        assets = assets,
        searchQuery = searchQuery,
        isLoading = walletViewModel.isLoading,
        isConnected = walletState.isConnected,
        address = walletState.address,
        onQueryChange = assetViewModel::updateSearchQuery,
        onRefresh = assetViewModel::loadAssets,
        onItemClick = assetViewModel::navigateToDetail,
        onProfileClick = assetViewModel::navigateToProfile,
        onCreateClick = assetViewModel::navigateToCreateItem,
        onConnectClick = walletViewModel::performLogout
    )
}
