package com.example.myapplication.ui.screen.sell_asset

import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.TimeUnit
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import com.example.myapplication.utils.ComponentUtils.replaceCommasWithDots
import com.example.myapplication.utils.ComponentUtils.validatePrice
import com.example.myapplication.utils.CryptoUtils
import java.math.BigDecimal

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAssetRoute(
    navController: NavController,
    assetId: String
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)

    NavigationEventEffect(assetViewModel.navigationManager, navController)

    val assetData = assetViewModel.findById(assetId.toBigInteger())
    val imageUri = remember(assetData) {
        Uri.fromFile(assetData?.imageFile)
    }

    var ethBuyout by remember { mutableStateOf("0") }
    var priceError by remember { mutableStateOf<String?>(null) }

    var selectedTime by remember { mutableIntStateOf(1) }
    var timeUnit by remember { mutableStateOf(TimeUnit.DAYS) }

    val auctionEndIn = remember(selectedTime, timeUnit) {
        when (timeUnit) {
            TimeUnit.MINUTES -> selectedTime * 60
            TimeUnit.HOURS -> selectedTime * 3600
            TimeUnit.DAYS -> selectedTime * 86400
        }
    }

    val isValidPrice = validatePrice(ethBuyout)

    ListAssetScreen(
        assetData = assetData,
        imageUri = imageUri,
        ethBuyout = ethBuyout,
        onPriceChange = { newValue ->
            if (newValue.matches(Regex("^\\d*([.,]?\\d*)?\$"))) {
                ethBuyout = newValue
                priceError = null
            }
        },
        priceError = priceError,
        selectedTime = selectedTime,
        timeUnit = timeUnit,
        onTimeChange = { selectedTime = it },
        onUnitChange = { timeUnit = it },
        isSubmitEnabled = isValidPrice,
        onSubmit = {
            if (!isValidPrice) {
                priceError = "Ціна викупу має бути більше 0"
            } else {
                priceError = null
                assetViewModel.listAssetForAuction(
                    assetId.toBigInteger(),
                    CryptoUtils.ethToWei(BigDecimal(replaceCommasWithDots(ethBuyout))),
                    auctionEndIn.toBigInteger()
                )
            }
        },
        onBack = { assetViewModel.navigateToProfile() }
    )
}
