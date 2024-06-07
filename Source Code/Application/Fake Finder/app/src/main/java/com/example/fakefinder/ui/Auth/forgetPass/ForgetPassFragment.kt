package com.example.fakefinder.ui.Auth.forgetPass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.databinding.FragmentForgetPassBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetPassFragment : Fragment() {

    private var _binding: FragmentForgetPassBinding? = null
    private val binding get() = _binding!!
    private val forgetPassFragmentViewModel: ForgetPassFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forget_pass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgetPassBinding.bind(view)
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSendLink.setOnClickListener {
            forgetPassFragmentViewModel.validate(binding.editTxtEmail.text.toString().trim())
        }

        binding.txtResendEmail.setOnClickListener {
            forgetPassFragmentViewModel.validate(binding.editTxtEmail.text.toString().trim())
        }
        binding.editTxtEmail.doAfterTextChanged {
            binding.textInputLayout.error = ""
        }
    }

    private fun observers() {
        forgetPassFragmentViewModel.validateEmailAndResetPass.observe(viewLifecycleOwner) {
            if (it.equals(VAL_EMAIL_PROB)) {
                binding.textInputLayout.error = "Required"
            } else if (it.equals(VAL_NO_PROB)) {
                Toast.makeText(
                    requireActivity(),
                    "Reset password succeed, Check your email",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                binding.txtResendEmail.visibility = View.VISIBLE
                binding.editTxtEmail.text?.clear()
                binding.textInputLayout.error = ""
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}