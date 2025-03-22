package com.harelshaigal.aroundtw.ui.reportsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import com.harelshaigal.aroundtw.data.report.ReportWithUser
import com.harelshaigal.aroundtw.databinding.FragmentReportListItemBinding

class ReportListIAdapter(
    private val fragmentManager: FragmentManager,
    private val currentUserId: String,
    private val viewModel: ReportsListViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val userId: String
) : ListAdapter<ReportWithUser, ReportViewHolder>(ReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = FragmentReportListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ReportViewHolder(
            parent.context, binding, fragmentManager, viewModel, viewLifecycleOwner, this, userId
        )
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val currentReportWithUser: ReportWithUser = getItem(position)
        holder.bind(currentReportWithUser, currentUserId)
    }
}

