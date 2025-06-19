package com.example.myapplication.utils

import java.math.BigDecimal
import java.math.BigInteger

object CryptoUtils {
    fun weiToEth(wei: BigInteger?): BigDecimal {
        return wei?.toBigDecimal()?.divide(BigDecimal.TEN.pow(18)) ?: BigDecimal.ZERO
    }

    fun ethToWei(eth: BigDecimal): BigInteger {
        return eth.multiply(BigDecimal.TEN.pow(18)).toBigInteger()
    }

    fun shortenAddress(address: String): String {
        return if (address.length > 10) "${address.take(5)}...${address.takeLast(4)}" else address
    }
}