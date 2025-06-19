package com.example.myapplication.ui.screen.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.model.AssetData
import java.math.BigInteger

@Composable
fun ItemRow(onItemClick: (BigInteger) -> Unit, asset: AssetData) {
    var expanded by remember { mutableStateOf(false) }
    val imageUri = Uri.fromFile(asset.imageFile)

    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .background(color = colorResource(R.color.ListBG), shape = RoundedCornerShape(18.dp))
    ) {
        if (!expanded) {
            Row() {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Asset Image",
                    contentScale = ContentScale.Companion.Crop,
                    modifier = Modifier.Companion
                        .size(75.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(18.dp))
                )
                Spacer(modifier = Modifier.Companion.width(16.dp))
                Text(
                    text = asset.name,
                    fontSize = 36.sp,
                    modifier = Modifier.Companion.padding(top = 14.dp),
                    fontWeight = FontWeight.Companion.Bold
                )
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier.Companion.clip(
                    androidx.compose.foundation.shape.RoundedCornerShape(
                        18.dp
                    )
                )
            ) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Asset Image",
                    contentScale = ContentScale.Companion.Crop,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .height(300.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(asset.assetId) }
                )
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.Companion.padding(16.dp),
                    fontWeight = FontWeight.Companion.Bold
                )
                Text(
                    text = asset.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.Companion.padding(bottom = 16.dp, start = 16.dp)
                )
            }
        }
    }
}