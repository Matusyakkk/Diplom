package com.example.myapplication.ui.screen.sell_asset.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PriceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        isError = error != null,
        shape = RoundedCornerShape(12.dp),
        label = { Text("Ціна викупу в ETH") }
    )
    if (error != null) {
        Text(
            text = error,
            color = Color.Companion.Red,
            fontSize = 12.sp,
            modifier = Modifier.Companion.padding(start = 16.dp, top = 4.dp)
        )
    }

}