package com.example.fakefinder.ui.Profile.BottomSheets.ProfilePicUpdate

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.utils.ALL_G
import com.example.fakefinder.utils.IMG
import com.example.fakefinder.utils.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfilePicViewModel @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth,
    private val storageReference: StorageReference
) : ViewModel() {

    private val _updateProfileValidate = MutableLiveData<String>()
    val updateProfileValidate get() = _updateProfileValidate

    fun updateProfilePic(fileImageUser: Uri) {
        storageReference.child("ProfileImages/")
            .child(auth.uid.toString())
            .putFile(fileImageUser)
            .addOnSuccessListener {
                storageReference.child("ProfileImages/")
                    .child(auth.uid.toString())
                    .downloadUrl
                    .addOnSuccessListener {
                        updateImgInDataBase(it.toString())
                    }
            }.addOnFailureListener {
                _updateProfileValidate.value = it.localizedMessage
            }
    }

    private fun updateImgInDataBase(imgURL: String) {
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .child(IMG)
            .setValue(imgURL)
            .addOnSuccessListener {
                _updateProfileValidate.value = ALL_G
            }.addOnFailureListener {
                _updateProfileValidate.value = it.localizedMessage
            }

    }

}