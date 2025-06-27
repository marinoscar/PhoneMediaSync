package com.example.phonemediasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.phonemediasync.data.MediaDatabaseHelper
import com.example.phonemediasync.observer.MediaObserver
import com.example.phonemediasync.worker.UploadWorker

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MediaDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = MediaDatabaseHelper(this)

        // Register content observer
        val observer = MediaObserver(this, dbHelper)
        contentResolver.registerContentObserver(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        // Schedule background upload
        val workRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, java.util.concurrent.TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        val settingsButton = Button(this)
        settingsButton.text = "Settings"
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        (findViewById<LinearLayout>(R.id.main_layout)).addView(settingsButton)


    }
}
