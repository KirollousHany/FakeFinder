package com.example.fakefinder.ui.Auth.signup.SecondSignupScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class ProfilePicFragmentViewModel @Inject constructor(
    private val storageReference: StorageReference
) : ViewModel() {
    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

}