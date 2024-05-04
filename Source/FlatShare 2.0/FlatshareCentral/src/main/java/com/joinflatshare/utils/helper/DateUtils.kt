package com.joinflatshare.utils.helper

import java.text.SimpleDateFormat
import java.util.Calendar
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
            sdf = SimpleDateFormat(serverFormat)
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    fun convertToAppFormat(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(appFormat)
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    fun getServerDate(): String {
        val sdf = SimpleDateFormat(serverFormat)
        return sdf.format(Date())
    }

    fun getLoggerDate(): String {
        val sdf = SimpleDateFormat(loggerFormat)
        return sdf.format(Date())
    }

    fun convertToShortMonthFormat(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(serverFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(shortMonthFormat)
            sdf.format(dt)
        } catch (ex: Exception) {
            ""
        }
    }

    private fun getNotificationDate(createdTime: String): Date {
        val sdf = SimpleDateFormat(serverFormat)
        return sdf.parse(createdTime)
    }

    fun getInviterDate(serverFormat: String): String {
        try {
            val onlyDate = serverFormat.substring(0, serverFormat.indexOf('T'))
            var sdf = SimpleDateFormat("yyyy-MM-dd")
            val serverDate = sdf.parse(onlyDate)
            sdf = SimpleDateFormat("dd MMM yyyy")
            return sdf.format(serverDate)
        } catch (ex: Exception) {
            return serverFormat
        }
    }

    fun getDOB(date: String?): String {
        return try {
            var sdf = SimpleDateFormat(dobServerFormat, Locale.getDefault())
            val dt: Date = sdf.parse(date)
            sdf = SimpleDateFormat(appFormat)
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

    fun getPostTime(createdTime: String?): String {
        if (createdTime == null || createdTime.isEmpty()) return ""
        val postTime: String
        val date = getNotificationDate(createdTime)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date.time
        calendar.add(Calendar.HOUR_OF_DAY, 5)
        calendar.add(Calendar.MINUTE, 30)
        val postTimeinMillis = calendar.timeInMillis
        val serverCurrentTime = Date().time
        postTime = if (postTimeinMillis < serverCurrentTime) {
            val timeDifference = serverCurrentTime - postTimeinMillis
            // Difference within a hour
            val minutesInSecond = (60 * 1000).toLong()
            val minutesInHour = 60 * minutesInSecond
            val hoursInDay = 24 * minutesInHour
            val daysInWeek = 7 * hoursInDay
            if (timeDifference < minutesInHour) {
                "" + timeDifference / minutesInSecond + "m"
            } else if (timeDifference < hoursInDay) {
                // Difference within a day
                "" + timeDifference / minutesInHour + "h"
            } else if (timeDifference < daysInWeek) {
                // Difference within a week
                "" + timeDifference / hoursInDay + "d"
            } else {
                "" + timeDifference / daysInWeek + "w"
            }
        } else "1m"
        return postTime
    }
}