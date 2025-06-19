package com.example.myapplication.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.screen.profile.components.ProfileInformation
import com.example.myapplication.ui.screen.profile.components.TabsInProfile
import com.example.myapplication.ui.screen.profile.components.TopBar
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import java.math.BigInteger

@Composable
fun ProfileScreen(
    address: String,
    assetsOwnedByUser: List<AssetData>,
    assetsListedByUser: List<AssetData>,
    assetsByHighestBidder: List<AssetData>,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClickListAsset: (BigInteger) -> Unit,
    onClickDetails: (BigInteger) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        //Header
        TopBar(
            onBackClick = onBackClick,
            onLogoutCLick = onLogoutClick,
            ableLogout = address.isNotBlank()
        )

        //Інформація профілю
        ProfileInformation(address)

        //Дані під інформацією профілю
        TabsInProfile(
            assetsOwnedByUser = assetsOwnedByUser,
            assetsListedByUser = assetsListedByUser,
            assetsByHighestBidder = assetsByHighestBidder,
            onClickListAsset = onClickListAsset,
            onClickDetails = onClickDetails
        )
    }
}