package com.joinflatshare.utils.helper

import java.text.NumberFormat
import java.util.Locale

object CurrencyHandler {
    fun convertToDot(amount: String): String {
        var amount = amount
        try {
            if (amount.contains(","))
                amount = amount.replace(",", ".")
        } catch (ex: Exception) {

        }
        return amount
    }


    fun formatPrice(amount: String): String {
        if (amount.isBlank())
            return amount
        val amount = convertToDot(amount).toDouble()
        try {
            val priceFormat = NumberFormat.getCurrencyInstance(Locale("en", "in"))
            return removeDecimal(priceFormat.format(amount))
        } catch (ex: java.lang.Exception) {
            return "" + amount
        }
    }

    private fun removeDecimal(amount: String): String {
        return amount.substring(0, amount.indexOf("."))
    }

}