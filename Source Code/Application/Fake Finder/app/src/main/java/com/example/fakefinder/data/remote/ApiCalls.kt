package com.example.fakefinder.data.remote

import com.example.fakefinder.Models.ModelDetectRespond
import com.example.fakefinder.Models.ModelFilterRespond
import com.example.fakefinder.Models.ModelGenerateRespond
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiCalls {

    @Multipart
    @POST("reduce_noise")
    suspend fun voiceFilter(
        @Part audio_file: MultipartBody.Part
    ): Response<ModelFilterRespond>

    @Multipart
    @POST("voice_conversion")
    suspend fun voiceGenerate(
        @Part source_voice: MultipartBody.Part,
        @Part target_voice: MultipartBody.Part
    ): Response<ModelGenerateRespond>

    @Multipart
    @POST("voice_conversion_new")
    suspend fun voiceGenerateNew(
        @Part source_voice: MultipartBody.Part,
        @Part text: MultipartBody.Part,
        @Part language: MultipartBody.Part
    ): Response<ModelGenerateRespond>

    @Multipart
    @POST("predict")
    suspend fun voiceDetection(
        @Part file: MultipartBody.Part,
    ): Response<ModelDetectRespond>


}