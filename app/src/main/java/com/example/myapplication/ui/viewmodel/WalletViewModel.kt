package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.WalletConnectUiState
import com.example.myapplication.domain.usecase.wallet.WalletUseCases
import com.example.myapplication.ui.navigation.NavigationEvent
import com.example.myapplication.ui.navigation.NavigationManager
import com.example.myapplication.ui.viewmodel.base.ViewModelWithLoading
import com.example.myapplication.ui.viewmodel.base.launchWithLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletUseCases: WalletUseCases,
    internal val navigationManager: NavigationManager,
) : ViewModel(), ViewModelWithLoading {

    private val _uiState = MutableStateFlow(WalletConnectUiState())
    val uiState: StateFlow<WalletConnectUiState> = _uiState.asStateFlow()

    override var isLoading by mutableStateOf(false)

    val address get() = _uiState.value.address

    fun connectWallet() = launchWithLoading {
        try {
            val walletAddress = walletUseCases.connect()
            if (walletAddress == null)
                throw Exception("Підключення гаманця не вдалося. Адреса отримана як null.")

            _uiState.update { it.copy(address = walletAddress, isConnected = true, error = null) }
            navigationManager.navigate(NavigationEvent.GoToAssetsForSaleScreen)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e, isConnected = false) }
            Log.e("WalletVM", "Connect error", e)
        }
    }

    fun proceedWithoutWallet() = launchWithLoading {
        try {
            walletUseCases.proceedWithoutWallet()
            _uiState.update { it.copy(continueWithoutWallet = true, error = null) }
            navigationManager.navigate(NavigationEvent.GoToAssetsForSaleScreen)
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e, continueWithoutWallet = false) }
            Log.e("WalletVM", "Proceed without wallet error", e)
        }
    }

    fun fetchWalletBalance() = launchWithLoading {
        try {
            val result = walletUseCases.fetchWalletBalance(_uiState.value.address)
            _uiState.update { it.copy(balance = result.toBigInteger()) }
            Log.i("BALANCE", "User balance: $result")
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e) }
            Log.e("WalletVM", "Balance error", e)
        }
    }

    fun performLogout() {
        walletUseCases.performLogout()
        _uiState.value = WalletConnectUiState()
        navigationManager.navigate(NavigationEvent.GoToWalletConnectScreen)
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
