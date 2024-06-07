package com.example.fakefinder.ui.History.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentHistoryBinding
import com.example.fakefinder.ui.History.DetectionHistory.DetectionHistoryActivity
import com.example.fakefinder.ui.History.FiltrationHistory.FiltrationHistoryActivity
import com.example.fakefinder.ui.History.GenerationHistory.GenerationHistoryActivity

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)
        onClicks()
    }

    private fun onClicks() {
        binding.voiceDetCardViewHistory.setOnClickListener {
            startActivity(Intent(activity, DetectionHistoryActivity::class.java))
        }
        binding.voiceGenCardViewHistory.setOnClickListener {
            startActivity(Intent(activity, GenerationHistoryActivity::class.java))
        }
        binding.voiceFilterCardViewHistory.setOnClickListener {
            startActivity(Intent(activity, FiltrationHistoryActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}