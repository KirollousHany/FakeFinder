package com.example.fakefinder.Models

data class ModelFilterRespond(
    val denoised_audio_url: String,
    val message: String,
    val original_data_shape: List<Int>,
    val original_rate: Int
)