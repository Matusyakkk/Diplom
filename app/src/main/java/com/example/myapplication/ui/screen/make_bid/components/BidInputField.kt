package com.example.myapplication.ui.screen.make_bid.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BidInputField(ethBidAmount: String, onValueChange: (String) -> Unit, bidError: String?) {
    Text(
        text = "Сума ставки",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.Companion.padding(start = 16.dp, bottom = 16.dp)
    )

    OutlinedTextField(
        value = ethBidAmount,
        onValueChange = onValueChange,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        isError = bidError != null,
        shape = RoundedCornerShape(12.dp),
        label = { Text("ETH") }
    )
    if (bidError != null) {
        Text(
            text = bidError ?: "",
            color = Color.Companion.Red,
            fontSize = 12.sp,
            modifier = Modifier.Companion.padding(start = 16.dp, top = 4.dp)
        )
    }
}