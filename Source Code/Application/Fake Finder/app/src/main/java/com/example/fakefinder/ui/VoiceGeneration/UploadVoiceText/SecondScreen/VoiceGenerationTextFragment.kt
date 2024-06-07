package com.example.fakefinder.ui.VoiceGeneration.UploadVoiceText.SecondScreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentVoiceGenerationTextBinding
import com.example.fakefinder.utils.VAL_TEXT_PROB

class VoiceGenerationTextFragment : Fragment() {

    private var _binding: FragmentVoiceGenerationTextBinding? = null
    private val binding get() = _binding!!
    private val voiceGenerationViewModel: VoiceGenerationViewModel by viewModels()
    private var voiceUser: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_generation_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVoiceGenerationTextBinding.bind(view)
        voiceUser = VoiceGenerationTextFragmentArgs.fromBundle(requireArguments()).voiceUri
        onClicks()
        observer()
    }

    private fun observer() {
        voiceGenerationViewModel.validate.observe(viewLifecycleOwner) {
            if (it.equals(VAL_TEXT_PROB)) {
                binding.textInputLayout.error = "Required"
            } else {
                findNavController().navigate(
                    VoiceGenerationTextFragmentDirections.actionVoiceGenerationTextFragmentToWaitingGenerateNewFragment(
                        it,
                        voiceUser!!
                    )
                )
            }
        }
    }

    private fun onClicks() {
        binding.btnUpload.setOnClickListener {
            voiceGenerationViewModel.validation(binding.editTxt.text.toString())
        }
        binding.editTxt.doAfterTextChanged {
            binding.textInputLayout.error = ""
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}