package com.example.fakefinder.ui.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakefinder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VoiceFilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_filter)
    }
}