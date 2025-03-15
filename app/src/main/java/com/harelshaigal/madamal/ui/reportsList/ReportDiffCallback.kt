package com.harelshaigal.madamal.ui.reportsList

import androidx.recyclerview.widget.DiffUtil
import com.harelshaigal.madamal.data.report.Report
import com.harelshaigal.madamal.data.report.ReportWithUser

class ReportDiffCallback : DiffUtil.ItemCallback<ReportWithUser>() {
    override fun areItemsTheSame(oldItem: ReportWithUser, newItem: ReportWithUser): Boolean =
        oldItem == newItem


    override fun areContentsTheSame(oldItem: ReportWithUser, newItem: ReportWithUser): Boolean =
        oldItem.report == newItem.report

}