package com.example.myapplication.ui.screen.list_assets.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.ItemRow
import java.math.BigInteger

@Composable
fun AssetsList(assets: List<AssetData>, onItemClick: (BigInteger) -> Unit) {
    LazyColumn {
        items(assets.size) { index ->
            val asset = assets[index]
            ItemRow(onItemClick, asset)
        }
    }
}