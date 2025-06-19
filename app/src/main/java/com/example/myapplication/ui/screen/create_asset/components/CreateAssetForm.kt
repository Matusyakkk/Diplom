package com.example.myapplication.ui.screen.create_asset.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.ui.screen.components.TopBarWithBack


@Composable
internal fun CreateAssetForm(
    innerPadding: PaddingValues,
    name: MutableState<String>,
    onNameChange: (String) -> Unit,
    description: MutableState<String>,
    onDescriptionChange: (String) -> Unit,
    imageUri: Uri?,
    onPickImage: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(start = 16.dp, end = 16.dp)
    ) {
        TopBarWithBack("Створити NFT", onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, top = 16.dp, end = 8.dp)
                .background(
                    color = colorResource(R.color.ListBG),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            if (name.value.isNotBlank() || description.value.isNotBlank() || imageUri != null)
                AssetPreview(name = name.value, description = description.value, imageUri = imageUri)


            Spacer(modifier = Modifier.height(8.dp))

            LimitedTextField(
                value = name.value,
                onValueChange = onNameChange,
                label = "Назва",
                maxLength = 12,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LimitedTextField(
                value = description.value,
                onValueChange = onDescriptionChange,
                label = "Опис",
                maxLength = 200,
                minLines = 3,
                maxLines = 8,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(min = 56.dp, max = 150.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onPickImage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Вибрати зображення",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}