package com.harelshaigal.madamal.ui.mapDisplay.reportsMap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.harelshaigal.madamal.data.report.Report
import com.harelshaigal.madamal.data.report.ReportRepository
import com.harelshaigal.madamal.data.report.ReportWithUser

class ReportsMapViewModel : ViewModel() {
    private val repository: ReportRepository = ReportRepository()

    val reportList: LiveData<List<ReportWithUser>> =  repository.getAllReports()
}