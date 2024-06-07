package com.example.fakefinder.ui.Profile.ProfileInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelInfoChanged
import com.example.fakefinder.Models.ModelUser
import com.example.fakefinder.utils.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(
    private val auth: FirebaseAuth, private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _userInfo = MutableLiveData<ModelUser?>()
    val userInfo get() = _userInfo
    private var _userInfoChanged = MutableLiveData<ModelInfoChanged?>()
    val userInfoChanged get() = _userInfoChanged
    private var childEventListener: ChildEventListener? = null
    private var valueEventListener: ValueEventListener? = null


    fun getUserData() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val modelUser = snapshot.getValue(ModelUser::class.java)
                _userInfo.value = modelUser
                detectDataChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                _userInfo.value = null
            }

        }
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .addValueEventListener(valueEventListener!!)
    }

    private fun detectDataChanged() {
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                _userInfoChanged.value = ModelInfoChanged(snapshot.key, snapshot.value.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                _userInfoChanged.value = null
            }

        }
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .addChildEventListener(childEventListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .removeEventListener(childEventListener!!)
        databaseReference.child(USERS)
            .child(auth.uid.toString())
            .removeEventListener(valueEventListener!!)

    }

}