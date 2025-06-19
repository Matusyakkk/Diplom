package com.example.myapplication.ui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)//shared??
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent

    fun navigate(event: NavigationEvent) {
        _navigationEvent.tryEmit(event)
    }

    fun onEventHandled() {
        _navigationEvent.tryEmit(null)
    }
}