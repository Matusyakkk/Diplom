package com.example.myapplication.domain.usecase.wallet

import com.example.myapplication.domain.reopsitory.EthereumRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    operator fun invoke() {
        ethereumRepo.clearSession()
    }
}
