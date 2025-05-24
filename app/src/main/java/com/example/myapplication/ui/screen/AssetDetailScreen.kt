package com.example.myapplication.ui.screen

import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import coil3.compose.rememberAsyncImagePainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.time.Instant

// Сторінка деталей предмета
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AssetDetailScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())

    Scaffold(
        bottomBar = {
            if (uiState.walletConnected && !viewModel.address.isNullOrBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { navController.navigate("makeBid/${assetData?.assetId}") }, enabled = viewModel.isUserOwnerOrHighestBidder(assetData),
                        shape = RoundedCornerShape(16.dp),modifier = Modifier.weight(1f).height(64.dp).padding(bottom = 16.dp)) {
                        Text("Зробити ставку",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black)
                    }
                    Button(onClick = { navController.navigate("buyout/${assetData?.assetId}") }, enabled = viewModel.isUserOwnerOrHighestBidder(assetData),
                        shape = RoundedCornerShape(16.dp),modifier = Modifier.weight(1f).height(64.dp).padding(bottom = 16.dp)) {
                        Text("Викупити",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black)
                    }
                }
            } else {
                Button(
                    onClick = {
                        viewModel.resetWalletConnectUiState()
                        navController.navigate("walletConnect")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Підключити гманець",
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
                .padding(horizontal = 16.dp)
        ) {

            BackButton(navController, "Деталі предмета", "itemList")

            Spacer(modifier = Modifier.height(16.dp))

            ImageBlock(assetData)

            DetailsBlock(assetData, viewModel)

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun ImageBlock(asset: AssetData?) {
    val imageUri = Uri.fromFile(asset?.imageFile)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Asset Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        )
        Text(
            text = asset?.name ?: "NO Name",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = asset?.description ?: "No description",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsBlock(asset: AssetData?, viewModel: ViewModel) {
    var remainingTime by remember { mutableStateOf(0) }

    val ethHighestBid = viewModel.weiToEth(BigDecimal(asset?.highestBid))
    val ethBuyoutPrice = viewModel.weiToEth(BigDecimal(asset?.buyoutPrice))
    LaunchedEffect(asset?.auctionEndTime) {
        val auctionEnd = asset?.auctionEndTime ?: 0
        while (true) {
            val currentTime = Instant.now().epochSecond
            val diff = auctionEnd.toInt() - currentTime

            if (diff <= 0) {
                remainingTime = 0
                break
            }

            remainingTime = diff.toInt()
            Log.d("DETAILS_TIME","auction end at $auctionEnd")
            Log.d("DETAILS_TIME","current time is $currentTime")
            Log.d("DETAILS_TIME","auction and in $diff (difference)")
            Log.d("DETAILS_TIME","auction and in $remainingTime (remaining)")
            Log.d("DETAILS_TIME","...DELAY...")
            delay(1000)
        }
//        val currentTime = Instant.now().epochSecond // Поточний час в секундах
//        val diff = auctionEnd.toInt() - currentTime

//        // Щоб кожну секунду оновлювати час
//        while (diff > 0) {
//            remainingTime = diff.toInt()
//            delay(1000) // Затримка 1 секунда
//        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Час до завершення: ${formatTime(remainingTime)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Ставка: $ethHighestBid ETH", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Викуп: $ethBuyoutPrice ETH", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Власник: ",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = viewModel.shortenAddress(asset?.owner.toString()),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun BackButton(navController: NavController, text: String, to: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController.navigate(to) }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .padding(top = 3.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

// Форматування часу в форматі "hh:mm:ss"
fun formatTime(seconds: Int): String {
    val days = seconds / 86400  // Обчислюємо кількість днів (86400 секунд у дні)
    val hours = (seconds % 86400) / 3600  // Обчислюємо години
    val minutes = (seconds % 3600) / 60  // Обчислюємо хвилини
    val remainingSeconds = seconds % 60  // Обчислюємо залишкові секунди

    return String.format("%02d дн. %02d:%02d:%02d", days, hours, minutes, remainingSeconds)
}
