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

        var japaneseWord = intent.getStringExtra("japaneseWord")
        var englishWord = intent.getStringExtra("englishWord")
        var kanaScript = intent.getStringExtra("kanaScript")
        var romaji = intent.getStringExtra("romaji")
        var additionalInfo = intent.getStringExtra("additional_info")

        setWord(Word(japaneseWord.toString(), kanaScript.toString(), romaji.toString(), englishWord.toString(), additionalInfo.toString()))

        var returnButton : ImageButton = findViewById(R.id.returnButton)
        returnButton.setOnClickListener{
            finish()
        }

        val japaneseWordView:TextView = findViewById(R.id.japaneseWord)
        val kanaScriptView:TextView = findViewById(R.id.kanaScript)
        val romajiView:TextView = findViewById(R.id.romaji)

        japaneseWordView.setOnClickListener{
            tts.speak(romajiView.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        var infoButton:TextView = findViewById(R.id.additional_info_button)
        var infoPopup:TextView = findViewById(R.id.info_popup)
        infoButton.setOnClickListener{

            if (infoPopup.visibility == View.GONE)
            {
                infoPopup.visibility = View.VISIBLE
                findViewById<View>(R.id.menu_dummy).visibility = View.VISIBLE
            }else{
                infoPopup.visibility = View.GONE
            }
        }

        findViewById<View>(R.id.menu_dummy).setOnClickListener{
            findViewById<View>(R.id.menu_dummy).visibility = View.GONE
            infoPopup.visibility = View.GONE
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