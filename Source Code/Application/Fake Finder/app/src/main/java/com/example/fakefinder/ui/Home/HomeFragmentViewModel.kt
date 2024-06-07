package com.example.fakefinder.ui.Home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelUser
import com.example.fakefinder.utils.USERS
import com.example.fakefinder.utils.VAL_GET_NAME_FAILED
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseRef: DatabaseReference
) : ViewModel() {

    private val _userName = MutableLiveData<String>()
    val userName get() = _userName

    fun getUserName() {
        databaseRef.child(USERS).child(auth.uid.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val modelUser = snapshot.getValue(ModelUser::class.java)
                    _userName.value = modelUser?.userName.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    _userName.value = VAL_GET_NAME_FAILED
                }

            })
    }

}