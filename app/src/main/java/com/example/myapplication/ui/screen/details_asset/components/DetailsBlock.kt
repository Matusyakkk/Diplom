package com.example.myapplication.ui.screen.details_asset.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.utils.ComponentUtils
import com.example.myapplication.utils.CryptoUtils
import kotlinx.coroutines.delay
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsBlock(asset: AssetData?, onEndAuction: () -> Unit) {
    var remainingTime by remember { mutableStateOf(0) }

    val ethHighestBid = CryptoUtils.weiToEth(asset?.highestBid).toPlainString()
    val ethBuyoutPrice = CryptoUtils.weiToEth(asset?.buyoutPrice).toPlainString()
    LaunchedEffect(asset?.auctionEndTime) {
        val auctionEnd = asset?.auctionEndTime ?: 0
        while (true) {
            val currentTime = Instant.now().epochSecond
            val diff = auctionEnd.toInt() - currentTime

            if (diff <= 0) {
                remainingTime = 0
                onEndAuction()
                break
            }

            remainingTime = diff.toInt()
            delay(1000)
        }
    }

    Column(modifier = Modifier.Companion.padding(8.dp)) {
        Text(
            text = "Час до завершення: ${ComponentUtils.formatTime(remainingTime)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))
        Text(text = "Ставка: $ethHighestBid ETH", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.Companion.height(8.dp))
        Text(text = "Викуп: $ethBuyoutPrice ETH", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.Companion.height(8.dp))
        LabelValueRow("Власник:", CryptoUtils.shortenAddress(asset?.owner.orEmpty()))
    }
}