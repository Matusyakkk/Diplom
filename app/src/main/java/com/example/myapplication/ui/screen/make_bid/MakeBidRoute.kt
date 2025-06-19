package com.example.myapplication.ui.screen.make_bid

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.BidState
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import com.example.myapplication.utils.ComponentUtils.replaceCommasWithDots
import com.example.myapplication.utils.ComponentUtils.validateBid
import com.example.myapplication.utils.CryptoUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

@Composable
fun MakeBidRoute(
    navController: NavController,
    assetId: String
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)
    val walletViewModel: WalletViewModel = hiltViewModel(activity)

    NavigationEventEffect(assetViewModel.navigationManager, navController)
    NavigationEventEffect(walletViewModel.navigationManager, navController)

    LaunchedEffect(Unit) {
        walletViewModel.fetchWalletBalance()
    }

    val walletUiState by walletViewModel.uiState.collectAsState()
    val weiBalance = walletUiState.balance
    val assetData = assetViewModel.findById(assetId.toBigInteger())
    val userAddress = walletViewModel.address

    var bidState by remember { mutableStateOf(BidState()) }

    val bidWei = remember(bidState.ethAmount) {
        if (bidState.ethAmount.isBlank()) BigInteger.ZERO
        else CryptoUtils.ethToWei(BigDecimal(replaceCommasWithDots(bidState.ethAmount)))
    }

    val ethHighestBid = CryptoUtils.weiToEth(assetData?.highestBid).toPlainString()
    val ethBalance = CryptoUtils.weiToEth(weiBalance).setScale(4, RoundingMode.DOWN).toPlainString()

    MakeBidScreen(
        assetData = assetData,
        ethBalance = ethBalance,
        ethHighestBid = ethHighestBid,
        bidState = bidState,
        isUserAllowed = assetViewModel.isUserOwnerOrHighestBidder(assetData, userAddress),
        onBidAmountChange = {
            if (it.matches(Regex("^\\d*([.,]?\\d*)?\$")))
                bidState = bidState.copy(ethAmount = it, error = null) },
        onBidClick = {
            bidState = bidState.copy(isLoading = true)
            val error = validateBid(
                bidWei,
                assetData?.highestBid ?: BigInteger.ZERO,
                assetData?.buyoutPrice ?: BigInteger.ZERO,
                weiBalance
            )
            if (error == null) {
                assetViewModel.placeAssetBid(assetId.toBigInteger(), bidWei)
            } else {
                bidState = bidState.copy(error = error, isLoading = false)
            }
        },
        onBack = { assetViewModel.navigateToDetail(assetId.toBigInteger()) }
    )
}
