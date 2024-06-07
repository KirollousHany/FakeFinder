package com.example.fakefinder.ui.Auth.signup.ThirdSignupScreen

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_EMAIL_PROB_FORMAT
import com.example.fakefinder.utils.VAL_NO_PROB

class EmailFragmentViewModel : ViewModel() {

    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    fun validate(email: String) {

        if (email.isEmpty()) {
            _validateResult.value = VAL_EMAIL_PROB
        } else if (!isEmailValid(email)) {
            _validateResult.value = VAL_EMAIL_PROB_FORMAT
        } else {
            _validateResult.value = VAL_NO_PROB
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}