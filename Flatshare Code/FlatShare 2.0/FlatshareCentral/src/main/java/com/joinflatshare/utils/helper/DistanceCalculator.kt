package com.joinflatshare.utils.helper

import com.joinflatshare.pojo.user.User
import java.text.DecimalFormat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object DistanceCalculator {
    fun meterToKm(meters: Double): String {
        var km = ""
        val kilo = meters / 1000
        km = String.format("%.2f", kilo)
        return "$km km"
    }

    fun kmToKm(km: Double): String {
        return String.format("%.1f", km) + " km"
    }

    fun calculateDistance(user1: User, user2: User): String {
        val lat1 = user1.flatProperties.preferredLocation[0].loc.coordinates[1]
        val lon1 = user1.flatProperties.preferredLocation[0].loc.coordinates[0]

        val lat2 = user2.flatProperties.preferredLocation[0].loc.coordinates[1]
        val lon2 = user2.flatProperties.preferredLocation[0].loc.coordinates[0]
        try {
            if ((lat1 == 0.0 && lon1 == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) return "NA"
            // radius in meters
            val radius = 6371 * 1000

            val pi1 = (lat1 * Math.PI) / 180
            val pi2 = (lat2 * Math.PI) / 180
            val deltaPi = ((lat2 - lat1) * Math.PI) / 180
            val deltaRad = ((lon2 - lon1) * Math.PI) / 180

            val a =
                (sin(deltaPi / 2) * sin(deltaPi / 2)) + cos(pi1) * cos(pi2) * sin(
                    deltaRad / 2
                ) * sin(deltaRad / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            // in metres
            val d = radius * c;

            // in kms
            val formatter = DecimalFormat("#.#")
            val dist = d / 1000
            return formatter.format(dist) + " km"
        } catch (ex: Exception) {
            return "NA"
        }
    }
}