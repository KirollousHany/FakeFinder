package com.example.fakefinder.ui.Profile.BottomSheets.UserNameUpdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.R
import com.example.fakefinder.databinding.UsernameUpdateDialogBinding
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.utils.VAL_USERNAME_PROB
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsernameUpdateDialog : BottomSheetDialogFragment() {

    private var _binding: UsernameUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val userNameUpdateViewModel: UsernameUpdateViewModel by viewModels()

    companion object {
        fun getIncs() = UsernameUpdateDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.username_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = UsernameUpdateDialogBinding.bind(view)
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.text = ""
            binding.progress.visibility = View.VISIBLE
            userNameUpdateViewModel.validate(binding.editTxtUserName.text.toString())
        }
        binding.editTxtUserName.doAfterTextChanged {
            binding.textInputLayoutUserName.error = ""
        }
    }

    private fun observers() {
        userNameUpdateViewModel.userNameValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_USERNAME_PROB)) {
                binding.btnSubmit.setText(R.string.submit)
                binding.progress.visibility = View.GONE
                binding.textInputLayoutUserName.error = "Required"
            } else if (it.equals(ALL_G)) {
                dismiss()
            } else {
                dismiss()
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}