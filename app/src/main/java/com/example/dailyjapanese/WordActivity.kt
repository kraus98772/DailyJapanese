package com.example.dailyjapanese

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
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

        val japaneseWord = intent.getStringExtra("japaneseWord")
        val englishWord = intent.getStringExtra("englishWord")
        val kanaScript = intent.getStringExtra("kanaScript")
        val romaji = intent.getStringExtra("romaji")
        val additionalInfo = intent.getStringExtra("additional_info")

        setWord(Word(japaneseWord.toString(), kanaScript.toString(), romaji.toString(), englishWord.toString(), additionalInfo.toString()))

        findViewById<ImageButton>(R.id.go_back_button).setOnClickListener{
            finish()
        }

        val japaneseWordView:TextView = findViewById(R.id.japaneseWord)
        val romajiView:TextView = findViewById(R.id.romaji)

        japaneseWordView.setOnClickListener{
            tts.speak(romajiView.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }


        PopupHelper.setupPopup(
            findViewById(R.id.additional_info_button),
            findViewById(R.id.info_popup),
            findViewById(R.id.menu_dummy)
        )

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
        val infoButton : TextView = findViewById(R.id.additional_info_button)
        val additionalInfo : TextView = findViewById(R.id.info_popup)

        englishWord.text = word.englishWord
        japaneseWord.text = word.japaneseWord
        if(word.kanaScript != "")
        {
            kanaScript.visibility = View.VISIBLE
            kanaScript.text = word.kanaScript
        }else
        {
            kanaScript.visibility = View.GONE
        }

        romaji.text = word.romaji

        if (word.additionalInfo == "")
        {
            infoButton.visibility = View.GONE
        }else{
            infoButton.visibility = View.VISIBLE
            additionalInfo.text = word.additionalInfo
        }
    }
}