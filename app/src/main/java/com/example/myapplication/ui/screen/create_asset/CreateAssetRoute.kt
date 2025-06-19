package com.example.myapplication.ui.screen.create_asset

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screen.components.NavigationEventEffect
import com.example.myapplication.ui.viewmodel.AssetViewModel
import com.example.myapplication.ui.viewmodel.WalletViewModel
import com.example.myapplication.utils.ComponentUtils

@Composable
fun CreateAssetRoute(
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val assetViewModel: AssetViewModel = hiltViewModel(activity)

    val context = LocalContext.current

    NavigationEventEffect(assetViewModel.navigationManager, navController)

    var name = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val isFormValid = name.value.isNotBlank() && description.value.isNotBlank() && imageUri != null
    val isLoading = assetViewModel.isLoading

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    CreateAssetScreen(
        name = name,
        onNameChange = { if (it.length <= 12) name.value = it },
        description = description,
        onDescriptionChange = { if (it.length <= 200) description.value = it },
        imageUri = imageUri,
        onPickImage = { imagePickerLauncher.launch("image/*") },
        isFormValid = isFormValid,
        isLoading = isLoading,
        onBack = { assetViewModel.navigateToAssetsForSale() },
        onCreateClick = {
            imageUri?.let { uri ->
                val file = ComponentUtils.uriToFile(uri, context)
                if (file != null) {
                    assetViewModel.createAndUploadAsset(file, name.value, description.value)
                }
            }
        }
    )
}
