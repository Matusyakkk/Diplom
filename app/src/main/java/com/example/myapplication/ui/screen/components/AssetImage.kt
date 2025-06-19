package com.example.myapplication.ui.screen.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter

@Composable
fun AssetImage(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    if (imageUri != null) {
        val imageModifier = if (onClick != null) {
            modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
        } else {
            modifier
        }

        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Asset Image",
            contentScale = ContentScale.Crop,
            modifier = imageModifier
        )
    } else {
        Log.d("AssetImage", "imageUri == null")
        Box(
            modifier = modifier.background(Color.Gray, shape = RoundedCornerShape(18.dp))
        ) {
            Text(
                text = "Зображення недоступне",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
