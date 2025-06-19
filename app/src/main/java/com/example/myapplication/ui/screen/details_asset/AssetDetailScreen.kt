package com.example.myapplication.ui.screen.details_asset

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.TopBarWithBack
import com.example.myapplication.ui.screen.details_asset.components.AssetDetailBottomBar
import com.example.myapplication.ui.screen.details_asset.components.DetailsBlock
import com.example.myapplication.ui.screen.details_asset.components.ImageBlock

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssetDetailScreen(
    assetData: AssetData?,
    isConnected: Boolean,
    address: String,
    isUserAllowed: Boolean,
    onConnectClick: () -> Unit,
    onBidClick: () -> Unit,
    onBuyoutClick: () -> Unit,
    onBack: () -> Unit,
    onEndAuction: () -> Unit
) {
    Scaffold(
        bottomBar = {
            AssetDetailBottomBar(
                isConnected = isConnected,
                address = address,
                onConnectClick = onConnectClick,
                onBidClick = onBidClick,
                onBuyoutClick = onBuyoutClick,
                isUserAllowed = isUserAllowed
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            TopBarWithBack("Деталі предмета", onBack)

            Spacer(modifier = Modifier.height(16.dp))

            ImageBlock(assetData)

            DetailsBlock(assetData, onEndAuction)

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}