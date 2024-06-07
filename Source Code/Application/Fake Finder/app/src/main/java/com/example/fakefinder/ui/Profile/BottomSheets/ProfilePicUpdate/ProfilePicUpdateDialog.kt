package com.example.fakefinder.ui.Profile.BottomSheets.ProfilePicUpdate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.R
import com.example.fakefinder.databinding.ProfilePicUpdateDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfilePicUpdateDialog : BottomSheetDialogFragment() {

    private var _binding: ProfilePicUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val profilePicViewModel: ProfilePicViewModel by viewModels()
    private var fileImageUser: Uri? = null

    companion object {
        fun getIncs() = ProfilePicUpdateDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_pic_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ProfilePicUpdateDialogBinding.bind(view)
        onClicks()
        observers()
    }

    private fun observers() {
        profilePicViewModel.updateProfileValidate.observe(viewLifecycleOwner) {
            if (it.equals(ALL_G)) {
                dismiss()
            } else {
                dismiss()
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClicks() {
        binding.imgUpload.setOnClickListener {
            openGallery()
        }
        binding.newProfPic.setOnClickListener {
            openGallery()
        }
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.text = ""
            binding.progress.visibility = View.VISIBLE
            binding.newProfPic.isEnabled = false
            profilePicViewModel.updateProfilePic(fileImageUser!!)
        }
        binding.removeProfilePicText.setOnClickListener {
            binding.removeProfilePicText.text = ""
            binding.progressRemoveProfilePic.visibility = View.VISIBLE
            binding.imgUpload.isEnabled = false
            val drawableResourceId = R.drawable.profile_1
            val imageUri = Uri.parse(
                "android.resource://${requireActivity().packageName}" +
                        "/drawable/${resources.getResourceEntryName(drawableResourceId)}"
            )
            profilePicViewModel.updateProfilePic(imageUri!!)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                if (data != null) {
                    fileImageUser = data.data
                    binding.newProfPic.setImageURI(fileImageUser)
                    binding.imgUpload.visibility = View.GONE
                    binding.btnBefore.visibility = View.GONE
                    binding.removeProfilePicText.visibility = View.GONE
                    binding.newProfPic.visibility = View.VISIBLE
                    binding.btnAfter.visibility = View.INVISIBLE
                    binding.btnSubmit.visibility = View.VISIBLE
                } else {
                    Toast.makeText(activity, "Something went wrong try again", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.imgUpload.visibility = View.VISIBLE
                binding.btnBefore.visibility = View.INVISIBLE
                binding.removeProfilePicText.visibility = View.VISIBLE
                binding.newProfPic.visibility = View.GONE
                binding.btnAfter.visibility = View.GONE
                binding.btnSubmit.visibility = View.GONE
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}