package com.example.myapplication.ui.screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R

// Сторінка деталей предмета
@Composable
fun ItemDetailScreen(
    itemName: String,
    navController: NavController
) { //TODO: change itemName to key

    // Для демонстрації створенр фіктивний об'єкт Item.
    // В подальшому буде викорастоно модель або ViewModel.
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

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { navController.navigate("makeBid/${item.name}") },
                    shape = RoundedCornerShape(16.dp),modifier = Modifier.weight(1f).height(64.dp).padding(bottom = 16.dp)) {
                    Text("Зробити ставку",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
                }
                Button(onClick = { navController.navigate("buyout/${item.name}") },
                    shape = RoundedCornerShape(16.dp),modifier = Modifier.weight(1f).height(64.dp).padding(bottom = 16.dp)) {
                    Text("Викупити",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black)
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

            BackButton(navController, "Деталі предмета")

            Spacer(modifier = Modifier.height(16.dp))

            ImageBlock(item)

            DetailsBlock(item)

            Spacer(modifier = Modifier.height(16.dp))

        }
    }

}

@Composable
fun BidHistoryList(bidHistory: List<BidHistory>) {
    Column(modifier = Modifier.fillMaxWidth()/*.verticalScroll(rememberScrollState())*/) {
        bidHistory.forEach { bid ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Ставка: ${bid.amount} - Гаманець: ${bid.wallet}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class Item2(
    val name: String,
    val description: String,
    val bid: Int,
    val buyout: Int,
    val imageUrl: String,
    val auctionTime: Int,
    val owner: String,
    val wallet: String,
    val bidHistory: List<BidHistory> // Історія ставок
)

data class BidHistory(
    val amount: Int,
    val wallet: String
)

@Composable
fun ImageBlock(item: Item2) {
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
            painter = painterResource(id = R.drawable.nft12),
            contentDescription = "Item Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp, start = 16.dp)
        )
    }
}

@Composable
fun DetailsBlock(item: Item2) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "Час до завершення аукціону: ${formatTime(item.auctionTime)}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Bid: ${item.bid}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Buyout: ${item.buyout}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Owner: ${item.owner}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.wallet,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }

        // Кнопка для показу історії ставок
/*        Spacer(modifier = Modifier.height(16.dp))
        var showBidHistory by remember { mutableStateOf(false) }
        //HistoryButton(text = "Історія ставок", onClick = { showBidHistory = !showBidHistory })
        Button(
            onClick = { showBidHistory = !showBidHistory },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Історія ставок")
        }

        // Показуємо історію ставок, якщо потрібно
        if (showBidHistory) {
            BidHistoryList(bidHistory = item.bidHistory)
        }*/
    }
}

@Composable
fun BackButton(navController: NavController, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
            //.padding(8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
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
@Composable
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

