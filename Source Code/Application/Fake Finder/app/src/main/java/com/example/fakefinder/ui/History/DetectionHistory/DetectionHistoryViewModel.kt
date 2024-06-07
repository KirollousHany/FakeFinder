package com.example.fakefinder.ui.History.DetectionHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelDetectUpload
import com.example.fakefinder.utils.Detection_VOICES
import com.example.fakefinder.utils.VOICES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetectionHistoryViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) : ViewModel() {
    private var _voiceList = MutableLiveData<ArrayList<ModelDetectUpload>>()
    val voiceList get() = _voiceList
    private var valueEventListener: ValueEventListener? = null

    fun getVoices() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val voiceList = ArrayList<ModelDetectUpload>()

                for (childSnapshot in snapshot.children) {
                    val modelVoice =
                        childSnapshot.getValue(ModelDetectUpload::class.java)
                    modelVoice?.let {
                        voiceList.add(it)
                    }
                }

                _voiceList.value = voiceList
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        databaseReference.child(VOICES)
            .child(auth.uid.toString())
            .child(Detection_VOICES)
            .addValueEventListener(valueEventListener!!)
    }
}