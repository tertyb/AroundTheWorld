package com.harelshaigal.aroundtw.ui.mapDisplay.reportsMap

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.harelshaigal.aroundtw.data.report.Report
import com.harelshaigal.aroundtw.data.report.ReportRepository
import com.harelshaigal.aroundtw.data.report.ReportWithUser

class ReportsMapViewModel : ViewModel() {
    private val repository: ReportRepository = ReportRepository()

    val reportList: LiveData<List<ReportWithUser>> =  repository.getAllReports()
}