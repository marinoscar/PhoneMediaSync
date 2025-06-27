package com.example.phonemediasync.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phonemediasync.R
import com.example.phonemediasync.data.MediaDatabaseHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var dbHelper: MediaDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        dbHelper = MediaDatabaseHelper(this)

        val accessKeyInput = findViewById<EditText>(R.id.awsAccessKey)
        val secretKeyInput = findViewById<EditText>(R.id.awsSecretKey)
        val bucketInput = findViewById<EditText>(R.id.s3Bucket)
        val regionInput = findViewById<EditText>(R.id.awsRegion)
        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            saveSetting("aws_access_key", accessKeyInput.text.toString())
            saveSetting("aws_secret_key", secretKeyInput.text.toString())
            saveSetting("s3_bucket", bucketInput.text.toString())
            saveSetting("aws_region", regionInput.text.toString())

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSetting(key: String, value: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("key", key)
            put("value", value)
        }

        db.insertWithOnConflict(
            "app_settings",
            null,
            values,
            android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
        )
    }
}
