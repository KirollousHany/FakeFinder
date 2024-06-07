package com.example.fakefinder.ui.Auth.signup.SecondSignupScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentProfilePicBinding

class ProfilePicFragment : Fragment() {

    private var _binding: FragmentProfilePicBinding? = null
    private val binding get() = _binding!!
    private var fileImageUser: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_pic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfilePicBinding.bind(view)
        val fullName = ProfilePicFragmentArgs.fromBundle(requireArguments()).fullName
        val userName = ProfilePicFragmentArgs.fromBundle(requireArguments()).userName
        val gender = ProfilePicFragmentArgs.fromBundle(requireArguments()).gender
        onClicks(fullName, userName, gender)
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    ProfilePicFragmentDirections.actionProfilePicFragmentToNameGenderFragment2()
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // Optional: Remove the callback when the fragment is destroyed
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                callback.remove()
            }
        })
    }

    private fun onClicks(fullName: String, userName: String, gender: String) {
        binding.imgProfile.setOnClickListener {
            openGallery()
        }
        binding.btnNext.setOnClickListener {
            findNavController().navigate(
                ProfilePicFragmentDirections.actionProfilePicFragmentToEmailFragment(
                    fullName,
                    userName,
                    gender,
                    fileImageUser!!
                )
            )

        }
        binding.btnSkip.setOnClickListener {
            val drawableResourceId = R.drawable.profile_1
            val imageUri = Uri.parse(
                "android.resource://${requireActivity().packageName}/drawable/${
                    resources.getResourceEntryName(drawableResourceId)
                }"
            )
            findNavController().navigate(
                ProfilePicFragmentDirections.actionProfilePicFragmentToEmailFragment(
                    fullName,
                    userName,
                    gender,
                    imageUri
                )
            )
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                ProfilePicFragmentDirections.actionProfilePicFragmentToNameGenderFragment2()
            )
        }
        binding.txtBackToLogin.setOnClickListener {
            findNavController().navigate(
                ProfilePicFragmentDirections.actionProfilePicFragmentToLoginFragment()
            )
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                if (data != null) {
                    fileImageUser = data.data
                    binding.imgProfile.setImageURI(fileImageUser)
                    binding.imgAdd.visibility = View.GONE
                    binding.btnNext.visibility = View.VISIBLE
                } else {
                    Toast.makeText(activity, "Something went wrong try again", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}