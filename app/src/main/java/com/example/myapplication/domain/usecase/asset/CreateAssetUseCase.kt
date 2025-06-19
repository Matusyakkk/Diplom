package com.example.myapplication.domain.usecase.asset

import android.util.Log
import com.example.myapplication.domain.service.PinataService
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class CreateAssetUseCase @Inject constructor(
    private val pinataService: PinataService,
    private val mintAssetUseCase: MintAssetUseCase
) {
    suspend operator fun invoke(
        imageFile: File,
        name: String,
        description: String
    ): Result<String> = runCatching {
        // Завантаження зображення на Pinata
        val imageHash = pinataService.uploadFile(imageFile)

        // Створення метаданих
        val metadata = JSONObject().apply {
            put("name", name)
            put("description", description)
            put("image", "ipfs://$imageHash")
        }

        // Завантаження метаданих
        val metadataHash = pinataService.uploadMetadata(metadata)

        // Карбування активу
        mintAssetUseCase("ipfs://$metadataHash")

        return@runCatching metadataHash
    }
}