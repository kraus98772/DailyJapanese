package com.example.dailyjapanese

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context : Context, factory : SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                ORIGINAL_WORD_COL + " TEXT, " +
                KANA_COL + " TEXT, " +
                ROMAJI_COL + " TEXT, " +
                ENGLISH_WORD_COL + " TEXT)")

        // Not sure about the null thing
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
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
        private const val DATABASE_NAME = "DAILY_JAPANESE"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "vocabulary"
        const val ID_COL = "id"
        const val ENGLISH_WORD_COL = "english"
        const val ORIGINAL_WORD_COL = "original_japanese"
        const val KANA_COL = "hiragana"
        const val ROMAJI_COL = "romaji"
    }

}