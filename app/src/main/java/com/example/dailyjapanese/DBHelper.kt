package com.example.dailyjapanese

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class DBHelper(val context : Context, factory : SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.database_versions",
        Context.MODE_PRIVATE
    )


    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DATABASE_NAME, DATABASE_VERSION)
            apply()
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
        }
    }


    override fun getWritableDatabase(): SQLiteDatabase {
        throw RuntimeException("The $DATABASE_NAME database is not writable.")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
//        val query = ("CREATE TABLE " + TABLE_NAME + " ("
//                + ID_COL + " INTEGER PRIMARY KEY, " +
//                ORIGINAL_WORD_COL + " TEXT, " +
//                KANA_COL + " TEXT, " +
//                ROMAJI_COL + " TEXT, " +
//                ENGLISH_WORD_COL + " TEXT)")
//
//        // Not sure about the null thing
//        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
//        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
//        onCreate(db)
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$DATABASE_NAME")


        try {
            val outputFile = File(context.getDatabasePath(DATABASE_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }


    fun addWord(word : Word) {
        val values = ContentValues()

        values.put(ORIGINAL_WORD_COL, word.japaneseWord)
        values.put(KANA_COL, word.kanaScript)
        values.put(ROMAJI_COL, word.romaji)
        values.put(ENGLISH_WORD_COL, word.englishWord)

        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getWord() : Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getWord(limit: Int) : Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME LIMIT $limit", null)
    }

    companion object {
        private const val DATABASE_NAME = "DAILY_JAPANESE.sqlite3"
        private const val DATABASE_VERSION = 2
        private const val ASSETS_PATH = "databases"

        const val TABLE_NAME = "vocabulary"
        const val ID_COL = "id"
        const val ENGLISH_WORD_COL = "english"
        const val ORIGINAL_WORD_COL = "original_japanese"
        const val KANA_COL = "kana"
        const val ROMAJI_COL = "romaji"
        const val ADDITIONAL_INFO = "additional_info"
    }

}