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

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import android.os.Build


class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: MediaDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMediaPermissions()

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            val denied = permissions.zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }

            if (denied.isNotEmpty()) {
                Toast.makeText(this, "Some permissions were denied. App may not work correctly.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestMediaPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else {
            // Android 6 to 12
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), 1001)
        }
    }


}
