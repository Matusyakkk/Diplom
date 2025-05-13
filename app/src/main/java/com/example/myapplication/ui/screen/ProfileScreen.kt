package com.example.myapplication.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.viewmodel.ViewModel
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(
    viewModel: ViewModel,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //Header
        TopBar(viewModel, navController)

        //Інформація профілю
        ProfileInformation(viewModel)

        //Дані під інформацією профілю
        TabsInProfile(navController)

    }
}

//Header
@Composable
fun TopBar(viewModel: ViewModel, navController: NavController? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { navController?.popBackStack() }) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Профіль",
                fontSize = 28.sp,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                viewModel.logOut()
                navController?.navigate("walletConnect")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Перехід на профіль"
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

//Інформація профілю
@Composable
fun ProfileInformation(viewModel: ViewModel) {
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
            painter = painterResource(id = R.drawable.nft1337),
            contentDescription = "Profile Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Jake",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = viewModel.address,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        }
    }
}

//Дані під інформацією профілю
@Composable
fun TabsInProfile(navController: NavController) {
    val sampleItems = listOf(
        Item3("Собака", "Чудовий песик", 5, R.drawable.nft12),
        Item3("Кіт", "Муркотик", 5, R.drawable.nft13),
        Item3("Папуга", "Говорить більше за тебе", 5, R.drawable.nft1337),
        Item3("Кіт", "Муркотик", 5, R.drawable.nft13),
        Item3("Собака", "Чудовий песик", 5, R.drawable.nft12),
        Item3("Кіт", "Муркотик", 5, R.drawable.nft13),
        Item3("Папуга", "Говорить більше за тебе", 5, R.drawable.nft1337),
        Item3("Кіт", "Муркотик", 5, R.drawable.nft13),
        Item3("Папуга", "Говорить більше за тебе", 5, R.drawable.nft1337)
    )

    val tabs = listOf("Мої предмети", "На продажі", "Мої ставки")
    var selectedTabIndex by remember { mutableStateOf(0) }
    val startPadding = 16.dp
    val tabPositions = remember { mutableStateListOf<Pair<IntOffset, Int>>() }

    val indicatorOffsetX by animateDpAsState(
        targetValue = if (tabPositions.size > selectedTabIndex)
            with(LocalDensity.current) { tabPositions[selectedTabIndex].first.x.toDp() }
        else 0.dp,
        label = "indicator_offset"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = if (tabPositions.size > selectedTabIndex)
            with(LocalDensity.current) { tabPositions[selectedTabIndex].second.toDp() }
        else 0.dp,
        label = "indicator_width"
    )

    //інформація під профілем
    Column(modifier = Modifier.fillMaxSize()) {
        //кнопки управління
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Column(
                        modifier = Modifier
                            .wrapContentWidth()
                            .onGloballyPositioned { coords ->
                                val position = coords.positionInParent().toIntOffset()
                                val width = coords.size.width
                                if (tabPositions.size <= index) {
                                    tabPositions.add(position to width)
                                } else {
                                    tabPositions[index] = position to width
                                }
                            }
                            .clickable { selectedTabIndex = index }
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            }
            //лінія під текстом
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffsetX + startPadding)
                    .padding(top = 36.dp) // Відступ від тексту
                    .height(2.dp)
                    .width(indicatorWidth)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.DarkGray
        )

        //Списки
        when (tabs[selectedTabIndex]) {
            "Мої предмети" -> {
                TabContentGrid(items3 = sampleItems)
            }

            "На продажі" -> {
                ListItemRowOnSale(items3 = sampleItems, navController)
            }

            "Мої ставки" -> {
                ListItemRowOnSale(items3 = sampleItems, navController)
            }
        }
    }
}


//Мої предмети
@Composable
fun TabContentGrid(items3: List<Item3>, navController: NavController? = null) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items3.size) { index ->
            val item = items3[index]
            ItemCard(item = item, navController = navController)
        }
    }
}

@Composable
fun ItemCard(item: Item3, navController: NavController? = null) {
    Column(
        modifier = Modifier
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
            .clip(RoundedCornerShape(18.dp))
            .clickable {
                navController?.navigate("itemDetail/${item.name}")
            }
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = "Item Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // ⬅️ нижча, щоб вміщувалось більше
        )
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
        )
    }
}


//На продажі ТА Мої Ставки
@Composable
fun ListItemRowOnSale(items3: List<Item3>, navController: NavController? = null){
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(items3.size) { index ->
            val item = items3[index]
            ItemRowOnSale(item = item, navController)
        }
    }
}

@Composable
fun ItemRowOnSale(item: Item3, navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = colorResource(R.color.ListBG), shape = RoundedCornerShape(18.dp))
    ) {
        Row(modifier = Modifier
            .clickable {navController?.navigate("itemDetail/${item.name}")}) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "Item Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(75.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                    //.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = item.name,
                    fontSize = 36.sp,
                    modifier = Modifier.padding(top = 14.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${item.bidAmount} ETH",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(top = 14.dp, end = 16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}


data class Item3(
    val name: String,
    val description: String,
    val bidAmount: Int,
    @DrawableRes val imageRes: Int
)

fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())