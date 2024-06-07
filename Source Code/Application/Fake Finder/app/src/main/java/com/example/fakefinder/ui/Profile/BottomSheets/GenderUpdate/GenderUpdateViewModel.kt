package com.example.fakefinder.ui.Profile.BottomSheets.GenderUpdate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.utils.GENDER
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_Gender_PROB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenderUpdateViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _userGenderValidate = MutableLiveData<String?>()
    val userGenderValidate get() = _userGenderValidate
    fun validate(gender: String) {
        if (gender.isNullOrEmpty()) {
            _userGenderValidate.value = VAL_Gender_PROB
        } else {
            updateInDataBase(gender)
        }
    }

    private fun updateInDataBase(gender: String) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .child(GENDER)
            .setValue(gender)
            .addOnSuccessListener {
                _userGenderValidate.value = ALL_G
            }.addOnFailureListener {
                _userGenderValidate.value = it.localizedMessage
            }
    }
}