package com.example.myapplication.ui.screen.profile.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import com.example.myapplication.ui.screen.components.ItemRow
import com.example.myapplication.utils.CryptoUtils
import java.math.BigInteger

//На продажі ТА Мої Ставки
@Composable
fun ListAssetRowOnSale(
    assets: List<AssetData>,
    onClickDetails: (BigInteger) -> Unit

){
    if (assets.isEmpty()){
        Text(text = "Тут ще немає активів")
    } else{
        LazyColumn(
            modifier = Modifier.Companion.padding(top = 8.dp)
        ) {
            items(assets.size) { index ->
                val asset = assets[index]
                ItemRow(onClickDetails, asset)
                //AssetRowOnSale(asset, onClickDetails)
            }
        }
    }
}

@Composable
fun AssetRowOnSale(
    asset: AssetData,
    onClickDetails: (BigInteger) -> Unit
) {
    val imageUri = Uri.fromFile(asset.imageFile)
    val ethHighestBid = CryptoUtils.weiToEth(asset.highestBid).toPlainString()
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = colorResource(R.color.ListBG), shape = RoundedCornerShape(18.dp))
    ) {
        Row(
            modifier = Modifier.Companion
                .clickable { onClickDetails(asset.assetId) }) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Asset Image",
                contentScale = ContentScale.Companion.Crop,
                modifier = Modifier.Companion
                    .size(75.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
            )
            Spacer(modifier = Modifier.Companion.width(16.dp))

            Row(
                modifier = Modifier.Companion
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Companion.Bottom
            ) {
                Text(
                    text = asset.name,
                    fontSize = 36.sp,
                    modifier = Modifier.Companion.padding(top = 14.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis,
                    fontWeight = FontWeight.Companion.Bold
                )
                /*Spacer(modifier = Modifier.Companion.width(8.dp))
                Text(
                    text = "$ethHighestBid ETH",
                    fontSize = 22.sp,
                    modifier = Modifier.Companion.padding(top = 14.dp, end = 16.dp),
                    color = Color.Companion.Gray
                )*/
            }
        }
    }
}