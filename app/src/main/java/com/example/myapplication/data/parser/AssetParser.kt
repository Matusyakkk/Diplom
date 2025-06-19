package com.example.myapplication.data.parser

import com.example.myapplication.contract.MyTokenizedAssets.Asset
import com.example.myapplication.data.model.AssetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface AssetParser {
    suspend fun parseAssets(assets: List<Asset>): List<AssetData>
}

@Singleton
class AssetParserImpl @Inject constructor(
    private val client: OkHttpClient
) : AssetParser {
    override suspend fun parseAssets(assets: List<Asset>): List<AssetData> {
        return assets.mapNotNull { asset ->
            runCatching { parseSingleAsset(asset) }.getOrNull()
        }
    }

    private suspend fun parseSingleAsset(asset: Asset): AssetData? {
        val metadata = fetchMetadata(asset.metaDataUri) ?: return null
        return AssetData(
            assetId = asset.assetId,
            name = metadata.getString("name"),
            description = metadata.optString("description", ""),
            imageFile = downloadImage(metadata.getString("image")),
            owner = asset.owner,
            buyoutPrice = asset.buyoutPrice,
            auctionEndTime = asset.auctionEndTime,
            highestBid = asset.highestBid,
            highestBidder = asset.highestBidder
        )
    }

    private suspend fun fetchMetadata(uri: String): JSONObject? {
        val url = uri.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")
        return executeRequest(Request.Builder().url(url).build())?.let {
            JSONObject(it.body?.string())
        }
    }

    private suspend fun downloadImage(imageUri: String): File {
        val url = imageUri.replace("ipfs://", "https://gateway.pinata.cloud/ipfs/")
        val response = executeRequest(Request.Builder().url(url).build())
            ?: throw Exception("Image download failed")

        return createTempFile(response)
    }

    private suspend fun executeRequest(request: Request): Response? {
        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().takeIf { it.isSuccessful }
        }
    }

    private fun createTempFile(response: Response): File {
        val safeName = "asset_${System.currentTimeMillis()}"//-currTime??
        val tempFile = File.createTempFile(safeName, ".jpg")//.tmp??
        response.body?.byteStream()?.use { input ->//old realisation???
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }
}