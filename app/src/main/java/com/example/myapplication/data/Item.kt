package com.example.myapplication.data

import java.io.File
import java.math.BigInteger

data class Item(
    //val tokenId: BigInteger,
    val name: String,
    val description: String,
    //val owner: String,
    val bid: Int,
    val buyout: Int,
    val imageFile: String,//File,
    val auctionTime: Int
)

//IPFS
data class ItemMetadata(
    val name: String,
    val description: String,
    val imageUri: String
)

data class TestItem(
    val name: String,
    val description: String,
    val imageFile: File
)