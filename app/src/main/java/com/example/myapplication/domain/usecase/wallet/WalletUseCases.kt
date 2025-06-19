package com.example.myapplication.domain.usecase.wallet

data class WalletUseCases(
    val connect: ConnectWalletUseCase,
    val proceedWithoutWallet: ConnectWithoutWalletUseCase,
    val fetchWalletBalance: GetBalanceUseCase,
    val performLogout: LogoutUseCase,
)