package com.example.myapplication.domain.usecase.wallet

import com.example.myapplication.domain.reopsitory.EthereumRepository
import java.math.BigDecimal
import javax.inject.Inject

class GetBalanceUseCase @Inject constructor(
    private val ethereumRepo: EthereumRepository
) {
    suspend operator fun invoke(address: String): BigDecimal {
        return ethereumRepo.getBalance(address)
    }
}
