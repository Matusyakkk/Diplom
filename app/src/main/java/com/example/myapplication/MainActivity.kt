package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screen.BuyOutScreen
import com.example.myapplication.ui.screen.ItemDetailScreen
import com.example.myapplication.ui.screen.ItemsForSaleScreen
import com.example.myapplication.ui.screen.MakeBidScreen
import com.example.myapplication.ui.screen.ProfileScreen
import com.example.myapplication.ui.screen.WalletConnectScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "walletConnect") {
        composable("walletConnect") {
            WalletConnectScreen(navController = navController) //TODO: Connected wallet???
        }
        composable("itemList") {
            ItemsForSaleScreen(navController = navController)
        }
        composable ("profile") {
            ProfileScreen(navController = navController)
        }
        composable("makeBid/{itemName}") {backStackEntry -> //TODO: change itemName to key
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            MakeBidScreen(itemName = itemName, navController = navController)
        }
        composable("buyout/{itemName}") {backStackEntry -> //TODO: change itemName to key
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            BuyOutScreen(itemName = itemName, navController = navController)
        }
        composable("itemDetail/{itemName}") { backStackEntry -> //TODO: change itemName to key
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            ItemDetailScreen(itemName = itemName, navController = navController)
        }
    }
}
