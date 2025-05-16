package com.example.myapplication.ui.screen

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.AssetData
import com.example.myapplication.viewmodel.ViewModel
import java.math.BigInteger
import java.time.Instant
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListAssetScreen(
    viewModel: ViewModel,
    assetId: String,
    navController: NavController
){
    val assetData: AssetData? = viewModel.findById(assetId.toBigInteger())
    var buyuot by remember { mutableStateOf("0") }
    val imageUri = Uri.fromFile(assetData?.imageFile)

    var selectedTime by remember { mutableStateOf(1) }  // Тривалість аукціону в днях
    // Обчислення кінцевого часу аукціону
    val currentTime = Instant.now()
    val auctionEndTimeBigInt = currentTime.plus(selectedTime.toLong(), ChronoUnit.DAYS).epochSecond

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.listAssetForAuction(
                            assetId.toBigInteger(),
                            buyuot.toBigInteger(),
                            auctionEndTimeBigInt.toBigInteger()
                        )
                        TODO("EVENT_LISTENER:: listener screen switch??")
                        //List asset and back to Asset Details screen
                        //navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Виставити на продаж",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp)
        ) {
            BackButton(navController, "Виставити на продаж актив")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, top = 16.dp, end = 8.dp)
                    .background(
                        color = colorResource(R.color.ListBG),
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {

                Row {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = assetData?.name,
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = assetData?.name.toString(),
                        fontSize = 36.sp,
                        modifier = Modifier.padding(top = 14.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = assetData?.description.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
                    color = Color.Gray
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Ціна викупу:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )
                OutlinedTextField(
                    value = buyuot,
                    onValueChange = { buyuot = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Тривалість аукціону (в днях):",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 8.dp)
                )
                Slider(
                    value = selectedTime.toFloat(),
                    onValueChange = { selectedTime = it.toInt() },
                    valueRange = 1f..30f,  // Вибір від 1 до 30 днів
                    steps = 29,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Text("Вибрано днів: $selectedTime", modifier = Modifier.padding(start = 16.dp))
            }
        }
    }
}