package com.example.fakefinder.ui.Activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fakefinder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

    }
}