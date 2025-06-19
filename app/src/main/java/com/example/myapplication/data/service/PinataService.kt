package com.example.myapplication.data.service

import com.example.myapplication.domain.service.PinataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton


@Singleton
class PinataServiceImpl @Inject constructor(
    private val client: OkHttpClient,
    @Named("PINATA_JWT") private val jwtToken: String
) : PinataService {

    override suspend fun uploadFile(file: File): String {
        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, requestBody)
            .build()
        return executeUpload(
            Request.Builder()
                .url("https://api.pinata.cloud/pinning/pinFileToIPFS")
                .addHeader("Authorization", "Bearer $jwtToken")
                .post(multipartBody)
        )
    }

    override suspend fun uploadMetadata(metadata: JSONObject): String {
        val requestBody = metadata.toString()
            .toRequestBody("application/json".toMediaType())

        return executeUpload(
            Request.Builder()
                .url("https://api.pinata.cloud/pinning/pinJSONToIPFS")
                .addHeader("Authorization", "Bearer $jwtToken")
                .post(requestBody)
        )
    }

    private suspend fun executeUpload(requestBuilder: Request.Builder): String {
        return withContext(Dispatchers.IO) {
            val response = client.newCall(requestBuilder.build()).execute()
            if (!response.isSuccessful) throw Exception("Upload failed: ${response.code}")

            val json = JSONObject(response.body?.string() ?: "")
            json.getString("IpfsHash")
        }
    }
}