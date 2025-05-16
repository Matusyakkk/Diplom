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
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel
import java.math.BigInteger

@Composable
fun BuyOutScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
) {
    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())

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

            ImageNameRow(assetData)

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            ItemInfo("Ціна", assetData?.buyoutPrice.toString())

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            TODO("Комісія???")
            ItemInfo("Комісія", "1")

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            TODO("розрахунок від комісії та введеної ставки вище")
            ItemInfo("Загалом", assetData?.buyoutPrice?.add(BigInteger("1")).toString())

            ActionBtn({
                viewModel.buyout(assetId.toBigInteger(),
                assetData?.buyoutPrice ?: BigInteger("9999"))
                TODO("EVENT_LISTENER:: Event switch screen ??? or nav")
                }, "Викупити")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
