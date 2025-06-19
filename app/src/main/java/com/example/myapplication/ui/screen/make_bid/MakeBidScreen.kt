package com.example.myapplication.ui.screen.make_bid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.data.model.BidState
import com.example.myapplication.ui.screen.components.TopBarWithBack
import com.example.myapplication.ui.screen.make_bid.components.BidForm
import java.math.BigDecimal

@Composable
fun MakeBidScreen(
    assetData: AssetData?,
    ethBalance: String,
    ethHighestBid: String,
    bidState: BidState,
    isUserAllowed: Boolean,
    onBidAmountChange: (String) -> Unit,
    onBidClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        TopBarWithBack("Підняти ставку", onBack)
        BidForm(
            assetData = assetData,
            ethBalance = ethBalance,
            ethHighestBid = ethHighestBid,
            onBidAmountChange = onBidAmountChange,
            onBidClick = onBidClick,
            bidState = bidState,
            isUserAllowed = isUserAllowed
        )
    }
}

