package com.example.fakefinder.ui.Profile.BottomSheets.PasswordUpdate.SecondDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordUpdateSecondDialogViewModel @Inject constructor(private val auth: FirebaseAuth) :
    ViewModel() {

    private var _passwordValidate = MutableLiveData<String?>()
    val passwordValidate get() = _passwordValidate

    fun validatePassword(password: String) {
        if (password.isEmpty()) {
            _passwordValidate.value = VAL_PASS_PROB
        } else {
            updatePassword(password)
        }
    }

    private fun updatePassword(password: String) {
        auth.currentUser!!.updatePassword(password)
            .addOnSuccessListener {
                _passwordValidate.value = VAL_NO_PROB
            }.addOnFailureListener {
                _passwordValidate.value = it.localizedMessage
            }

    }
}