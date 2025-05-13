package com.example.myapplication.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import com.example.myapplication.R

@Composable
fun BuyOutScreen(
    itemName: String,
    navController: NavController
) {
    val item = Item2(
        name = itemName,
        description = "Опис предмета: $itemName",
        bid = 150,
        buyout = 700,
        imageUrl = "https://via.placeholder.com/300",
        auctionTime = 3600,
        owner = "JohnDoe",
        wallet = "0x1234567890abcdef",
        bidHistory = listOf(
            BidHistory(200, "0xabc123"),
            BidHistory(250, "0xdef456"),
            BidHistory(300, "0xghi789")
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        BackButton(navController, "Викупити")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, top = 16.dp, end = 8.dp)
                .background(
                    color = colorResource(R.color.ListBG),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {

            ImageNameRow(item)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            ItemInfo("Ціна", "5")

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            ItemInfo("Комісія", "0.05")

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            ItemInfo("Загалом", "5.05") //TODO: розрахунок від комісії та введеної ставки вище

            ActionBtn({ /**/ }, "Викупити")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
