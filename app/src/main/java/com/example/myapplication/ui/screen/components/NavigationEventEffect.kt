package com.example.myapplication.ui.screen.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.myapplication.ui.navigation.NavigationManager

@Composable
fun NavigationEventEffect(
    navigationManager: NavigationManager,
    navController: NavController
) {
    LaunchedEffect(navigationManager) {
        navigationManager.navigationEvent.collect { event ->
            if (event != null) {
                navController.navigate(event.route)
                navigationManager.onEventHandled()
            }
        }
    }
}