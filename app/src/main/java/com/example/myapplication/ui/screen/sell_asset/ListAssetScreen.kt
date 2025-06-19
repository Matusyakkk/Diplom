package com.example.myapplication.ui.screen.sell_asset

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.data.model.TimeUnit
import com.example.myapplication.ui.screen.components.BottomButton
import com.example.myapplication.ui.screen.components.TopBarWithBack
import com.example.myapplication.ui.screen.sell_asset.components.AssetDetailCard
import com.example.myapplication.ui.screen.sell_asset.components.AuctionDurationSlider
import com.example.myapplication.ui.screen.sell_asset.components.BottomBarButton
import com.example.myapplication.ui.screen.sell_asset.components.PriceInputField

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAssetScreen(
    assetData: AssetData?,
    imageUri: Uri?,
    ethBuyout: String,
    onPriceChange: (String) -> Unit,
    priceError: String?,
    selectedTime: Int,
    timeUnit: TimeUnit,
    onTimeChange: (Int) -> Unit,
    onUnitChange: (TimeUnit) -> Unit,
    isSubmitEnabled: Boolean,
    onSubmit: () -> Unit,
    onBack: () -> Unit
){
    Scaffold(
        bottomBar = {
            BottomButton(/*BottomBarButton()*/
                text = "Створити аукціон",
                onClick = onSubmit,
                enabled = isSubmitEnabled
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            TopBarWithBack("Виставити на продаж актив", onBack)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, top = 16.dp, end = 8.dp)
                    .background(
                        color = colorResource(R.color.ListBG),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                AssetDetailCard(assetData, imageUri)

                Spacer(modifier = Modifier.height(8.dp))

                PriceInputField(
                    value = ethBuyout,
                    onValueChange = onPriceChange,
                    error = priceError
                )

                Spacer(modifier = Modifier.height(8.dp))

                AuctionDurationSlider(
                    selectedTime = selectedTime,
                    onUnitChange = onUnitChange,
                    timeUnit = timeUnit,
                    onTimeChange = onTimeChange
                )
            }
        }
    }
}