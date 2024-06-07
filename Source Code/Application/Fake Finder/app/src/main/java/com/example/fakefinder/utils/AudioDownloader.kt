package com.example.fakefinder.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

class AudioDownloader(private val context: Context) {
    fun downloadAudio(audioUrl: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(audioUrl))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$fileName.mp3")

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }
}