package com.example.myapplication.data.model

import java.math.BigInteger

data class WalletConnectUiState(
    val isConnected: Boolean = false,
    val address: String = "",
    val balance: BigInteger = BigInteger.ZERO,
    val continueWithoutWallet: Boolean = false,
    val error: Exception? = null
)