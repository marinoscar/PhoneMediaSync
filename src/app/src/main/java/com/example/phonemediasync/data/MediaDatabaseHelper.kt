package com.example.phonemediasync.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class MediaDatabaseHelper(context: Context) : SQLiteOpenHelper(context, "media.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE media_files (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                file_name TEXT,
                file_path TEXT,
                status TEXT,
                upload_time TEXT,
                s3_url TEXT,
                retry_count INTEGER DEFAULT 0
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE app_settings (
                key TEXT PRIMARY KEY,
                value TEXT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS media_files")
        db.execSQL("DROP TABLE IF EXISTS app_settings")
        onCreate(db)
    }

    fun insertMediaFile(fileName: String, filePath: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("file_name", fileName)
            put("file_path", filePath)
            put("status", "pending")
        }
        db.insert("media_files", null, values)
    }

    fun getSetting(key: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT value FROM app_settings WHERE key = ?", arrayOf(key))
        cursor.use {
            if (it.moveToFirst()) {
                return it.getString(0)
            }
        }
        return null
    }

}
