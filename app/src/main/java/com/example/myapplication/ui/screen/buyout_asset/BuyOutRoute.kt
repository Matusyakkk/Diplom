package com.example.myapplication.ui.screen.buyout_asset

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import com.example.myapplication.utils.CryptoUtils
import java.math.BigInteger
import java.math.RoundingMode

@Composable
fun BuyOutRoute(
    navController: NavController,
    assetId: String
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    val context = LocalContext.current

    NavigationEventEffect(assetViewModel.navigationManager, navController)
    NavigationEventEffect(walletViewModel.navigationManager, navController)

    LaunchedEffect(Unit) {
        walletViewModel.fetchWalletBalance()
    }
    val walletUiState by walletViewModel.uiState.collectAsState()
    val weiBalance = walletUiState.balance
    val ethBalance = CryptoUtils.weiToEth(weiBalance).setScale(4, RoundingMode.DOWN).toPlainString()

    val assetData = assetViewModel.findById(assetId.toBigInteger())
    val ethBuyoutPrice = CryptoUtils.weiToEth(assetData?.buyoutPrice).toPlainString()
    val canBuyOut = assetViewModel.isUserOwnerOrHighestBidder(assetData, walletViewModel.address)

    BuyOutScreen(
        assetData = assetData,
        ethBuyoutPrice = ethBuyoutPrice,
        ethBalance = ethBalance,
        canBuyOut = canBuyOut,
        onBack = { assetViewModel.navigateToProfile() },
        onBuyOutClick = {
            val buyoutPrice = assetData?.buyoutPrice
            if (buyoutPrice != null && weiBalance > buyoutPrice) {
                assetViewModel.executeAssetBuyout(assetId.toBigInteger(), buyoutPrice)
            } else {
                Toast.makeText(
                    context,
                    "Недостатньо коштів. Баланс: $ethBalance",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )
}
