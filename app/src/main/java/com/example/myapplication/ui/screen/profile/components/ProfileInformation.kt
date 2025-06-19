package com.example.myapplication.ui.screen.profile.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.viewmodel.WalletViewModel
import com.example.myapplication.utils.CryptoUtils
import okhttp3.Address

//Інформація профілю
@Composable
fun ProfileInformation(address: String) {
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = colorResource(R.color.ListBG),
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.nft1337),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Companion.FillBounds,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        )

        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.Bottom
        ) {
            Text(
                text = "Address",
                modifier = Modifier.Companion.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = CryptoUtils.shortenAddress(address),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.Companion.padding(16.dp),
                color = Color.Companion.Gray
            )
        }
    }
}