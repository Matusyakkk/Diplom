package com.example.myapplication.ui.screen.profile.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.utils.ComponentUtils.shortenDescription
import java.math.BigInteger

//Мої предмети
@Composable
fun TabContentGrid(
    assetsOwnedByUser: List<AssetData>,
    onClickListAsset: (BigInteger) -> Unit
) {
    if (assetsOwnedByUser.isEmpty()){
        Text(text = "Тут ще немає активів")
    } else{
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(8.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(assetsOwnedByUser.size) { index ->
                val asset = assetsOwnedByUser[index]
                AssetCard(asset, onClickListAsset)
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: AssetData,
    onClickListAsset: (BigInteger) -> Unit
) {
    val imageUri = Uri.fromFile(asset.imageFile)
    Column(
        modifier = Modifier.Companion
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            .clickable {
                onClickListAsset(asset.assetId)
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Asset Image",
            contentScale = ContentScale.Companion.Crop,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(100.dp)
        )
        Text(
            text = asset.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Companion.Bold,
            maxLines = 1,
            overflow = TextOverflow.Companion.Ellipsis,
            modifier = Modifier.Companion.padding(8.dp)
        )
        Text(
            text = shortenDescription(asset.description),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Companion.Ellipsis,
            modifier = Modifier.Companion.padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
        )
    }
}