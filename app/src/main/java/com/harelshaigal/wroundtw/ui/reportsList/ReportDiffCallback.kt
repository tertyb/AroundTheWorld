package com.harelshaigal.aroundtw.ui.reportsList

import androidx.recyclerview.widget.DiffUtil
import com.harelshaigal.aroundtw.data.report.Report
import com.harelshaigal.aroundtw.data.report.ReportWithUser

class ReportDiffCallback : DiffUtil.ItemCallback<ReportWithUser>() {
    override fun areItemsTheSame(oldItem: ReportWithUser, newItem: ReportWithUser): Boolean =
        oldItem == newItem


    override fun areContentsTheSame(oldItem: ReportWithUser, newItem: ReportWithUser): Boolean =
        oldItem.report == newItem.report

}