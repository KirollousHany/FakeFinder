package com.example.fakefinder.ui.VoiceFiltration.RecordAudio

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fakefinder.R
import com.example.fakefinder.databinding.FragmentRecordFilterBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RecordFilterFragment : Fragment() {
    private var _binding: FragmentRecordFilterBinding? = null
    private val binding get() = _binding!!
    private var recorder: MediaRecorder? = null
    private var isRecording = false
    private var fileName = ""
    private var audioCreated = false
    private var outputFile: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRecordFilterBinding.bind(view)
        onClicks()
    }

    private fun onClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonRecordPause.setOnClickListener {
            if (checkPermission()) {
                handlePlayRecordButtonClicks()
            } else {
                requestPermission()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat
            .checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.RECORD_AUDIO), 1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0]
                == PackageManager.PERMISSION_GRANTED
            ) {
                handlePlayRecordButtonClicks()
            } else {
                Toast.makeText(activity, "Need permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun handleMediaRecorder() {
        if (audioCreated) {
            val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
            val date = simpleDateFormat.format(Date())
            outputFile = Environment.getExternalStorageDirectory().absolutePath + "/recording.mp3"
            fileName = "audio_recorder_$date"
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile)
            }
            handlePlayRecordButtonClicks()
            audioCreated = true
        } else {
            handlePlayRecordButtonClicks()
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun handlePlayRecordButtonClicks() {
        isRecording = if (isRecording) {
            recorder?.stop()
            binding.buttonRecordPause.setImageResource(R.drawable.recording_icon)
            false
        } else {
            recorder = MediaRecorder()
            val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
            val date = simpleDateFormat.format(Date())
            fileName = "audio_recorder_$date.mp3"
            try {
                recorder?.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(fileName)
                    prepare()
                    start()
                }
            } catch (e: IOException) {
                Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                return
            }

            binding.buttonRecordPause.setImageResource(R.drawable.pause_icon)
            true
        }
    }

    private fun releaseMediaRecorder() {
        recorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (e: RuntimeException) {
                // Handle runtime exception
                Toast.makeText(requireActivity(), e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        recorder = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        releaseMediaRecorder()
    }
}