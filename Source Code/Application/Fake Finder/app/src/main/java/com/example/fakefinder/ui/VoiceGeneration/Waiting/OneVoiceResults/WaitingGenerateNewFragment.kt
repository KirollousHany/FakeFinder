package com.example.fakefinder.ui.VoiceGeneration.Waiting.OneVoiceResults

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
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentWaitingGenerateNewBinding
import com.example.fakefinder.utils.AudioDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaitingGenerateNewFragment : Fragment() {

    private var _binding: FragmentWaitingGenerateNewBinding? = null
    private val binding get() = _binding!!
    private val waitingGenerateNewViewModel: WaitingGenerateNewViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    lateinit var runnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var audioDownloader: AudioDownloader
    private var time: String? = null
    private var url: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waiting_generate_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWaitingGenerateNewBinding.bind(view)
        audioDownloader = AudioDownloader(requireContext())
        val text = WaitingGenerateNewFragmentArgs.fromBundle(requireArguments()).text
        val voiceUri = WaitingGenerateNewFragmentArgs.fromBundle(requireArguments()).voiceUri
        waitingGenerateNewViewModel.generateAudio(voiceUri, text, requireContext())
        observers()
        onClicks()
        handleOnBackPressed()
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
        binding.downloadButton.setOnClickListener {
            audioDownloader.downloadAudio(url!!, time!!)
        }
    }

    private fun observers() {
        waitingGenerateNewViewModel.validateGenerateRespond.observe(viewLifecycleOwner) {
            url = it.voiceUrl
            time = it.name
            handleMediaHideUnhidden()
        }
        waitingGenerateNewViewModel.validateResult.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
            requireActivity().finish()
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

    private fun handleMediaHideUnhidden() {
        CoroutineScope(Dispatchers.Main).launch {
            val job = async(Dispatchers.IO) {
                setAudioToMediaPlayer(url!!)
            }
            // Wait for the task to complete
            job.await()

            // Code inside binding.apply will run after the coroutine completes
            binding.apply {
                waitingIcon.visibility = View.GONE
                textView4.visibility = View.VISIBLE
                AudioPlaybackCardView.visibility = View.VISIBLE
                buttonGoToHome.visibility = View.VISIBLE
            }
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