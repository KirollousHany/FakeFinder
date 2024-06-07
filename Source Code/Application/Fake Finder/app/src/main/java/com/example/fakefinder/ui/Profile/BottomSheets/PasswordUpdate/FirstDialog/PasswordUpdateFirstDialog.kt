package com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.FirstDialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.databinding.PasswordUpdateDialogBinding
import com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.SecondDialog.PasswordUpdateSecondDialog
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordUpdateFirstDialog(private val email: String) : BottomSheetDialogFragment() {

    private var _binding: PasswordUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val passwordUpdateDialogViewModel: PasswordUpdateFirstViewModel by viewModels()

    companion object {
        fun getIncs(email: String) = PasswordUpdateFirstDialog(email)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.password_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = PasswordUpdateDialogBinding.bind(view)
        onClicks()
        observer()
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        passwordUpdateDialogViewModel.passwordValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NO_PROB)) {
                activity?.let { it1 ->
                    PasswordUpdateSecondDialog.getIncs().show(it1.supportFragmentManager, "TAG1")
                }
                dismiss()
            } else if (it.equals(VAL_PASS_PROB)) {
                binding.btnConfirm.text = "Confirm"
                binding.progress.visibility = View.INVISIBLE
                binding.textInputLayoutPassword.error = "Required"
            } else {
                binding.editTxtPassword.setText("")
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
            passwordUpdateDialogViewModel.validatePassword(
                email,
                binding.editTxtPassword.text.toString()
            )
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