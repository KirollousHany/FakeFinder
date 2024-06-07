package com.example.fakefinder.ui.Profile.BottomSheets.EmailUpdate.FirstDialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.databinding.EmailUpdateDialogBinding
import com.example.fakefinder.ui.Profile.BottomSheets.EmailUpdate.SecondDialog.EmailUpdateSecondDialog
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailUpdateDialog(private val email: String) : BottomSheetDialogFragment() {

    private var _binding: EmailUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val emailUpdateViewModel: EmailUpdateViewModel by viewModels()

    companion object {
        fun getIncs(email: String) = EmailUpdateDialog(email)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = EmailUpdateDialogBinding.bind(view)
        onClicks()
        observers()
    }

    @SuppressLint("SetTextI18n")
    private fun observers() {
        emailUpdateViewModel.passwordValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NO_PROB)) {
                activity?.let { it1 ->
                    EmailUpdateSecondDialog.getIncs().show(it1.supportFragmentManager, "TAG1")
                }
                dismiss()
            } else if (it.equals(VAL_PASS_PROB)) {
                binding.editTxtPassword.setText("")
                binding.btnSubmit.setText(R.string.confirm)
                binding.progress.visibility = View.GONE
                binding.textInputLayoutPassword.error = "Required"
            } else {
                binding.editTxtPassword.setText("")
                binding.btnSubmit.setText(R.string.confirm)
                binding.progress.visibility = View.GONE
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClicks() {
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.text = ""
            binding.progress.visibility = View.VISIBLE
            emailUpdateViewModel.validatePassword(email, binding.editTxtPassword.text.toString())
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