package com.example.recipeapp.utilities

import android.text.format.DateUtils
import java.util.Date

class TimeUtils(private val currentTimeProvider: CurrentTimeProvider) {
    fun calculateTimeAgo(createdAt: Date): String {
        val time = createdAt.time
        val now = currentTimeProvider.currentTime
        val diff = now - time
        return when {
            diff < DateUtils.MINUTE_IN_MILLIS -> "just now"
            diff < 2 * DateUtils.MINUTE_IN_MILLIS -> "a minute ago"
            diff < 50 * DateUtils.MINUTE_IN_MILLIS -> "${diff / DateUtils.MINUTE_IN_MILLIS} m"
            diff < 90 * DateUtils.MINUTE_IN_MILLIS -> "an hour ago"
            diff < 24 * DateUtils.HOUR_IN_MILLIS -> "${diff / DateUtils.HOUR_IN_MILLIS} h"
            diff < 48 * DateUtils.HOUR_IN_MILLIS -> "yesterday"
            else -> "${diff / DateUtils.DAY_IN_MILLIS} d"
        }
    }

    companion object {
        const val TAG = "TimeUtils"
    }
}