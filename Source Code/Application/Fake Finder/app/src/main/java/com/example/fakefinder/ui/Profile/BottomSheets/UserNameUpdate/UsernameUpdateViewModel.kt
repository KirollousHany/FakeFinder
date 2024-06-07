package com.example.fakefinder.ui.Profile.BottomSheets.UserNameUpdate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.utils.USERNAME
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_USERNAME_PROB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsernameUpdateViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _userNameValidate = MutableLiveData<String?>()
    val userNameValidate get() = _userNameValidate
    fun validate(userName: String) {
        if (userName.isEmpty()) {
            _userNameValidate.value = VAL_USERNAME_PROB
        } else {
            updateInDataBase(userName)
        }
    }

    private fun updateInDataBase(userName: String) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .child(USERNAME)
            .setValue(userName)
            .addOnSuccessListener {
                _userNameValidate.value = ALL_G
            }.addOnFailureListener {
                _userNameValidate.value = it.localizedMessage
            }
    }
}