package com.harelshaigal.madamal.data.report

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.harelshaigal.madamal.data.AppLocalDb
import com.harelshaigal.madamal.data.user.User
import com.harelshaigal.madamal.helpers.ImagePickerHelper
import com.harelshaigal.madamal.helpers.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReportRepository {
    private val reportsCollection = FirebaseFirestore.getInstance().collection("reports")
    private val usersCollection = FirebaseFirestore.getInstance().collection("users")
    private val reportDao by lazy { AppLocalDb.db.reportDao()!! }
    private lateinit var reportsRegistration: ListenerRegistration

    private val _reportsWithUserLiveData = MutableLiveData<List<ReportWithUser>>()
    val reportsWithUserLiveData: LiveData<List<ReportWithUser>> get() = _reportsWithUserLiveData

    fun getAllReports(userId: String? = null): LiveData<List<ReportWithUser>> {
        fetchReports(userId)
        return reportsWithUserLiveData
    }

    private fun fetchReports(userId: String? = null) {
        reportsCollection.get().addOnSuccessListener { reportsSnapshot ->
            val reports = reportsSnapshot.documents.map { doc ->
                doc.toObject(Report::class.java)?.apply {
                    id = doc.id
                }
            }
            val reportsWithUser = mutableListOf<ReportWithUser>()

            CoroutineScope(Dispatchers.IO).launch {
                for (report in reports) {
                    if (report?.userId != null) {
                        val userSnapshot = usersCollection.document(report.userId).get().await()
                        if (userSnapshot.exists()) {
                            val user = userSnapshot.toObject(User::class.java)
                            if (user != null) {
                                reportsWithUser.add(
                                    ReportWithUser(
                                        report = report,
                                        userName = user.fullName ?: "unknown user",
                                        userImage = user.imageUri ?: ""
                                    )
                                )
                            }
                        }
                    }
                }
                if (userId != null) {
                    reportsWithUser.removeAll { it.report.userId != userId }
                }
                _reportsWithUserLiveData.postValue(reportsWithUser)
            }
        }
    }

    fun deleteReportById(id: String) {
        reportsCollection.document(id).delete().addOnSuccessListener {
            fetchReports() // Fetch updated reports after deletion
        }
    }

    fun startReportsFetching() {
        reportsRegistration =
            reportsCollection.addSnapshotListener { snapshots: QuerySnapshot?, _: FirebaseFirestoreException? ->
                CoroutineScope(Dispatchers.IO).launch {
                    if (snapshots?.isEmpty == false) {
                        for (dc in snapshots.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED ->
                                    reportDao.insertReport(convertQueryTOReport(dc.document))

                                DocumentChange.Type.MODIFIED -> reportDao.updateReport(
                                    convertQueryTOReport(dc.document)
                                )

                                DocumentChange.Type.REMOVED -> reportDao.deleteReportById(dc.document.id)
                            }
                        }
                    }
                }
            }
    }

    fun endReportsFetching() = reportsRegistration.remove()

    private fun convertQueryTOReport(document: QueryDocumentSnapshot): Report {
        val reportDto = document.toObject(ReportDto::class.java) // Fetching DTOs
        return reportDto.toReport(document.id)
    }

    fun getReportById(id: String): LiveData<Report> {
        val reportLiveData = MutableLiveData<Report>()
        reportsCollection.document(id).get().addOnSuccessListener { documentSnapshot ->
            val report = documentSnapshot.toObject(Report::class.java)
            report?.id = documentSnapshot.id
            reportLiveData.postValue(report!!)
        }
        return reportLiveData
    }

    private suspend fun uploadImage(selectedImageUri: Uri, reportId: String): String =
        ImagePickerHelper.uploadImageToFirebaseStorage(
            selectedImageUri,
            Utils.getReportImageName(reportId)
        ).toString()

    suspend fun updateReport(reportDto: ReportDto, reportId: String, selectedImageUri: Uri?) {
        if (selectedImageUri !== null)
            reportDto.image = uploadImage(selectedImageUri, reportId)
        reportsCollection.document(reportId).set(reportDto.toMap())
    }

    suspend fun addReport(reportDto: ReportDto, selectedImageUri: Uri?) {
        val newReportRef: DocumentReference = reportsCollection.document()
        if (selectedImageUri !== null)
            reportDto.image = uploadImage(selectedImageUri, newReportRef.id)

        newReportRef.set(reportDto.toMap())
    }
}
