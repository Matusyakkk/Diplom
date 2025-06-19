package com.example.myapplication.ui.screen.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.data.model.AssetData

@Composable
fun ImageNameRow(asset: AssetData?) {
    val imageUri = remember(asset?.imageFile) {
        asset?.imageFile?.let { Uri.fromFile(it) }
    }
    Row {
        AssetImage(
            imageUri = imageUri,
            modifier = Modifier.Companion
                .size(124.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(18.dp))
        )
//        imageUri?.let {
//            Image(
//                painter = rememberAsyncImagePainter(it),
//                contentDescription = "Asset Image",
//                contentScale = ContentScale.Companion.Crop,
//                modifier = Modifier.Companion
//                    .size(124.dp)
//                    .padding(16.dp)
//                    .clip(RoundedCornerShape(18.dp))
//            )
//        }
        Spacer(modifier = Modifier.Companion.width(16.dp))
        Text(
            text = asset?.name ?: "No Name",
            fontSize = 36.sp,
            modifier = Modifier.Companion.padding(top = 14.dp),
            fontWeight = FontWeight.Companion.Bold
        )
    }
}