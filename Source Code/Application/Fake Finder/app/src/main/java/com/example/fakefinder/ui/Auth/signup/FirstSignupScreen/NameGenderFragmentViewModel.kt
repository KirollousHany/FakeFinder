package com.example.fakefinder.ui.Auth.signup.FirstSignupScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_Gender_PROB
import com.example.fakefinder.utils.VAL_NAME_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_USERNAME_PROB

class NameGenderFragmentViewModel : ViewModel() {

    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    fun validate(fullName: String, username: String, gender: String?) {

        if (fullName.isEmpty()) {
            _validateResult.value = VAL_NAME_PROB
        } else if (username.isEmpty()) {
            _validateResult.value = VAL_USERNAME_PROB
        } else if (gender.isNullOrEmpty()) {
            _validateResult.value = VAL_Gender_PROB
        } else {
            _validateResult.value = VAL_NO_PROB
        }
    }
}