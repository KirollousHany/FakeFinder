package com.example.fakefinder.ui.Profile.BottomSheets.EmailUpdate.SecondDialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.databinding.EmailUpdateNewDialogBinding
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_EMAIL_PROB_FORMAT
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VERIFICATION_EMAIL_SENT
import com.example.fakefinder.utils.WAITING_VERIFICATION
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailUpdateSecondDialog : BottomSheetDialogFragment() {

    private var _binding: EmailUpdateNewDialogBinding? = null
    private val binding get() = _binding!!
    private val emailUpdateSecondDialogViewModel: EmailUpdateSecondDialogViewModel by viewModels()

    companion object {
        fun getIncs() = EmailUpdateSecondDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_update_new_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = EmailUpdateNewDialogBinding.bind(view)
        onClicks()
        observers()
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        emailUpdateSecondDialogViewModel.emailValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NO_PROB)) {
                Toast.makeText(
                    activity,
                    "Please check your inbox to verify your email!",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            } else if (it.equals(VAL_EMAIL_PROB)) {
                binding.btnUpdate.text = "Update"
                binding.progressAfter.visibility = View.GONE
                binding.textInputLayoutEmail.error = "Required"
            } else if (it.equals(VAL_EMAIL_PROB_FORMAT)) {
                binding.btnUpdate.text = "Update"
                binding.progressAfter.visibility = View.GONE
                binding.textInputLayoutEmail.error = "Invalid Format"
            } else if (it.equals(VERIFICATION_EMAIL_SENT)) {
                Toast.makeText(
                    activity,
                    "Please check your email.",
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }else if (it.equals(WAITING_VERIFICATION)){

            }else {
                Toast.makeText(
                    activity,
                    it,
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }

    private fun onClicks() {
        binding.btnUpdate.setOnClickListener {
            binding.btnUpdate.text = ""
            binding.progressAfter.visibility = View.VISIBLE
            emailUpdateSecondDialogViewModel.validateEmail(binding.editTxtEmail.text.toString())
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