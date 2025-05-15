package com.example.myapplication.data

import java.io.File
import java.math.BigInteger

data class AssetData(
    val assetId: BigInteger,
    val name: String,
    val description: String,
    val imageFile: File,
    val owner: String,
    val buyoutPrice: BigInteger,
    val auctionEndTime: BigInteger,
    val highestBid: BigInteger,
    val highestBidder: String
)