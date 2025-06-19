package com.example.myapplication.ui.screen.list_assets.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import java.math.BigInteger


@Composable
fun AssetListContent(
    innerPadding: PaddingValues,
    assets: List<AssetData>,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    isLoading: Boolean,
    onItemClick: (BigInteger) -> Unit,
    onProfileClick: () -> Unit,
    isConnected: Boolean,
    address: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(end = 16.dp, start = 16.dp)
    ) {
        TitleIcon("Предмети на продажі", R.drawable.ic_user, onProfileClick, isConnected, address)
        SearchBar(
            searchQuery = searchQuery,
            onQueryChange = onQueryChange,
            onRefreshClick = onRefresh
        )

        when {
            isLoading -> LoadingState()
            assets.isEmpty() -> EmptyState()
            else -> AssetsList(assets, onItemClick)
        }
    }
}
