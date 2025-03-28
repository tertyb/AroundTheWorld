package com.harelshaigal.aroundtw.ui.userDispaly.userProfile

import OperationStatus
import android.annotation.SuppressLint

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater

import android.view.ViewGroup

import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.harelshaigal.aroundtw.data.report.ReportRepository
import com.harelshaigal.aroundtw.databinding.FragmentUserProfileBinding
import com.harelshaigal.aroundtw.helpers.ImagePickerHelper
import com.squareup.picasso.Picasso
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import com.harelshaigal.aroundtw.helpers.CircleTransform

class UserProfileFragment : Fragment(), ImagePickerHelper.ImagePickerCallback {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var imagePickerHelper: ImagePickerHelper
    private var selectedImageUri: Uri? = null
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val reportRepository: ReportRepository = ReportRepository()
    private lateinit var adapter: UserPostsAdapter



    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[UserProfileViewModel::class.java]
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imagePickerHelper = ImagePickerHelper(this, this)

        binding.userProfileCameraButton.visibility = View.GONE

        binding.userProfileCameraButton.setOnClickListener {
            imagePickerHelper.openImagePicker()
        }

        binding.userPhotosRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = UserPostsAdapter(emptyList()) // Initially empty
        binding.userPhotosRecyclerView.adapter = adapter



        binding.userProfileFullNameEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Hide the keyboard when the "Next" button is pressed
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                true // Return true to indicate the action is handled
            } else {
                false
            }
        }


        viewModel.fetchUserData()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                reportRepository.getAllReports(user.uid).observe(viewLifecycleOwner, Observer { reports ->
                    val size = reports.size

                    binding.userProfilePostsCount.setText( size.toString() + " פוסטים")
                    // Fetch user data

                    viewModel.user.observe(viewLifecycleOwner) { user ->
                        if (user != null) {
                            reportRepository.getAllReports(user.uid).observe(viewLifecycleOwner, Observer { reports ->
                                val postImages = reports.mapNotNull { it.report.image } // Extract image URLs
                                adapter.updatePosts(postImages) // Update adapter data
                            })
                        }
                    }

                })

            }


            binding.userProfileFullNameText.setText(user?.fullName ?: "")
            binding.userProfileFullNameEdit.setText(binding.userProfileFullNameText.text)
            if (user?.imageUri != null) {
                Picasso.get()
                    .load(user.imageUri)
                    .transform(CircleTransform(borderWidth = 6, borderColor = Color.GRAY)) // Add a grey border with custom width
                    .into(binding.userProfileProfileImageView)

                binding.userProfileProfileImageView.visibility = View.VISIBLE
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userProfileSaveButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                updateUser()

                withContext(Dispatchers.Main) {
                    binding.userProfileCameraButton.visibility = View.GONE
                    binding.userProfileSaveButton.visibility = View.GONE
                    binding.userProfileFullNameText.visibility = View.VISIBLE
                    binding.userProfileFullNameEdit .visibility = View.GONE
                }
            }
        }




        binding.userProfileEditButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                // Do any background work here

                // Switch to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    binding.userProfileCameraButton.visibility = View.VISIBLE
                    binding.userProfileSaveButton.visibility = View.VISIBLE
                    binding.userProfileFullNameText.visibility = View.GONE
                    binding.userProfileFullNameEdit .visibility = View.VISIBLE
                }
            }
        }



    viewModel.updateStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                OperationStatus.LOADING -> {
                    binding.registerProgressBar.visibility = View.VISIBLE

                }

                OperationStatus.SUCCESS -> {
                    binding.registerProgressBar.visibility = View.GONE
                    Toast.makeText(context, "שינוי הנתונים נשמר בהצלחה", Toast.LENGTH_SHORT)
                        .show()
                }

                OperationStatus.FAILURE -> {
                    binding.registerProgressBar.visibility =
                        View.GONE
                    Toast.makeText(
                        context,
                        "שגיאה בשמירת הנתונים, נא לנסות שוב",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun updateUser() {
        val newName = binding.userProfileFullNameEdit.text.toString()
        if (newName.nonEmpty()) {
            viewModel.updateUserDetails(newName, selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getImageViewForLoad(): ImageView = binding.userProfileProfileImageView

    override fun selectedImageExtraLogic(uri: Uri?) {
        uri.also {
            selectedImageUri = it
            binding.userProfileProfileImageView.visibility = View.VISIBLE
        }
    }
}
