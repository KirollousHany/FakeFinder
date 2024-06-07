package com.example.fakefinder.ui.History.FiltrationHistory

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fakefinder.Models.ModelFilterGenerationUpload
import com.example.fakefinder.utils.FILTERED_VOICES
import com.example.fakefinder.utils.VOICES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FiltrationHistoryViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val databaseReference: DatabaseReference
) : ViewModel() {

    private var _voiceList = MutableLiveData<ArrayList<ModelFilterGenerationUpload>>()
    val voiceList get() = _voiceList
    private var valueEventListener: ValueEventListener? = null

    fun getVoices() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val voiceList = ArrayList<ModelFilterGenerationUpload>()

                for (childSnapshot in snapshot.children) {
                    val modelVoice =
                        childSnapshot.getValue(ModelFilterGenerationUpload::class.java)
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
            .child(FILTERED_VOICES)
            .addValueEventListener(valueEventListener!!)
    }

}