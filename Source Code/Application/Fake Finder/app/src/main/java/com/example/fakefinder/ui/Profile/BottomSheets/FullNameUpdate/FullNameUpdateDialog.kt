package com.example.fakefinder.ui.Profile.BottomSheets.FullNameUpdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_NAME_PROB
import com.example.fakefinder.databinding.FullnameUpdateDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullNameUpdateDialog : BottomSheetDialogFragment() {

    private var _binding: FullnameUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val fullNameUpdateViewModel: FullNameUpdateViewModel by viewModels()

    companion object {
        fun getIncs() = FullNameUpdateDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fullname_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FullnameUpdateDialogBinding.bind(view)
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.text = ""
            binding.progress.visibility = View.VISIBLE
            fullNameUpdateViewModel.validate(binding.editTxtFullName.text.toString())
        }
        binding.editTxtFullName.doAfterTextChanged {
            binding.textInputLayoutFullName.error = ""
        }
    }

    private fun observers() {
        fullNameUpdateViewModel.userFullNameValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_NAME_PROB)) {
                binding.btnSubmit.setText(R.string.submit)
                binding.progress.visibility = View.GONE
                binding.textInputLayoutFullName.error = "Required"
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