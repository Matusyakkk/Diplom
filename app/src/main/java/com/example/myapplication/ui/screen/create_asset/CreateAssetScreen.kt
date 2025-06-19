package com.example.myapplication.ui.screen.create_asset

import android.net.Uri
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.example.myapplication.ui.screen.components.BottomButton
import com.example.myapplication.ui.screen.create_asset.components.CreateAssetForm
import com.example.myapplication.ui.screen.create_asset.components.CreateButton

@Composable
fun CreateAssetScreen(
    name: MutableState<String>,
    onNameChange: (String) -> Unit,
    description: MutableState<String>,
    onDescriptionChange: (String) -> Unit,
    imageUri: Uri?,
    onPickImage: () -> Unit,
    isFormValid: Boolean,
    isLoading: Boolean,
    onBack: () -> Unit,
    onCreateClick: () -> Unit
) {
    Scaffold(
        bottomBar = {/*CreateButton()*/
            BottomButton(
                text = " Створити своє NFT ",
                enabled = isFormValid,
                isLoading = isLoading,
                onClick = onCreateClick
            )
        }
    ) { innerPadding ->
        CreateAssetForm(
            innerPadding = innerPadding,
            name = name,
            onNameChange = onNameChange,
            description = description,
            onDescriptionChange = onDescriptionChange,
            imageUri = imageUri,
            onPickImage = onPickImage,
            onBack = onBack
        )
    }
}

