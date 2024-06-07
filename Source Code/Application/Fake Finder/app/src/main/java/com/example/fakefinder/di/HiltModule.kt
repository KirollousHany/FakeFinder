package com.example.fakefinder.di

import com.example.fakefinder.data.remote.ApiCalls
import com.example.fakefinder.data.repo.IRepo
import com.example.fakefinder.data.repo.Repo
import com.example.fakefinder.utils.BASE_URL
import com.example.fakefinder.utils.BASE_URL_V2
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HiltModule {
    @Provides
    fun getAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun getRef(): DatabaseReference {
        return Firebase.database.reference
    }

    @Provides
    fun getStorage(): StorageReference {
        return Firebase.storage.reference
    }

    @Singleton
    @Provides
    @Named("ApiCalls1")
    fun getRetrofit(): ApiCalls {
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiCalls::class.java)

    }

    @Singleton
    @Provides
    @Named("ApiCalls2")
    fun getRetrofitV2(): ApiCalls {
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL_V2)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiCalls::class.java)

    }

    @Singleton
    @Provides
    fun getIRep(@Named("ApiCalls1") apiCalls: ApiCalls,@Named("ApiCalls2") apiCallsV2: ApiCalls): IRepo {
        return Repo(apiCalls,apiCallsV2)
    }
}