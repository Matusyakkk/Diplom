package com.example.myapplication

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screen.details_asset.AssetDetailScreen
import com.example.myapplication.ui.screen.buyout_asset.BuyOutRoute
import com.example.myapplication.ui.screen.connect_wallet.WalletConnectRoute
import com.example.myapplication.ui.screen.create_asset.CreateAssetRoute
import com.example.myapplication.ui.screen.details_asset.AssetDetailRoute
import com.example.myapplication.ui.screen.list_assets.AssetsForSaleRoute
import com.example.myapplication.ui.screen.make_bid.MakeBidRoute
import com.example.myapplication.ui.screen.profile.ProfileRoute
import com.example.myapplication.ui.screen.sell_asset.ListAssetRoute
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val walletViewModel: WalletViewModel by viewModels()
    private val assetViewModel: AssetViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        walletViewModel.performLogout()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "walletConnect") {
        composable("walletConnect") {
            WalletConnectRoute(navController)
        }
        composable("itemList") {
            AssetsForSaleRoute(navController)
        }
        composable("createItem") {
            CreateAssetRoute(navController)
        }
        composable ("profile") {
            ProfileRoute(navController)
        }
        composable("listAsset/{assetId}") { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            ListAssetRoute(navController, assetId)
        }
        composable("makeBid/{assetId}") {backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            MakeBidRoute(navController, assetId)
        }
        composable("buyout/{assetId}") {backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            BuyOutRoute(navController, assetId)
        }
        composable("itemDetail/{assetId}") { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            AssetDetailRoute(navController, assetId)
        }
    }
}
