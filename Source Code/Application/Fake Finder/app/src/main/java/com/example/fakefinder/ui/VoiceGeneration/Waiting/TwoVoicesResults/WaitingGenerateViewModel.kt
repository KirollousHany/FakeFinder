package com.example.fakefinder.ui.VoiceGeneration.Waiting.TwoVoicesResults

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakefinder.Models.ModelFilterGenerationUpload
import com.example.fakefinder.data.repo.IRepo
import com.example.fakefinder.utils.BASE_URL_NO
import com.example.fakefinder.utils.ERROR
import com.example.fakefinder.utils.GENERATED_VOICES
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WaitingGenerateViewModel @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth, private val storageReference: StorageReference,
    private val repo: IRepo
) : ViewModel() {
    private val _validateResult = MutableLiveData<String>()
    val validateResult get() = _validateResult

    private val _validateGenerateRespond = MutableLiveData<ModelFilterGenerationUpload>()
    val validateGenerateRespond get() = _validateGenerateRespond

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

    fun generateAudio(sourceVoiceUri: Uri, targetVoiceUri: Uri, context: Context) {
        val sourceVoice = uriToFile(context, sourceVoiceUri)!!
        val targetVoice = uriToFile(context, targetVoiceUri)!!
        viewModelScope.launch(IO) {
            try {
                val data = repo.generate(sourceVoice, targetVoice)
                if (data.code() == 200) {
                    uploadVoice(data.body()!!.output_voice_url)
                } else {
                    _validateResult.postValue(ERROR)
                }
            } catch (e: IOException) {
                _validateResult.postValue(e.localizedMessage)
            }
        }
    }

    private fun formatTimestamp(timestamp: String): String {
        val millis = timestamp.toLong()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(millis)
        return sdf.format(date)
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


    fun uploadVoice(url: String) {
        val time = formatTimestamp(System.currentTimeMillis().toString())
        viewModelScope.launch(IO) {
            val data = downloadAudioByteArray("$BASE_URL_NO$url")
            if (data != null) {
                val reference: StorageReference =
                    storageReference.child(VOICES)
                        .child(auth.uid.toString())
                        .child(GENERATED_VOICES)
                        .child(time)
                reference.putBytes(data)
                    .addOnSuccessListener {
                        reference.downloadUrl.addOnSuccessListener {
                            sendDataToDatabase(it.toString(), time)
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
            .child(GENERATED_VOICES)
            .child(voiceName)
            .setValue(ModelFilterGenerationUpload(voiceUrl, voiceName))
            .addOnSuccessListener {
                _validateGenerateRespond.value = ModelFilterGenerationUpload(voiceUrl, voiceName)
            }.addOnFailureListener {
                _validateResult.value = it.localizedMessage
            }
    }

}