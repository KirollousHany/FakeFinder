package com.example.fakefinder.data.repo

import com.example.fakefinder.data.remote.ApiCalls
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class Repo @Inject constructor(
    @Named("ApiCalls1") private val apiCalls: ApiCalls,
    @Named("ApiCalls2") private val apiCallsV2: ApiCalls
) : IRepo {
    override suspend fun filter(
        audio_file: File
    ) = withContext(IO) {
        apiCalls.voiceFilter(
            audio_file =
            MultipartBody.Part.createFormData(
                "audio_file", audio_file.name, audio_file.asRequestBody()
            )
        )
    }

    override suspend fun generate(
        source_voice: File,
        target_voice: File
    ) = withContext(IO) {
        apiCalls.voiceGenerate(
            source_voice =
            MultipartBody.Part.createFormData(
                "source_voice", source_voice.name, source_voice.asRequestBody()
            ), target_voice =
            MultipartBody.Part.createFormData(
                "target_voice", target_voice.name, target_voice.asRequestBody()
            )
        )

    }

    override suspend fun generateNew(
        source_voice: File,
        text: String,
        language: String
    ) = withContext(IO) {
        apiCalls.voiceGenerateNew(
            source_voice =
            MultipartBody.Part.createFormData(
                "source_voice", source_voice.name, source_voice.asRequestBody()
            ),
            text = MultipartBody.Part.createFormData("text", text),
            language = MultipartBody.Part.createFormData("language", language)
        )
    }

    override suspend fun detection(
        file: File
    ) = withContext(IO) {
        apiCallsV2.voiceDetection(
            file = MultipartBody.Part.createFormData(
                "file", file.name, file.asRequestBody()
            )
        )
    }


}