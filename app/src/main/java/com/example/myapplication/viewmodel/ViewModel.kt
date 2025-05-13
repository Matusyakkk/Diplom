package com.example.myapplication.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.metamask.androidsdk.Ethereum
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ViewModel @Inject constructor(private val ethereum: Ethereum): ViewModel() {

    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    var address by mutableStateOf("")
        private set

    fun connectWallet() {
        viewModelScope.launch {
            ethereum.connect {
                address = ethereum.selectedAddress
                if (!address.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(walletConnected = true)
                    //create contract
                    //fetch data
                } else {
                    Log.e("TESTWALLET", "Failed to connect to wallet")
                }
            }
        }
    }

    fun onContinueWithoutWalletClick() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = true)
    }

    fun resetWalletConnectUiState() {
        _uiState.value = _uiState.value.copy(continueWithoutWallet = false)
        _uiState.value = _uiState.value.copy(walletConnected = false)
    }

    fun logOut(){
        resetWalletConnectUiState()
        address = ""
        ethereum.clearSession()
    }

    data class WalletConnectUiState(
        val walletConnected: Boolean = false,
        val continueWithoutWallet: Boolean = false
    )
}