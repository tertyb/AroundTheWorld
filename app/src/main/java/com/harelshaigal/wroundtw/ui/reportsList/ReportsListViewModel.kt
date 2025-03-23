package com.harelshaigal.aroundtw.ui.reportsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.harelshaigal.aroundtw.data.report.Report
import com.harelshaigal.aroundtw.data.report.ReportRepository
import com.harelshaigal.aroundtw.data.report.ReportWithUser

class ReportsListViewModel : ViewModel() {
    private val repository: ReportRepository = ReportRepository()

    fun getReportList(userId: String?): LiveData<List<ReportWithUser>> = repository.getAllReports(userId)
}