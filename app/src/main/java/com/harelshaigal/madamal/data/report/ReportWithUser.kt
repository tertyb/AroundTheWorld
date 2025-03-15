package com.harelshaigal.madamal.data.report

data class ReportWithUser(
    val report: Report,
    val userName: String = "",
    val userImage: String = ""
)