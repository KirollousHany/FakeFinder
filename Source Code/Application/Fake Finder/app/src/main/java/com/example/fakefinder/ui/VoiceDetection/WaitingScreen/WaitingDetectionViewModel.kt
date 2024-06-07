package com.example.fakefinder.ui.VoiceDetection.WaitingScreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakefinder.Models.ModelDetectLiveData
import com.example.fakefinder.Models.ModelDetectUpload
import com.example.fakefinder.data.repo.IRepo
import com.example.fakefinder.utils.Detection_VOICES
import com.example.fakefinder.utils.ERROR
import com.example.fakefinder.utils.VOICES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WaitingDetectionViewModel @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth, private val storageReference: StorageReference,
    private val repo: IRepo
) : ViewModel() {
    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    private val _detectionResult = MutableLiveData<ModelDetectLiveData>()
    val detectionResult get() = _detectionResult


    private fun uriToFile(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputFile = File.createTempFile("audio", ".wav", context.cacheDir)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        return outputFile
    }

    fun detectAudio(context: Context, uri: Uri, voiceName: String) {
        val file = uriToFile(context, uri)!!
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repo.detection(file)
                if (data.code() == 200) {
                    uploadVoice(uri, data.body()!!.predicted_class, voiceName)
                } else {
                    _validateResult.postValue(ERROR)
                }
            } catch (e: IOException) {
                _validateResult.postValue(e.localizedMessage)
            }

        }
    }

    private fun uploadVoice(uri: Uri, report: String, voiceName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val reference: StorageReference =
                storageReference.child(VOICES)
                    .child(auth.uid.toString())
                    .child(Detection_VOICES)
                    .child(voiceName)
            reference.putFile(uri)
                .addOnSuccessListener {
                    reference.downloadUrl.addOnSuccessListener {
                        sendDataToDatabase(it.toString(), voiceName, report)
                    }
                }.addOnFailureListener {
                    _validateResult.value = it.localizedMessage
                }
        }

    }

    private fun sendDataToDatabase(voiceUrl: String, voiceName: String, report: String) {
        databaseReference.child(VOICES)
            .child(auth.uid.toString())
            .child(Detection_VOICES)
            .child(voiceName)
            .setValue(ModelDetectUpload(voiceUrl, voiceName, report))
            .addOnSuccessListener {
                _detectionResult.value = ModelDetectLiveData(voiceUrl, voiceName, report)
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }

}