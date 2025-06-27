package com.example.phonemediasync.worker

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.phonemediasync.data.MediaDatabaseHelper
import java.io.File

class UploadWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val dbHelper = MediaDatabaseHelper(context)

    override fun doWork(): Result {
        Log.d("UploadWorker", "Upload job started")

        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM media_files WHERE status = 'pending'", null)

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex("id"))
                val filePath = it.getString(it.getColumnIndex("file_path"))
                val fileName = it.getString(it.getColumnIndex("file_name"))
                val file = File(filePath)

                if (file.exists()) {
                    val success = uploadToS3Mock(file)

                    if (success) {
                        val values = ContentValues().apply {
                            put("status", "uploaded")
                            put("upload_time", System.currentTimeMillis().toString())
                            put("s3_url", "https://mock-s3.com/bucket/$fileName")
                        }
                        db.update("media_files", values, "id = ?", arrayOf(id.toString()))
                        Log.d("UploadWorker", "Uploaded and updated: $fileName")
                    } else {
                        Log.e("UploadWorker", "Failed to upload: $fileName")
                    }
                } else {
                    Log.w("UploadWorker", "File not found: $filePath")
                }
            }
        }

        return Result.success()
    }

    // 🔁 This is a mock function; we'll replace it with real S3 logic later
    private fun uploadToS3Mock(file: File): Boolean {
        Thread.sleep(500) // Simulate upload time
        return true
    }
}
