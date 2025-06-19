package com.example.myapplication.ui.screen.make_bid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.data.model.BidState
import com.example.myapplication.ui.screen.components.BottomButton
import com.example.myapplication.ui.screen.components.ImageNameRow
import com.example.myapplication.ui.screen.components.ItemInfo
import com.example.myapplication.utils.ComponentUtils
import java.math.BigDecimal

@Composable
fun BidForm(
    assetData: AssetData?,
    ethBalance: String,
    ethHighestBid: String,
    onBidAmountChange: (String) -> Unit,
    onBidClick: () -> Unit,
    bidState: BidState,
    isUserAllowed: Boolean
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp, top = 16.dp, end = 8.dp)
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        ImageNameRow(assetData)

        BidInputField(
            ethBidAmount = bidState.ethAmount,
            onValueChange = onBidAmountChange,
            bidError = bidState.error
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))

        ItemInfo("Доступний баланс", ethBalance)
        Spacer(modifier = Modifier.Companion.height(8.dp))

        ItemInfo("Поточна ставка", ethHighestBid)

        BottomButton(
            enabled = isUserAllowed && ComponentUtils.validatePrice(bidState.ethAmount),
            isLoading = bidState.isLoading,
            text = "Зробити ставку",
            onClick = onBidClick
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))
    }
}