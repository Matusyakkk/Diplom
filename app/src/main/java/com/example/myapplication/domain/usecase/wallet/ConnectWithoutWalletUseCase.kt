package com.example.myapplication.domain.usecase.wallet

import com.example.myapplication.domain.reopsitory.EthereumRepository
import javax.inject.Inject

class ConnectWithoutWalletUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke() {
        ethereumRepo.getContract()
    }
}
