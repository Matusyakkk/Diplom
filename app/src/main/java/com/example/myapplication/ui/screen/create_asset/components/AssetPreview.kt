package com.example.myapplication.ui.screen.create_asset.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter


@Composable
internal fun AssetPreview(name: String, description: String, imageUri: Uri?) {
    Row {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Asset Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(124.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            fontSize = 36.sp,
            modifier = Modifier.padding(top = 14.dp),
            fontWeight = FontWeight.Bold
        )

    }
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
        color = Color.Gray
    )
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        thickness = 1.dp,
        color = Color.DarkGray
    )
}
