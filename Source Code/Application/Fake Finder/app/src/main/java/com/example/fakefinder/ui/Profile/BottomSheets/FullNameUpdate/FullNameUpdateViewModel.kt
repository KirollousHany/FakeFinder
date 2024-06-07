package com.example.fakefinder.ui.Profile.BottomSheets.FullNameUpdate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.utils.FULL_NAME
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_NAME_PROB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FullNameUpdateViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _userFullNameValidate = MutableLiveData<String?>()
    val userFullNameValidate get() = _userFullNameValidate
    fun validate(fullName: String) {
        if (fullName.isEmpty()) {
            _userFullNameValidate.value = VAL_NAME_PROB
        } else {
            updateInDataBase(fullName)
        }
    }

    private fun updateInDataBase(fullName: String) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .child(FULL_NAME)
            .setValue(fullName)
            .addOnSuccessListener {
                _userFullNameValidate.value = ALL_G
            }.addOnFailureListener {
                _userFullNameValidate.value = it.localizedMessage
            }
    }
}