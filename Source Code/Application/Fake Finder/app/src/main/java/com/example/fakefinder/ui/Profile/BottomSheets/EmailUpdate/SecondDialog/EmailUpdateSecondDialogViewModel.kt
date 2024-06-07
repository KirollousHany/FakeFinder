package com.example.fakefinder.ui.Profile.BottomSheets.EmailUpdate.SecondDialog

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakefinder.utils.EMAIL
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_EMAIL_PROB
import com.example.fakefinder.utils.VAL_EMAIL_PROB_FORMAT
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.WAITING_VERIFICATION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EmailUpdateSecondDialogViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _emailValidate = MutableLiveData<String?>()
    val emailValidate get() = _emailValidate

    fun validateEmail(newEmail: String) {
        if (newEmail.isEmpty()) {
            _emailValidate.value = VAL_EMAIL_PROB
        } else if (!isEmailValid(newEmail)) {
            _emailValidate.value = VAL_EMAIL_PROB_FORMAT
        } else {
            verifyBeforeUpdateEmail(newEmail)
        }
    }

    private fun verifyBeforeUpdateEmail(newEmail: String) {
        auth.currentUser!!.verifyBeforeUpdateEmail(newEmail)
            .addOnSuccessListener {
                updateDataBase(newEmail)
            }.addOnFailureListener {
                _emailValidate.value = it.localizedMessage
            }
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkVerification(newEmail: String) {
        var job: Job? = null

        job = viewModelScope.launch {
            while (isActive) {
                delay(5000) // Delay for 15 seconds

                // Reload the user's data
                auth.currentUser?.reload()
                    ?.addOnFailureListener {
                        updateDataBase(newEmail)
                        job?.cancel()
                    }
            }
        }
    }

    private fun updateDataBase(newEmail: String) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .child(EMAIL)
            .setValue(newEmail)
            .addOnSuccessListener {
                _emailValidate.value = VAL_NO_PROB
            }

    }
}