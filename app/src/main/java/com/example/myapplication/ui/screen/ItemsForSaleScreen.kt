package com.example.myapplication.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
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
import com.example.myapplication.data.TestItem
import com.example.myapplication.viewmodel.ViewModel


@Composable
fun ItemsForSaleScreen(
    viewModel: ViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val items by viewModel.parsedNFTs.collectAsState(initial = emptyList())
    LaunchedEffect(Unit) {
        viewModel.fetchNftData()
    }
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (uiState.walletConnected) {
                    Button(
                        onClick = {
                            navController.navigate("createItem")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Створити своє NFT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
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
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(end = 16.dp, start = 16.dp)
        ) {
            TitleIcon("Предмети на продаж", R.drawable.ic_user, navController, viewModel)

            LazyColumn {
                items(items.size) { index ->
                    val item = items[index]
                    ItemRow(item = item, navController)
                }
            }
        }
    }
}

// Компонент для відображення предмета в списку
@Composable
fun ItemRow(item: TestItem, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .background(color = colorResource(R.color.ListBG), shape = RoundedCornerShape(18.dp))
    ) {
        if (!expanded) {
            Row() {
                Image(
                    painter = painterResource(id = R.drawable.nft12),
                    contentDescription = "Item Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.name,
                    fontSize = 36.sp,
                    modifier = Modifier.padding(top = 14.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.clip(RoundedCornerShape(18.dp))) {
                Image(
                    painter = painterResource(id = R.drawable.nft12),
                    contentDescription = "Item Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable { navController.navigate("itemDetail/${item.name}") }
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
    }
}

@Composable
fun TitleIcon(text: String, imageID: Int, navController: NavController, viewModel: ViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        if (uiState.walletConnected) {
            IconButton(onClick = { navController.navigate("profile") }) {
                Image(
                    painter = painterResource(id = imageID),
                    contentDescription = "Перехід на профіль"
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}