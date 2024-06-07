package com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.SecondDialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.databinding.PasswordUpdateNewDialogBinding
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordUpdateSecondDialog : BottomSheetDialogFragment() {

    private var _binding: PasswordUpdateNewDialogBinding? = null
    private val binding get() = _binding!!
    private val passwordUpdateSecondDialogViewModel: PasswordUpdateSecondDialogViewModel by viewModels()

    companion object {
        fun getIncs() = PasswordUpdateSecondDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.password_update_new_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = PasswordUpdateNewDialogBinding.bind(view)
        onClicks()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        passwordUpdateSecondDialogViewModel.passwordValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NO_PROB)) {
                Toast.makeText(activity, "Your password has been successfully updated!", Toast.LENGTH_SHORT).show()
                dismiss()
            } else if (it.equals(VAL_PASS_PROB)) {
                binding.btnConfirm.text = "Confirm"
                binding.progress.visibility = View.INVISIBLE
                binding.textInputLayoutPassword.error = "Required"
            } else {
                binding.btnConfirm.text = "Confirm"
                binding.progress.visibility = View.INVISIBLE
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClicks() {
        binding.btnConfirm.setOnClickListener {
            binding.btnConfirm.text = ""
            binding.progress.visibility = View.VISIBLE
            passwordUpdateSecondDialogViewModel.validatePassword(binding.editTxtPassword.text.toString())
        }
        binding.editTxtPassword.doAfterTextChanged {
            binding.textInputLayoutPassword.error = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}