package com.example.fakefinder.ui.Auth.forgetPass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgetPassFragmentViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _validateEmailAndResetPass = MutableLiveData<String>()
    val validateEmailAndResetPass get() = _validateEmailAndResetPass

    fun validate(email: String) {
        if (email.isNullOrEmpty()) {
            _validateEmailAndResetPass.value = VAL_EMAIL_PROB
        } else {
            resetPass(email)
        }
    }

    private fun resetPass(email: String) {
        auth.sendPasswordResetEmail(email).addOnSuccessListener {
            _validateEmailAndResetPass.value = VAL_NO_PROB
        }.addOnFailureListener {
            _validateEmailAndResetPass.value = it.localizedMessage
        }
    }
}