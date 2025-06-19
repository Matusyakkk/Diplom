package com.example.myapplication.ui.screen.details_asset.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.AssetImage

@Composable
fun ImageBlock(asset: AssetData?) {
    val imageUri = asset?.imageFile?.let { Uri.fromFile(it) }//val imageUri = Uri.fromFile(asset.imageFile)
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
/*        imageUri?.let { uri ->
//            Image(
//                painter = rememberAsyncImagePainter(uri),
//                contentDescription = "Asset Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
//            )
//        } ?: run {
//            Log.d("IMAGEBLOCK NULL", "IN IMAGEBLOCK NULL WHY????????")
//            Log.d("IMAGEBLOCK NULL", asset?.assetId.toString())
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//                    .background(Color.Gray, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
//            ) {
//                Text(
//                    text = "Зображення недоступне",
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }*/
        AssetImage(
            imageUri = imageUri,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        )
//        Image(
//            painter = rememberAsyncImagePainter(imageUri),
//            contentDescription = "Asset Image",
//            contentScale = ContentScale.Companion.Crop,
//            modifier = Modifier.Companion
//                .fillMaxWidth()
//                .height(300.dp)
//                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
//        )
        Text(
            text = asset?.name ?: "NO Name",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.Companion.padding(16.dp),
            fontWeight = FontWeight.Companion.Bold
        )
        Text(
            text = asset?.description ?: "No description",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.Companion.padding(bottom = 16.dp, start = 16.dp)
        )
    }
}