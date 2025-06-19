package com.example.myapplication.ui.screen.buyout_asset.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.BottomButton
import com.example.myapplication.ui.screen.components.ImageNameRow
import com.example.myapplication.ui.screen.components.ItemInfo

@Composable
fun BuyOutForm(
    assetData: AssetData?,
    ethBuyout: String,
    ethBalance: String,
    canBuyOut: Boolean,
    onBuyOutClick: () -> Unit,
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

        HorizontalDivider(
            modifier = Modifier.Companion.padding(vertical = 8.dp, horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.Companion.DarkGray
        )

        ItemInfo("Ціна", ethBuyout)

        HorizontalDivider(
            modifier = Modifier.Companion.padding(vertical = 8.dp, horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.Companion.DarkGray
        )

        ItemInfo("Баланс", ethBalance)

        BottomButton(
            enabled = canBuyOut,
            text = "Викупити" ,
            onClick = onBuyOutClick,
        )
        Spacer(modifier = Modifier.Companion.height(8.dp))
    }
}