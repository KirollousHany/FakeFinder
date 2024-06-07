package com.example.fakefinder.ui.VoiceFiltration.WaitingScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakefinder.Models.ModelFilterGenerationUpload
import com.example.fakefinder.Models.ModelFilterLiveData
import com.example.fakefinder.data.repo.IRepo
import com.example.fakefinder.utils.BASE_URL_NO
import com.example.fakefinder.utils.ERROR
import com.example.fakefinder.utils.FILTERED_VOICES
import com.example.fakefinder.utils.VOICES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class WaitingFiltrationViewModel @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth, private val storageReference: StorageReference,
    private val repo: IRepo
) : ViewModel() {

    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    private val _validateFilterRespond = MutableLiveData<ModelFilterLiveData>()
    val validateFilterRespond get() = _validateFilterRespond

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

    fun filterAudio(audio: Uri, context: Context, name: String) {
        viewModelScope.launch(IO) {
            val audioFile = uriToFile(context, audio)!!
            try {
                val data = repo.filter(audioFile)
                if (data.code() == 200) {
                    uploadVoice(
                        "$BASE_URL_NO${data.body()!!.denoised_audio_url}",
                        "$name (Filtered)"
                    )
                } else {
                    _validateResult.postValue(ERROR)
                }
            } catch (e: IOException) {
                _validateResult.postValue(e.localizedMessage)
            }
        }
    }

    private fun downloadAudioByteArray(audioUrl: String): ByteArray? {
        var byteArray: ByteArray? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(audioUrl)
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = BufferedInputStream(connection.inputStream)
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
                byteArray = outputStream.toByteArray()
                inputStream.close()
                outputStream.close()
            } else {
                Log.e("TAG", "HTTP error response code: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error downloading audio: ${e.message}")
        } finally {
            connection?.disconnect()
        }
        return byteArray
    }


    private fun uploadVoice(url: String, name: String) {
        viewModelScope.launch(IO) {
            val data = downloadAudioByteArray(url)
            if (data != null) {
                val reference: StorageReference =
                    storageReference.child(VOICES)
                        .child(auth.uid.toString())
                        .child(FILTERED_VOICES)
                        .child(name)
                reference.putBytes(data)
                    .addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener {
                            sendDataToDatabase(it.toString(), name)
                        }
                    }.addOnFailureListener {
                        _validateResult.value = it.localizedMessage
                    }
            } else {
                _validateResult.postValue("Error in converting voice")
            }
        }
    }

    private fun sendDataToDatabase(voiceUrl: String, voiceName: String) {
        databaseReference.child(VOICES)
            .child(auth.uid.toString())
            .child(FILTERED_VOICES)
            .child(voiceName)
            .setValue(ModelFilterGenerationUpload(voiceUrl, voiceName))
            .addOnSuccessListener {
                _validateFilterRespond.value = ModelFilterLiveData(voiceUrl, voiceName)
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }
}