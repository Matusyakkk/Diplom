package com.example.myapplication.data

data class WalletConnectUiState(
    val walletConnected: Boolean = false,
    val continueWithoutWallet: Boolean = false
)