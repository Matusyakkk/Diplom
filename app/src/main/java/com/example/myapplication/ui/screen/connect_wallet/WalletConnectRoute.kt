package com.example.myapplication.ui.screen.connect_wallet

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.ui.viewmodel.WalletViewModel

@Composable
fun WalletConnectRoute(
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    //val walletViewModel: WalletViewModel = hiltViewModel()

    val uiState by walletViewModel.uiState.collectAsState()
    val isLoading = walletViewModel.isLoading

    WalletConnectScreen(
        uiState = uiState,
        isLoading = isLoading,
        onConnectWallet = { walletViewModel.connectWallet() },
        onProceedWithoutWallet = { walletViewModel.proceedWithoutWallet() },
        onClearError = { walletViewModel.clearError() },
        navigationEvents = walletViewModel.navigationManager.navigationEvent,
        onNavigationHandled = { walletViewModel.navigationManager.onEventHandled() },
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    )
}
