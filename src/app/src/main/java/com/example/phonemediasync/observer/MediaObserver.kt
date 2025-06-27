package com.example.phonemediasync.observer

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import com.example.phonemediasync.data.MediaDatabaseHelper
import java.io.File

class MediaObserver(
    private val context: Context,
    private val dbHelper: MediaDatabaseHelper
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)

        try {
            if (uri != null) {
                val projection = arrayOf(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.DATA
                )

                val cursor = context.contentResolver.query(uri, projection, null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val filePath = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                        val fileName = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))

                        // Insert to SQLite
                        dbHelper.insertMediaFile(fileName, filePath)

                        Log.d("MediaObserver", "New file detected and saved: $fileName")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MediaObserver", "Error reading new media: ${e.message}")
        }
    }
}
