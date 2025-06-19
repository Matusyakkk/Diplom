package com.example.myapplication.ui.screen.buyout_asset

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.buyout_asset.components.BuyOutForm
import com.example.myapplication.ui.screen.components.TopBarWithBack

@Composable
fun BuyOutScreen(
    assetData: AssetData?,
    ethBuyoutPrice: String,
    ethBalance: String,
    canBuyOut: Boolean,
    onBack: () -> Unit,
    onBuyOutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        TopBarWithBack("Викупити", onBack)

        BuyOutForm(
            assetData,
            ethBuyoutPrice,
            ethBalance,
            canBuyOut,
            onBuyOutClick
        )
    }
}

