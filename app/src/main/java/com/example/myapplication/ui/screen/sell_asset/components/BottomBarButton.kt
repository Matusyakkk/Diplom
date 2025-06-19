package com.example.myapplication.ui.screen.sell_asset.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBarButton(
    onSubmit: () -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onSubmit,
            enabled = isEnabled,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Виставити на продаж",
                fontSize = 16.sp,
                fontWeight = FontWeight.Companion.Bold,
                color = Color.Companion.Black
            )
        }
    }
}