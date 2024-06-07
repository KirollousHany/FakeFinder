package com.example.fakefinder.ui.VoiceGeneration.UploadVoiceText.FirstScreen

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentVoiceGenerationBinding

class VoiceGenerationFragment : Fragment() {

    private var _binding: FragmentVoiceGenerationBinding? = null
    private val binding get() = _binding!!
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var runnable: Runnable
    private val handler = Handler(Looper.getMainLooper())
    private var voiceUser: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_generation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVoiceGenerationBinding.bind(view)
        onClicks()
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.uploadImage.setOnClickListener {
            pickAudio()
        }
        binding.pickAnotherOneText.setOnClickListener {
            releaseMediaPlayer()
            pickAudio()
            binding.playPauseButton.setImageResource(R.drawable.play_icon)
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

        binding.btnUpload.setOnClickListener {
            findNavController().navigate(
                VoiceGenerationFragmentDirections.actionVoiceGenerationFragmentToVoiceGenerationTextFragment(
                    voiceUser!!
                )
            )
        }

    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data: Intent? = it.data
                if (data != null) {
                    voiceUser = data.data
                    setAudioToMediaPlayer(data)
                } else {
                    Toast.makeText(activity, "Something went wrong try again", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                binding.uploadCardView.visibility = View.VISIBLE
                binding.AudioPlaybackCardView.visibility = View.GONE
                binding.btnUpload.visibility = View.GONE
                binding.pickAnotherOneText.visibility = View.GONE
            }
        }

    private fun setAudioToMediaPlayer(data: Intent?) {
        binding.uploadCardView.visibility = View.GONE
        binding.AudioPlaybackCardView.visibility = View.VISIBLE
        binding.btnUpload.visibility = View.VISIBLE
        binding.pickAnotherOneText.visibility = View.VISIBLE
        mediaPlayer = MediaPlayer.create(activity, data!!.data)
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

    private fun pickAudio() {
        val intent = Intent()
        intent.type = "audio/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        releaseMediaPlayer()
    }

}