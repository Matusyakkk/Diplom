package com.example.myapplication.ui.screen.list_assets

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.BottomButton
import com.example.myapplication.ui.screen.list_assets.components.AssetListContent
import java.math.BigInteger

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AssetsForSaleScreen(
    assets: List<AssetData>,
    searchQuery: String,
    isLoading: Boolean,
    isConnected: Boolean,
    address: String?,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onItemClick: (BigInteger) -> Unit,
    onProfileClick: () -> Unit,
    onCreateClick: () -> Unit,
    onConnectClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomButton(
                isConnected = isConnected,
                onCreateClick = onCreateClick,
                onConnectClick = onConnectClick
            )
        }
    ) { innerPadding ->
        AssetListContent(
            innerPadding = innerPadding,
            assets = assets,
            searchQuery = searchQuery,
            onQueryChange = onQueryChange,
            onRefresh = onRefresh,
            isLoading = isLoading,
            onItemClick = onItemClick,
            onProfileClick = onProfileClick,
            isConnected = isConnected,
            address = address
        )
    }
}