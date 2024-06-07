package com.example.fakefinder.ui.History.GenerationHistory

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fakefinder.databinding.ActivityGenerationHistoryBinding
import com.example.fakefinder.ui.adapters.AdapterRecyclerFiltrationGenerationHistory
import com.example.fakefinder.utils.AudioDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class GenerationHistoryActivity : AppCompatActivity() {
    private var _binding: ActivityGenerationHistoryBinding? = null
    private val binding get() = _binding!!
    private val generationHistoryViewModel: GenerationHistoryViewModel by viewModels()
    private val adapterRecyclerFiltrationGenerationHistory: AdapterRecyclerFiltrationGenerationHistory by lazy {
        AdapterRecyclerFiltrationGenerationHistory()
    }
    private var playedVoicePosition: Int = -1
    private var mediaPlayer: MediaPlayer? = null
    private val audioDownloader = AudioDownloader(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGenerationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generationHistoryViewModel.getVoices()
        onClicks()
        observers()
    }

    private fun observers() {
        generationHistoryViewModel.voiceList.observe(this) {
            adapterRecyclerFiltrationGenerationHistory.voicesList = it
            binding.recyclerGenerate.adapter = adapterRecyclerFiltrationGenerationHistory
        }
    }

    private fun releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    mediaPlayer?.stop()
                }
                mediaPlayer?.release()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        adapterRecyclerFiltrationGenerationHistory.onClick =
            object : AdapterRecyclerFiltrationGenerationHistory.OnClick {
                override fun onPlayPauseClick(url: String, position: Int) {
                    if (playedVoicePosition == position) {
                        if (!mediaPlayer?.isPlaying!!) {
                            mediaPlayer?.start()
                            adapterRecyclerFiltrationGenerationHistory.updatePosition(position)
                        } else {
                            mediaPlayer?.pause()
                            adapterRecyclerFiltrationGenerationHistory.updatePosition(-1)
                        }
                    } else {
                        releaseMediaPlayer()
                        mediaPlayer = MediaPlayer()
                        CoroutineScope(Dispatchers.Main).launch {
                            val job = async(Dispatchers.IO) {
                                mediaPlayerDeclaration(url)
                            }
                            // Wait for the task to complete
                            job.await()

                            // Code inside binding.apply will run after the coroutine completes
                            adapterRecyclerFiltrationGenerationHistory.updatePosition(position)
                            playedVoicePosition = position
                        }
                    }
                    handleWhenMediaPlayerFinish()
                }

                override fun onDownloadClick(url: String, name: String) {
                    audioDownloader.downloadAudio(url, name)
                }

            }
    }

    private suspend fun mediaPlayerDeclaration(url: String) {
        try {
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(
                this@GenerationHistoryActivity,
                "Can not playing audio try again later",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleWhenMediaPlayerFinish() {
        mediaPlayer!!.setOnCompletionListener {
            adapterRecyclerFiltrationGenerationHistory.updatePosition(-1)
            mediaPlayer!!.seekTo(0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        releaseMediaPlayer()
    }
}