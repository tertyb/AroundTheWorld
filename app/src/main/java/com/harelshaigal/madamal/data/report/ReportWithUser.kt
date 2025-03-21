package com.harelshaigal.aroundtw.data.report

data class ReportWithUser(
    val report: Report,
    val userName: String = "",
    val userImage: String = ""
)