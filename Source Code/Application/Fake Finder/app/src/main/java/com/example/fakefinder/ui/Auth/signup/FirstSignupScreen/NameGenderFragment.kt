package com.example.fakefinder.ui.Auth.signup.FirstSignupScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_Gender_PROB
import com.example.fakefinder.utils.VAL_NAME_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_USERNAME_PROB
import com.example.fakefinder.databinding.FragmentNameGenderBinding

class NameGenderFragment : Fragment() {

    private var _binding: FragmentNameGenderBinding? = null
    private val binding get() = _binding!!
    private val nameGenderFragmentViewModel: NameGenderFragmentViewModel by viewModels()
    private var gender: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_name_gender, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNameGenderBinding.bind(view)
        onClicks()
        observers()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    NameGenderFragmentDirections.actionNameGenderFragmentToLoginFragment2()
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

    private fun observers() {
        nameGenderFragmentViewModel.validateResult.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NAME_PROB)) {
                binding.textInputLayoutFullName.error = "Required"
            } else if (it.equals(VAL_USERNAME_PROB)) {
                binding.textInputLayoutUserName.error = "Required"
            } else if (it.equals(VAL_Gender_PROB)) {
                Toast.makeText(activity, "Select your gender", Toast.LENGTH_SHORT).show()
            } else if (it.equals(VAL_NO_PROB)) {
                findNavController().navigate(
                    NameGenderFragmentDirections.actionNameGenderFragmentToProfilePicFragment(
                        binding.editTxtFullName.text.toString(),
                        binding.editTxtUsername.text.toString(),
                        gender!!
                    )
                )
            }
        }
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().navigate(
                NameGenderFragmentDirections.actionNameGenderFragmentToLoginFragment2()
            )
        }
        binding.btnNext.setOnClickListener {
            nameGenderFragmentViewModel.validate(
                binding.editTxtFullName.text.toString(),
                binding.editTxtUsername.text.toString(), gender
            )
        }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(checkedId)
            gender = selectedRadioButton.text.toString()
        }
        binding.txtBackToLogin.setOnClickListener {
            findNavController().navigate(
                NameGenderFragmentDirections.actionNameGenderFragmentToLoginFragment2()
            )
        }
        binding.editTxtUsername.doAfterTextChanged {
            binding.textInputLayoutUserName.error = ""
        }
        binding.editTxtFullName.doAfterTextChanged {
            binding.textInputLayoutFullName.error = ""
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}