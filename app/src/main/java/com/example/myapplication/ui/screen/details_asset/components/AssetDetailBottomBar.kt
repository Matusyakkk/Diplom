package com.example.myapplication.ui.screen.details_asset.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.screen.components.BottomButton

@Composable
fun AssetDetailBottomBar(
    isConnected: Boolean,
    address: String,
    onConnectClick: () -> Unit,
    onBidClick: () -> Unit,
    onBuyoutClick: () -> Unit,
    isUserAllowed: Boolean
) {
    if (isConnected && address.isNotBlank()) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BottomButton(
                enabled = isUserAllowed,
                text = "Зробити ставку",
                onClick = onBidClick,
                horizontalPadding = 0,
                modifier = Modifier.Companion.weight(1f)
            )
            BottomButton(
                text = "Викупити",
                onClick = onBuyoutClick,
                enabled = isUserAllowed,
                horizontalPadding = 0,
                modifier = Modifier.Companion.weight(1f)
            )
        }
    } else {
        BottomButton(
            text = "Підключити гаманець",
            onClick = onConnectClick,
        )
    }
}