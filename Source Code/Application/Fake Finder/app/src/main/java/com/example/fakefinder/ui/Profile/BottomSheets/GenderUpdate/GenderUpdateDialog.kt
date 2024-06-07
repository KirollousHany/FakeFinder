package com.example.fakefinder.ui.Profile.BottomSheets.GenderUpdate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.R
import com.example.fakefinder.utils.VAL_Gender_PROB
import com.example.fakefinder.databinding.GenderUpdateDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderUpdateDialog : BottomSheetDialogFragment() {

    private var _binding: GenderUpdateDialogBinding? = null
    private val binding get() = _binding!!
    private val genderUpdateViewModel: GenderUpdateViewModel by viewModels()
    private var gender: String? = null

    companion object {
        fun getIncs() = GenderUpdateDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.gender_update_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = GenderUpdateDialogBinding.bind(view)
        onClicks()
        observers()
    }

    private fun onClicks() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = binding.root.findViewById<RadioButton>(checkedId)
            gender = selectedRadioButton.text.toString()
        }
        binding.btnSubmit.setOnClickListener {
            binding.btnSubmit.text = ""
            binding.progress.visibility = View.VISIBLE
            genderUpdateViewModel.validate(gender.toString())
        }
    }

    private fun observers() {
        genderUpdateViewModel.userGenderValidate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_Gender_PROB)) {
                binding.btnSubmit.setText(R.string.submit)
                binding.progress.visibility = View.GONE
                Toast.makeText(activity, "Select your gender", Toast.LENGTH_SHORT).show()
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