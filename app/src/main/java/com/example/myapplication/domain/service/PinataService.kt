package com.example.myapplication.domain.service

import org.json.JSONObject
import java.io.File


interface PinataService {
    suspend fun uploadFile(file: File): String
    suspend fun uploadMetadata(metadata: JSONObject): String
}