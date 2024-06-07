package com.example.fakefinder.ui.Auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(

    private val auth: FirebaseAuth
) : ViewModel() {
    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    fun validate(email: String, password: String) {
        if (email.isEmpty()) {
            _validateResult.value = VAL_EMAIL_PROB
        } else if (password.isEmpty()) {
            _validateResult.value = VAL_PASS_PROB
        } else {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            _validateResult.value = VAL_NO_PROB
        }.addOnFailureListener {
            _validateResult.value = it.localizedMessage
        }
    }

}