package com.example.dailyjapanese

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class WordActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)

        tts = TextToSpeech(this, this)

        var japaneseWord = intent.getStringExtra("japaneseWord")
        var englishWord = intent.getStringExtra("englishWord")
        var kanaScript = intent.getStringExtra("kanaScript")
        var romaji = intent.getStringExtra("romaji")

        setWord(Word(japaneseWord.toString(), kanaScript.toString(), romaji.toString(), englishWord.toString()))

        var returnButton : ImageButton = findViewById(R.id.returnButton)
        returnButton.setOnClickListener{
            finish()
        }

        val japaneseWordView:TextView = findViewById(R.id.japaneseWord)
        val kanaScriptView:TextView = findViewById(R.id.kanaScript)
        japaneseWordView.setOnClickListener{
            tts.speak(kanaScriptView.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

    }

    override fun onInit(status: Int) {
        if(status != TextToSpeech.ERROR)
        {
            tts.language = Locale.JAPANESE
            tts.setPitch(1F)
        }else
        {
            Toast.makeText(this, "Something went wrong with text-to-speech", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    private fun setWord(word: Word)
    {
        val englishWord : TextView = findViewById(R.id.englishWord)
        val japaneseWord : TextView = findViewById(R.id.japaneseWord)
        val kanaScript : TextView = findViewById(R.id.kanaScript)
        val romaji : TextView = findViewById(R.id.romaji)

        englishWord.text = word.englishWord
        japaneseWord.text = word.japaneseWord
        kanaScript.text = word.kanaScript
        romaji.text = word.romaji
    }
}