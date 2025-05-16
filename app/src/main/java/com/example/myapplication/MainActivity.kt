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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screen.BuyOutScreen
import com.example.myapplication.ui.screen.CreateAssetScreen
import com.example.myapplication.ui.screen.AssetDetailScreen
import com.example.myapplication.ui.screen.AssetsForSaleScreen
import com.example.myapplication.ui.screen.ListAssetScreen
import com.example.myapplication.ui.screen.MakeBidScreen
import com.example.myapplication.ui.screen.ProfileScreen
import com.example.myapplication.ui.screen.WalletConnectScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.viewmodel.ViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.logOut()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(viewModel: ViewModel = hiltViewModel(), modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "walletConnect") {
        composable("walletConnect") {
            WalletConnectScreen(viewModel, navController)
        }
        composable("itemList") {
            AssetsForSaleScreen(viewModel, navController)
        }
        composable("createItem") {
            CreateAssetScreen(viewModel, navController)
        }
        composable ("profile") {
            ProfileScreen(viewModel, navController)
        }
        composable("listAsset/{assetId}") { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            ListAssetScreen(viewModel, assetId, navController)
        }
        composable("makeBid/{assetId}") {backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            MakeBidScreen(viewModel, assetId, navController)
        }
        composable("buyout/{assetId}") {backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            BuyOutScreen(viewModel, assetId, navController)
        }
        composable("itemDetail/{assetId}") { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString("assetId") ?: ""
            AssetDetailScreen(viewModel, assetId, navController)
        }
    }
}
