package com.harelshaigal.aroundtw.helpers

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
class Utils {
    companion object {
        fun formatTimestampToString(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("d.M.yy HH:mm", Locale.getDefault())
        return format.format(date)
    }


        fun getUserImageName(id: String) = "images/users/${id}/profile.jpg"
        fun getReportImageName(id: String) = "reportsImages/${id}/reportImage.jpg"
    }
}