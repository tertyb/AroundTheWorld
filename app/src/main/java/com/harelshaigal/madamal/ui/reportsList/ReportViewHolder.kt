package com.harelshaigal.aroundtw.ui.reportsList

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.harelshaigal.aroundtw.data.report.ReportWithUser
import com.harelshaigal.aroundtw.databinding.FragmentReportListItemBinding
import com.harelshaigal.aroundtw.helpers.Utils
import com.harelshaigal.aroundtw.ui.reportDialogs.DeleteReportDialog
import com.harelshaigal.aroundtw.ui.reportDialogs.reportDialogForm.ReportDialogFormFragment
import com.squareup.picasso.Picasso

class ReportViewHolder(
    private val context: Context,
    private val binding: FragmentReportListItemBinding,
    private val fragmentManager: FragmentManager,
    private val viewModel: ReportsListViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val reportAdapter: ReportListIAdapter,
    private val userId: String
) : RecyclerView.ViewHolder(binding.root) {
    private var currentReportId: String? = null

    init {
        binding.deleteReport.setOnClickListener {
            currentReportId?.let { it1 ->
                DeleteReportDialog.createDeleteDialog(context, it1) {
                    // Refresh the data after deletion
                    viewModel.getReportList(userId).observe(viewLifecycleOwner) {
                        reportAdapter.submitList(it.sortedByDescending { report -> report.report.lastUpdated })
                    }
                }
            }
        }

        binding.editReport.setOnClickListener {
            ReportDialogFormFragment.display(fragmentManager, currentReportId) {
                viewModel.getReportList(userId).observe(viewLifecycleOwner) {
                    reportAdapter.submitList(it.sortedByDescending { report -> report.report.lastUpdated }.toMutableList())
                }
            }
        }
    }

    fun bind(reportWithUser: ReportWithUser, currentUserId: String) {
        currentReportId = reportWithUser.report.id
        binding.reportTitle.text = reportWithUser.report.title
        binding.reportDate.text = reportWithUser.report.lastUpdated?.let { Utils.formatTimestampToString(it) }
        binding.reportData.text = reportWithUser.report.data
        binding.userName.text = reportWithUser.userName

        // Conditionally display action buttons
        if (reportWithUser.report.userId == currentUserId) {
            binding.editReport.visibility = View.VISIBLE
            binding.deleteReport.visibility = View.VISIBLE
        } else {
            binding.editReport.visibility = View.GONE
            binding.deleteReport.visibility = View.GONE
        }
        Log.d("ReportViewHolder","reportWithUser.userImage: ${reportWithUser.userImage}")

        if (reportWithUser.userImage != "null" && reportWithUser.userImage != "" && reportWithUser.userImage != null) {
            Log.d("ReportViewHolder","got here")
            Picasso.get().load(Uri.parse(reportWithUser.userImage)).into(binding.profileImage)
            binding.profileImage.visibility = View.VISIBLE
        }

        if (Firebase.auth.currentUser?.uid != reportWithUser.report.userId) {
            binding.actionButtonsContainer.visibility = View.GONE
        }

        if (reportWithUser.report.image != "null" && reportWithUser.report.image != null) {
            Picasso.get().load(Uri.parse(reportWithUser.report.image)).into(binding.reportImage)
            binding.reportImage.visibility = View.VISIBLE
        }
    }
}