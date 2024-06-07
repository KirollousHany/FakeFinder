package com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.FirstDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordUpdateFirstViewModel @Inject constructor(private val auth: FirebaseAuth) :
    ViewModel() {

    private var _passwordValidate = MutableLiveData<String?>()
    val passwordValidate get() = _passwordValidate

    fun validatePassword(email: String, password: String) {
        if (password.isEmpty()) {
            _passwordValidate.value = VAL_PASS_PROB
        } else {
            accountValidate(email, password)
        }
    }

    private fun accountValidate(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _passwordValidate.value = VAL_NO_PROB
            }.addOnFailureListener {
                _passwordValidate.value = it.localizedMessage
            }

    }
}