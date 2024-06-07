package com.example.fakefinder.ui.Auth.signup.FourthSigupScreen

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelUser
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_CONFPASS_PROB
import com.example.fakefinder.utils.VAL_NO_PROB
import com.example.fakefinder.utils.VAL_PASS_PROB
import com.example.fakefinder.utils.VAL_PASS_SMALL_PROB
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordFragmentViewModel @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth, private val storageReference: StorageReference
) : ViewModel() {

    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    fun validate(
        password: String,
        confirmPassword: String,
        fullName: String,
        username: String,
        email: String,
        gender: String,
        fileImageUser: Uri
    ) {

        if (password.isNullOrEmpty()) {
            _validateResult.value = VAL_PASS_PROB
        } else if (confirmPassword != password) {
            _validateResult.value = VAL_CONFPASS_PROB
        } else if (password.length < 6) {
            _validateResult.value = VAL_PASS_SMALL_PROB
        } else {

            signup(fullName, username, email, password, gender, fileImageUser)
        }

    }

    private fun signup(
        fullName: String, username: String, email: String, password: String,
        gender: String, image: Uri
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            setImageToStorage(fullName, username, email, image, gender)
        }.addOnFailureListener() {
            _validateResult.value = it.localizedMessage
        }
    }

    private fun setImageToStorage(
        fullName: String, username: String, email: String,
        image: Uri, gender: String
    ) {
        storageReference.child("ProfileImages/")
            .child(auth.uid.toString())
            .putFile(image).addOnSuccessListener {
                storageReference.child("ProfileImages/")
                    .child(auth.uid.toString()).downloadUrl.addOnSuccessListener {
                        sendDataToRealTime(fullName, username, email, it.toString(), gender)
                    }
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }

    private fun sendDataToRealTime(
        fullName: String, username: String, email: String,
        imageUrl: String, gender: String
    ) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .setValue(ModelUser(imageUrl, fullName, username, email, auth.uid.toString(), gender))
            .addOnSuccessListener {
                _validateResult.value = VAL_NO_PROB
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }
}