package com.example.myapplication.ui.screen.profile.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.utils.ComponentUtils.toIntOffset
import java.math.BigInteger

//Дані під інформацією профілю
@Composable
fun TabsInProfile(
    assetsListedByUser: List<AssetData>,
    assetsOwnedByUser: List<AssetData>,
    assetsByHighestBidder: List<AssetData>,
    onClickListAsset: (BigInteger) -> Unit,
    onClickDetails: (BigInteger) -> Unit
) {

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
    Column(modifier = Modifier.Companion.fillMaxSize()) {
        //кнопки управління
        Box {
            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    Column(
                        modifier = Modifier.Companion
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
                        horizontalAlignment = Alignment.Companion.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.primary else Color.Companion.Gray
                        )
                    }
                }
            }
            //лінія під текстом
            Box(
                modifier = Modifier.Companion
                    .offset(x = indicatorOffsetX + startPadding)
                    .padding(top = 36.dp)
                    .height(2.dp)
                    .width(indicatorWidth)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        HorizontalDivider(
            modifier = Modifier.Companion.padding(horizontal = 8.dp),
            thickness = 1.dp,
            color = Color.Companion.DarkGray
        )

        //Списки
        when (tabs[selectedTabIndex]) {
            "Мої предмети" -> TabContentGrid(assetsOwnedByUser, onClickListAsset)

            "На продажі" -> ListAssetRowOnSale(assetsListedByUser, onClickDetails)

            "Мої ставки" -> ListAssetRowOnSale(assetsByHighestBidder, onClickDetails)
        }
    }
}