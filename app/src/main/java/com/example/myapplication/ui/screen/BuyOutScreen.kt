package com.example.myapplication.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel
import com.example.myapplication.viewmodel.ViewModel.NavigationEvent
import java.math.BigDecimal
import java.math.BigInteger

@Composable
fun BuyOutScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
) {
    // Спостерігаємо за подією навігації через StateFlow
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    // Якщо подія настане, виконуємо навігацію
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.GoToProfileScreen -> {
                navController.navigate("profile")
                viewModel.onEventHandled()  // Очищаємо подію після навігації
            }
            else -> Unit
        }
    }

    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())
    val context = LocalContext.current
    val weiBalance = viewModel.getBalance()
    val ethBalance = viewModel.weiToEth(weiBalance)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        BackButton(navController, "Викупити", "itemDetail/${assetId}")

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

            ItemInfo("Ціна", viewModel.weiToEth(BigDecimal(assetData?.buyoutPrice)).toString())

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                thickness = 1.dp,
                color = Color.DarkGray
            )

            ItemInfo("Баланс", ethBalance.toString())

            Button(
                onClick = {
                    if (weiBalance > BigDecimal(assetData?.buyoutPrice)) {
                        viewModel.buyout(
                            assetId.toBigInteger(),
                            assetData?.buyoutPrice ?: BigInteger("9999")
                        )
                    } else {
                        Toast.makeText(context, "Недостатньо коштів. Баланс: $ethBalance", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = viewModel.isUserOwnerOrHighestBidder(assetData),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Викупити",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
