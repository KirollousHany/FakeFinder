package com.example.fakefinder.ui.Auth.signup.FourthSigupScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentPasswordBinding
import com.example.fakefinder.ui.Activities.HomeActivity
import com.example.fakefinder.utils.VAL_CONFPASS_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.example.fakefinder.utils.VAL_PASS_SMALL_PROB
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordFragment : Fragment() {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!
    private val passwordFragmentViewModel: PasswordFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPasswordBinding.bind(view)
        val fullName = PasswordFragmentArgs.fromBundle(requireArguments()).fullname
        val userName = PasswordFragmentArgs.fromBundle(requireArguments()).username
        val gender = PasswordFragmentArgs.fromBundle(requireArguments()).gender
        val img = PasswordFragmentArgs.fromBundle(requireArguments()).imgUri
        val email = PasswordFragmentArgs.fromBundle(requireArguments()).email
        onClicks(fullName, userName, gender, img, email)
        observers()
        handleOnBackPressed(fullName, userName, gender, img)
    }

    private fun handleOnBackPressed(fullName: String, userName: String, gender: String, img: Uri) {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    PasswordFragmentDirections.actionPasswordFragmentToEmailFragment(
                        fullName,
                        userName,
                        gender,
                        img
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

    private fun onClicks(
        fullname: String,
        username: String,
        gender: String,
        img: Uri,
        email: String
    ) {

        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                PasswordFragmentDirections.actionPasswordFragmentToEmailFragment(
                    fullname,
                    username,
                    gender,
                    img
                )
            )
        }
        binding.btnNext.setOnClickListener() {
            binding.btnNext.text = ""
            binding.progress.visibility = View.VISIBLE
            passwordFragmentViewModel.validate(
                binding.editTxtPassword.text.toString(),
                binding.editTxtConfirmPassword.text.toString(),
                fullname, username, email, gender, img
            )
        }
        binding.txtBackToLogin.setOnClickListener {
            findNavController().navigate(
                PasswordFragmentDirections.actionPasswordFragmentToLoginFragment()
            )
        }
        binding.editTxtPassword.doAfterTextChanged {
            binding.textInputLayoutPassword.error = ""
        }
        binding.editTxtConfirmPassword.doAfterTextChanged {
            binding.textInputLayoutConfirmPassword.error = ""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        passwordFragmentViewModel.validateResult.observe(viewLifecycleOwner) {

            if (it.equals(VAL_PASS_PROB)) {
                binding.btnNext.text = "Confirm"
                binding.progress.visibility = View.GONE
                binding.textInputLayoutPassword.error = "Required"
            } else if (it.equals(VAL_CONFPASS_PROB)) {
                binding.btnNext.text = "Confirm"
                binding.progress.visibility = View.GONE
                binding.textInputLayoutConfirmPassword.error = "Doesn't match password"
            } else if (it.equals(VAL_PASS_SMALL_PROB)) {
                binding.btnNext.text = "Confirm"
                binding.progress.visibility = View.GONE
                binding.textInputLayoutPassword.error = "Password must be greater than 6 characters"
            } else if (it.equals(VAL_NO_PROB)) {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                findNavController().navigate(PasswordFragmentDirections.actionPasswordFragmentToLoginFragment())
            }
        }
    }

}