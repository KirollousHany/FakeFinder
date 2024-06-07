package com.example.fakefinder.ui.VoiceFiltration.Choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentChoiceFiltrationBinding


class ChoiceUploadRecordFragment : Fragment() {

    private var _binding: FragmentChoiceFiltrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choice_filtration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChoiceFiltrationBinding.bind(view)
        onClicks()
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.voiceRecordCardView.setOnClickListener {
            findNavController().navigate(ChoiceUploadRecordFragmentDirections.actionVoiceFiltrationFragmentToRecordFragment())
        }
        binding.voiceUploadCardView.setOnClickListener {
            findNavController().navigate(ChoiceUploadRecordFragmentDirections.actionVoiceFiltrationFragmentToUploadFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}