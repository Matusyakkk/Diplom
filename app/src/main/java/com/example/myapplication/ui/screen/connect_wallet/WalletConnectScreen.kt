package com.example.myapplication.ui.screen.connect_wallet

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.WalletConnectUiState
import com.example.myapplication.ui.navigation.NavigationEvent
import kotlinx.coroutines.flow.StateFlow


@Composable
fun WalletConnectScreen(
    uiState: WalletConnectUiState,
    isLoading: Boolean,
    onConnectWallet: () -> Unit,
    onProceedWithoutWallet: () -> Unit,
    onClearError: () -> Unit,
    navigationEvents: StateFlow<NavigationEvent?>,
    onNavigationHandled: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(
                context,
                error.message ?: "Сталася невідома помилка",
                Toast.LENGTH_LONG
            ).show()

            onClearError()
        }
    }

    LaunchedEffect(Unit) {
        navigationEvents.collect { event ->
            if (event != null) {
                onNavigate(event.route)
                onNavigationHandled()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 164.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Підключіть гаманець",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Для доступу до повного функціоналу програми потрібно приєднати гаманець",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onConnectWallet,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(96.dp)
                ) {
                    Text(
                        "Підключити гаманець",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                TextButton(
                    onClick = onProceedWithoutWallet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Продовжити без гаманця",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
