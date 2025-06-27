package com.example.phonemediasync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.phonemediasync.data.MediaDatabaseHelper
import com.example.phonemediasync.observer.MediaObserver
import com.example.phonemediasync.worker.UploadWorker

// ✅ Additional imports for UI interaction
import android.widget.Button
import android.content.Intent
import com.example.phonemediasync.ui.SettingsActivity
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MediaDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Set the content view to an XML that contains a LinearLayout with ID main_layout
        setContentView(R.layout.activity_main)

        // ✅ Initialize database helper
        dbHelper = MediaDatabaseHelper(this)

        // ✅ Register media content observer to track new photos/videos
        val observer = MediaObserver(this, dbHelper)
        contentResolver.registerContentObserver(
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )

        // ✅ Schedule periodic work using WorkManager to upload pending media
        val workRequest = PeriodicWorkRequestBuilder<UploadWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        // ✅ Create a "Settings" button programmatically
        val settingsButton = Button(this).apply {
            text = "Settings"
            setOnClickListener {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        // ✅ Add the button to a LinearLayout defined in activity_main.xml
        val layout = findViewById<LinearLayout>(R.id.main_layout)
        layout.addView(settingsButton)
    }
}
