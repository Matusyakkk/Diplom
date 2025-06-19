package com.example.myapplication.ui.viewmodel.base

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


inline fun ViewModel.launchWithLoading(
    crossinline block: suspend () -> Unit
) {
    viewModelScope.launch {
        if (this@launchWithLoading is ViewModelWithLoading) {
            isLoading = true
        }

        runCatching {
            block()
        }.onFailure {
            Log.e("ViewModel", "Error: ${it.message}", it)
        }

        if (this@launchWithLoading is ViewModelWithLoading) {
            isLoading = false
        }
    }
}
