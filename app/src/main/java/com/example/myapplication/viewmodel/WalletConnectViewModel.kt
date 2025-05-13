package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletConnectViewModel: ViewModel() {

    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    fun onConnectClick() {
        //TODO: Підключення гаманця
        _uiState.value = _uiState.value.copy(walletConnected = true)
    }

    fun onContinueWithoutWalletClick() {
        //TODO: Продовження без гаманця
        _uiState.value = _uiState.value.copy(continueWithoutWallet = true)
    }

    data class WalletConnectUiState(
        val walletConnected: Boolean = false,
        val continueWithoutWallet: Boolean = false
    )
}