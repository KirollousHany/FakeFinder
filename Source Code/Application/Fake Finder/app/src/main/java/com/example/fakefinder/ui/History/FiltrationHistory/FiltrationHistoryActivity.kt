package com.example.fakefinder.ui.History.FiltrationHistory

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fakefinder.databinding.ActivityFiltrationHistoryBinding
import com.example.fakefinder.ui.adapters.AdapterRecyclerFiltrationGenerationHistory
import com.example.fakefinder.utils.AudioDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class FiltrationHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityFiltrationHistoryBinding? = null
    private val binding get() = _binding!!
    private val filtrationHistoryViewModel: FiltrationHistoryViewModel by viewModels()
    private val adapterRecyclerFiltrationGenerationHistory: AdapterRecyclerFiltrationGenerationHistory by lazy {
        AdapterRecyclerFiltrationGenerationHistory()
    }
    private var playedVoicePosition: Int = -1
    private var mediaPlayer: MediaPlayer? = null
    private val audioDownloader = AudioDownloader(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFiltrationHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        filtrationHistoryViewModel.getVoices()
        onClicks()
        observers()
    }

    private fun observers() {
        filtrationHistoryViewModel.voiceList.observe(this) {
            adapterRecyclerFiltrationGenerationHistory.voicesList = it
            binding.recyclerViewFilter.adapter = adapterRecyclerFiltrationGenerationHistory
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
                            val job = async(IO) {
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
                this@FiltrationHistoryActivity,
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