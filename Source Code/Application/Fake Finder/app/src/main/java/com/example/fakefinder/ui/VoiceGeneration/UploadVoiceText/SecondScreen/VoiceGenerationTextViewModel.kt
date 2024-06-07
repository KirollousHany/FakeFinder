package com.example.fakefinder.ui.VoiceGeneration.UploadVoiceText.SecondScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_TEXT_PROB

class VoiceGenerationViewModel : ViewModel() {

    private val _validate = MutableLiveData<String>()
    val validate get() = _validate

    fun validation(text: String) {
        if (text.isEmpty()) {
            _validate.value = VAL_TEXT_PROB
        } else {
            _validate.value = text
        }
    }
}