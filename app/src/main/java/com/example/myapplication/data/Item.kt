package com.example.myapplication.data

data class Item(
    val name: String,
    val description: String,
    val bid: Int,
    val buyout: Int,
    val imageUrl: String, //File
    val auctionTime: Int
    //owner id
)