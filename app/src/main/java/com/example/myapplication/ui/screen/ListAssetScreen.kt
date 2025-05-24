package com.example.myapplication.ui.screen

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel
import com.example.myapplication.viewmodel.ViewModel.NavigationEvent
import java.math.BigDecimal

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAssetScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
){
    // Спостерігаємо за подією навігації через StateFlow
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Якщо подія настане, виконуємо навігацію
    LaunchedEffect(navigationEvent) {
        when (navigationEvent) {
            is NavigationEvent.GoToAssetDetailScreen -> {
                navController.navigate("itemDetail/${assetId}")
                viewModel.onEventHandled()  // Очищаємо подію після навігації
            }
            else -> Unit
        }
    }
    val context = LocalContext.current
    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())
    var ethBuyuot by remember { mutableStateOf("0") }
    var priceError by remember { mutableStateOf<String?>(null) }
    val imageUri = Uri.fromFile(assetData?.imageFile)

    var selectedTime by remember { mutableIntStateOf(1) }  // Тривалість аукціону в днях
    val auctionEndTimeBigInt = selectedTime * 86400

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        if (!validatePrice(ethBuyuot)) {
                            priceError = "Ціна викупу має бути більше 0"
                        } else {
                            priceError = null
                            viewModel.listAssetForAuction(
                                assetId.toBigInteger(),
                                viewModel.ethToWei(BigDecimal(replaceCommasWithDots(ethBuyuot))).toBigInteger(),
                                auctionEndTimeBigInt.toBigInteger()
                            )
                        }
                    },
                    enabled = validatePrice(ethBuyuot),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Виставити на продаж",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            BackButton(navController, "Виставити на продаж актив", "profile")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, top = 16.dp, end = 8.dp)
                    .background(
                        color = colorResource(R.color.ListBG),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = assetData?.name,
                    modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = assetData?.name.toString(),
                    fontSize = 36.sp,
                    modifier = Modifier.padding(top = 14.dp, start = 14.dp, end = 14.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = assetData?.description.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
                    color = Color.Gray
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ethBuyuot,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*([.,]?\\d*)?\$"))) {
                            ethBuyuot = it
                            priceError = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    isError = priceError != null,
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Ціна викупу в ETH") }
                )
                if (priceError != null) {
                    Text(
                        text = priceError ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Тривалість аукціону (в днях):",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )
                Slider(
                    value = selectedTime.toFloat(),
                    onValueChange = {
                        Log.i("LISTING","selected time $it and ${it.toInt()}")
                        selectedTime = it.toInt()
                                    },
                    valueRange = 1f..30f,  // Вибір від 1 до 30 днів
                    steps = 29,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text("Вибрано днів: $selectedTime", modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}

fun replaceCommasWithDots(input: String): String {
    return input.replace(',', '.')
}

// Функція валідації
fun validatePrice(input: String): Boolean {
    val cleanInput = input.replace(',', '.')
    return try {
        val price = BigDecimal(cleanInput)
        price > BigDecimal.ZERO
    } catch (e: Exception) {
        false
    }
}