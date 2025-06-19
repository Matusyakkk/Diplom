package com.example.myapplication.data.model

data class BidState(
    val ethAmount: String = "",
    val error: String? = null,
    val isLoading: Boolean = false
)