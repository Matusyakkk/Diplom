package com.example.myapplication.ui.screen.sell_asset.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.AssetImage

@Composable
fun AssetDetailCard(assetData: AssetData?, imageUri: Uri?) {

    AssetImage(
        imageUri = imageUri,
        modifier = Modifier.Companion
            .fillMaxWidth()
            .height(200.dp),
    )
//    Image(
//        painter = rememberAsyncImagePainter(imageUri),
//        contentDescription = assetData?.name,
//        modifier = Modifier.Companion
//            .fillMaxWidth()
//            .height(200.dp),
//        contentScale = ContentScale.Companion.Crop
//    )
    Spacer(modifier = Modifier.Companion.width(16.dp))
    Text(
        text = assetData?.name.orEmpty(),
        fontSize = 36.sp,
        fontWeight = FontWeight.Companion.Bold,
        modifier = Modifier.Companion.padding(
            top = 14.dp,
            start = 14.dp,
            end = 14.dp,
            bottom = 8.dp
        ),
    )
    Text(
        text = assetData?.description.orEmpty(),
        style = MaterialTheme.typography.bodyLarge,
        color = Color.Companion.Gray,
        modifier = Modifier.Companion.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
    )
    HorizontalDivider(
        modifier = Modifier.Companion.padding(vertical = 8.dp, horizontal = 16.dp),
        thickness = 1.dp,
        color = Color.Companion.DarkGray
    )
}