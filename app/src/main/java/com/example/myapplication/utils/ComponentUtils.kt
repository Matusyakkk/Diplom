package com.example.myapplication.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.roundToInt

object ComponentUtils {
    fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                File.createTempFile("nft_", ".jpg", context.cacheDir).apply {
                    FileOutputStream(this).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FileUtils", "Error converting URI to file", e)
            null
        }
    }
    /*
    fun uriToFile(uri: Uri, context: Context): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile("nft", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            file
        } catch (e: Exception) {
            Log.e("TESTWALLET", "CreateItemScreen.kt uriToFile() Error ${e.message.toString()}")
            null
        }
    }*/


    fun Offset.toIntOffset() = IntOffset(x.roundToInt(), y.roundToInt())

    fun shortenDescription(text: String, maxLength: Int = 25): String {
        return if (text.length > maxLength) {
            text.take(maxLength) + "..."
        } else {
            text
        }
    }

    fun replaceCommasWithDots(input: String): String {
        return input.replace(',', '.')
    }

    fun validatePrice(input: String): Boolean {
        val cleanInput = input.replace(',', '.')
        return try {
            val price = BigDecimal(cleanInput)
            price > BigDecimal.ZERO
        } catch (_: Exception) {
            false
        }
    }

    fun validateBid(
        bidWei: BigInteger,
        highestBid: BigInteger,
        buyoutPrice: BigInteger,
        userBalance: BigInteger
    ): String? {
        return when {
            bidWei <= highestBid -> "Ставка має бути більше ніж: ${CryptoUtils.weiToEth(highestBid)}"
            userBalance <= bidWei -> "Недостатньо коштів"
            bidWei >= buyoutPrice -> "Ставка не може бути більшою за ціну викупу"
            else -> null
        }
    }

    fun formatTime(seconds: Int): String {
        val days = seconds / 86400  // Обчислюємо кількість днів (86400 секунд у дні)
        val hours = (seconds % 86400) / 3600  // Обчислюємо години
        val minutes = (seconds % 3600) / 60  // Обчислюємо хвилини
        val remainingSeconds = seconds % 60  // Обчислюємо залишкові секунди

        return String.format("%02d дн. %02d:%02d:%02d", days, hours, minutes, remainingSeconds)
    }

}


