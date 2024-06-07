package com.example.fakefinder.ui.VoiceDetection.WaitingScreen

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.fakefinder.Models.ModelDetectLiveData
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentWaitingDetectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaitingDetectionFragment : Fragment() {

    private var _binding: FragmentWaitingDetectionBinding? = null
    private val binding get() = _binding!!
    private val waitingDetectionViewModel: WaitingDetectionViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    lateinit var runnable: Runnable
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_waiting_detection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWaitingDetectionBinding.bind(view)
        val voiceUri = WaitingDetectionFragmentArgs.fromBundle(requireArguments()).voiceUri
        val voiceName = WaitingDetectionFragmentArgs.fromBundle(requireArguments()).voiceName
        waitingDetectionViewModel.detectAudio(
            requireContext(),
            voiceUri,
            voiceName
        )
        observers()
        handleOnBackPressed()
        onClicks()
    }

    private fun onClicks() {
        binding.buttonGoToHome.setOnClickListener {
            requireActivity().finish()
        }
        binding.playPauseButton.setOnClickListener {
            if (!mediaPlayer?.isPlaying!!) {
                mediaPlayer?.start()
                binding.playPauseButton.setImageResource(R.drawable.pause_icon)
            } else {
                mediaPlayer?.pause()
                binding.playPauseButton.setImageResource(R.drawable.play_icon)
            }
        }
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if (changed) {
                    mediaPlayer?.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })


    }

    private fun handleOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        // Optional: Remove the callback when the fragment is destroyed
        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                callback.remove()
            }
        })
    }

    private fun observers() {
        waitingDetectionViewModel.validateResult.observe(viewLifecycleOwner) {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        waitingDetectionViewModel.detectionResult.observe(viewLifecycleOwner) {
            handleView(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleView(data: ModelDetectLiveData) {
        CoroutineScope(Dispatchers.Main).launch {
            val job = async(Dispatchers.IO) {
                setAudioToMediaPlayer(data.audioURl)
            }
            // Wait for the task to complete
            job.await()

            // Code inside binding.apply will run after the coroutine completes
            binding.apply {
                WaitingAnimation.visibility = View.GONE
                textView4.visibility = View.VISIBLE
                AudioPlaybackCardView.visibility = View.VISIBLE
                ResultText.visibility = View.VISIBLE
                buttonGoToHome.visibility = View.VISIBLE
                ResultText.text = data.report
            }
        }

    }

    private suspend fun setAudioToMediaPlayer(url: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(url)
        mediaPlayer?.prepare()
        _binding?.seekBar?.max = mediaPlayer?.duration ?: 0
        runnable = Runnable {
            binding.seekBar.progress = mediaPlayer?.currentPosition!!
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)

        mediaPlayer!!.setOnCompletionListener {
            binding.playPauseButton.setImageResource(R.drawable.play_icon)
            binding.seekBar.progress = 0
            mediaPlayer!!.seekTo(0)
        }

    }

    private fun releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.stop()
                }
                mediaPlayer?.release()
                handler.removeCallbacks(runnable)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        releaseMediaPlayer()
    }

}