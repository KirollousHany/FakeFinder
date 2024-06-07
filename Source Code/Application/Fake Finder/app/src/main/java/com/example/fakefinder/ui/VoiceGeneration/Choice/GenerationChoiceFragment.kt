package com.example.fakefinder.ui.VoiceGeneration.Choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentGenerationChoiceBinding

class GenerationChoiceFragment : Fragment() {

    private var _binding: FragmentGenerationChoiceBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generation_choice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGenerationChoiceBinding.bind(view)
        onClicks()
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }
        binding.voiceTextCardView.setOnClickListener {
            findNavController().navigate(GenerationChoiceFragmentDirections.actionGenerationChoiceFragmentToVoiceGenerationFragment())
        }
        binding.voiceVoiceCardView.setOnClickListener {
            findNavController().navigate(GenerationChoiceFragmentDirections.actionGenerationChoiceFragmentToVoiceGenerationTwoVoicesFragmentFirst())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}