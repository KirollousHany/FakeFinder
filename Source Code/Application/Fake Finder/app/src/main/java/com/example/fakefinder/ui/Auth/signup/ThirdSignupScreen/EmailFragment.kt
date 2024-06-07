package com.example.fakefinder.ui.Auth.signup.ThirdSignupScreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_EMAIL_PROB_FORMAT
import com.example.fakefinder.databinding.FragmentEmailBinding

class EmailFragment : Fragment() {

    private var _binding: FragmentEmailBinding? = null
    private val binding get() = _binding!!
    private val emailFragmentViewModel: EmailFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEmailBinding.bind(view)
        val fullName = EmailFragmentArgs.fromBundle(requireArguments()).fullname
        val userName = EmailFragmentArgs.fromBundle(requireArguments()).userName
        val gender = EmailFragmentArgs.fromBundle(requireArguments()).gender
        val img = EmailFragmentArgs.fromBundle(requireArguments()).imgUri
        onClick(fullName, userName, gender)
        handleOnBackPressed(fullName, userName, gender)
        observers(fullName, userName, gender, img)
    }

    private fun observers(fullName: String, userName: String, gender: String, img: Uri) {
        emailFragmentViewModel.validateResult.observe(viewLifecycleOwner) {
            if (it.equals(VAL_EMAIL_PROB)) {
                binding.textInputLayoutEmail.error = "Required"
            } else if (it.equals(VAL_EMAIL_PROB_FORMAT)) {
                binding.textInputLayoutEmail.error = "Invalid Format"
            } else {
                findNavController().navigate(
                    EmailFragmentDirections.actionEmailFragmentToPasswordFragment(
                        fullName, userName,
                        binding.editTxtEmail.text.toString(), gender, img
                    )
                )
            }
        }
    }

    private fun handleOnBackPressed(fullName: String, userName: String, gender: String) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    EmailFragmentDirections.actionEmailFragmentToProfilePicFragment(
                        fullName,
                        userName,
                        gender
                    )
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

    private fun onClick(fullName: String, userName: String, gender: String) {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                EmailFragmentDirections.actionEmailFragmentToProfilePicFragment(
                    fullName,
                    userName,
                    gender
                )
            )
        }
        binding.txtBackToLogin.setOnClickListener {
            findNavController().navigate(
                EmailFragmentDirections.actionEmailFragmentToLoginFragment()
            )
        }
        binding.btnNext.setOnClickListener {
            emailFragmentViewModel.validate(binding.editTxtEmail.text.toString())
        }
        binding.editTxtEmail.doAfterTextChanged {
            binding.textInputLayoutEmail.error = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}