package com.example.myapplication.domain.usecase.wallet

import com.example.myapplication.domain.reopsitory.EthereumRepository
import javax.inject.Inject


class ConnectWalletUseCase @Inject constructor(
    private val repository: EthereumRepository
) {
    suspend operator fun invoke(): String? {
        return repository.connectWallet()
    }
}