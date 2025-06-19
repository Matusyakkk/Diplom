package com.example.myapplication.data.model

import java.io.File
import java.math.BigInteger

data class AssetData(
    val assetId: BigInteger = BigInteger("-1"),
    val name: String = "",
    val description: String = "",
    val imageFile: File = File(""),
    val owner: String = "",
    val buyoutPrice: BigInteger = BigInteger.ZERO,
    val auctionEndTime: BigInteger = BigInteger.ZERO,
    val highestBid: BigInteger = BigInteger.ZERO,
    val highestBidder: String = "0x0000000000000000000000000000000000000000"
)