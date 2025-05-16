package com.example.myapplication.ui.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel

@Composable
fun MakeBidScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
) {
    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())
    var bidAmount by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        BackButton(navController, "Зробити ставку")

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, top = 16.dp, end = 8.dp)
                .background(
                    color = colorResource(R.color.ListBG),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            ImageNameRow(assetData)

            Text(
                text = "Сума ставки",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = bidAmount,
                onValueChange = { bidAmount = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TODO("Available balance of wallet")
            ItemInfo("Доступний баланс", "7")

            TODO("Вираховувати комісію???")
            ItemInfo("Комісія", "0.05")

            ItemInfo("Загалом", bidAmount.plus(0.05))

            ActionBtn({
                viewModel.placeBid(assetId.toBigInteger(),
                bidAmount.toBigInteger())
                TODO("EVENT_LISTENER:: EventListener switch?")
            },"Зробити ставку")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ImageNameRow(asset: AssetData?) {
    val imageUri = Uri.fromFile(asset?.imageFile)
    Row {
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Asset Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(124.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(18.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = asset?.name ?: "No Name",
            fontSize = 36.sp,
            modifier = Modifier.padding(top = 14.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActionBtn(onClick: () -> Unit, text: String) {
    Button(
        onClick = {
            onClick
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ItemInfo(
    parameter: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = parameter,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "$value ETH",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}