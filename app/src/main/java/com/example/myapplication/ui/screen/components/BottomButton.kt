package com.example.myapplication.ui.screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomButton(
    text: String? = null,
    isConnected: Boolean? = null,
    onClick: (() -> Unit)? = null,
    onCreateClick: (() -> Unit)? = null,
    onConnectClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    horizontalPadding: Int = 16,
    modifier: Modifier = Modifier
) {
    val displayText = when {
        text != null -> text
        isConnected == true -> "Створити своє NFT"
        isConnected == false -> "Підключити гаманець"
        else -> ""
    }

    val action: (() -> Unit)? = when {
        onClick != null -> onClick
        isConnected == true -> onCreateClick
        isConnected == false -> onConnectClick
        else -> null
    }

    Box(modifier = modifier.padding(bottom = 16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        } else {
            Button(
                onClick = { action?.invoke() },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = horizontalPadding.dp, vertical = 8.dp),//vertical8
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = displayText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}