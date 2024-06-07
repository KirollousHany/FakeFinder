package com.example.fakefinder.data.repo

import com.example.fakefinder.Models.ModelDetectRespond
import com.example.fakefinder.Models.ModelFilterRespond
import com.example.fakefinder.Models.ModelGenerateRespond
import retrofit2.Response
import java.io.File

interface IRepo {
    suspend fun filter(
        audio_file: File
    ): Response<ModelFilterRespond>

    suspend fun generate(
        source_voice: File,
        target_voice: File
    ): Response<ModelGenerateRespond>

    suspend fun generateNew(
        source_voice: File,
        text: String,
        language: String
    ): Response<ModelGenerateRespond>

    suspend fun detection(
        file : File
    ):Response<ModelDetectRespond>

}