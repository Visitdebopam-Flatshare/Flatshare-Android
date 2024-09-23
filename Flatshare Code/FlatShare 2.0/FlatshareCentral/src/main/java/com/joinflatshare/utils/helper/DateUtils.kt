package com.joinflatshare.utils.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private const val serverFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val appFormat = "dd/MM/yyyy"
    private const val dobServerFormat = "yyyy/MM/dd"
    private const val loggerFormat = "dd/MM/yyyy HH:mm:ss"
    private const val shortMonthFormat = "dd MMM yyyy"

    fun convertToServerFormat(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(appFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(serverFormat,Locale.getDefault())
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    fun convertToAppFormat(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(appFormat, Locale.getDefault())
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    fun getServerDate(): String {
        val sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
        return sdf.format(Date())
    }

    fun getLoggerDate(): String {
        val sdf = SimpleDateFormat(loggerFormat, Locale.getDefault())
        return sdf.format(Date())
    }

    fun convertToShortMonthFormat(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(shortMonthFormat, Locale.getDefault())
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    private fun getNotificationDate(createdTime: String): Date {
        val sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
        return sdf.parse(createdTime)
    }

    fun getDOB(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(dobServerFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(appFormat, Locale.getDefault())
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    fun getServerDateInMillis(date: String): Long {
        var sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
        val dt: Date = sdf.parse(date)
        return dt.time
    }
}