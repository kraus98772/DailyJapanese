package com.example.dailyjapanese

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity(), OnInitListener {

    private lateinit var tts: TextToSpeech
    //private var isMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkDayCounter()

        tts = TextToSpeech(this, this)

        val newWordButton: MaterialButton = findViewById(R.id.newWordButton)

        val wordsRecyclerView:RecyclerView = findViewById(R.id.wordsRecyclerView);
        val wordsArrayList: ArrayList<Word> = ArrayList();
        val adapter = WordAdapter(this, wordsArrayList);

        wordsRecyclerView.adapter = adapter;
        wordsRecyclerView.layoutManager = LinearLayoutManager(this)

        getData(wordsArrayList, adapter)

        newWordButton.setOnClickListener{
            increaseOffset()
            getData(wordsArrayList, adapter)
        }

        val japaneseWordView:TextView = findViewById(R.id.japaneseWord)
        val romajiView:TextView = findViewById(R.id.romaji)

        japaneseWordView.setOnClickListener{
            tts.speak(romajiView.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        PopupHelper.setupPopup(
            findViewById(R.id.additional_info_button),
            findViewById(R.id.info_popup),
            findViewById(R.id.info_dummy)
        )

        PopupHelper.setupPopup(
            findViewById(R.id.open_drawer_button),
            findViewById(R.id.menu_popup),
            findViewById(R.id.menu_dummy)
        )
        setupMenuButtons()

        findViewById<ImageButton>(R.id.go_back_button).visibility = View.GONE
    }


    private fun setupMenuButtons()
    {
        findViewById<MaterialButton>(R.id.hiragana_button).setOnClickListener{
            var intent = Intent(this, KanaSelectionActivity::class.java)
            intent.putExtra(KANAMOJI, Kanamoji.hiragana)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.katakana_button).setOnClickListener{
            var intent = Intent(this, KanaSelectionActivity::class.java)
            intent.putExtra(KANAMOJI, Kanamoji.katakana)
            startActivity(intent)
        }
    }

    override fun onInit(status: Int) {
        if(status != ERROR)
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

    private fun setMainWord(word: Word)
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

    private fun getData(wordsArrayList: ArrayList<Word>, adapter: WordAdapter)
    {
        val dbHelper = DBHelper(this, null)
        val c = dbHelper.getWord(getDayCounter() + getOffset())
        c?.moveToFirst()
        wordsArrayList.clear()
        while (c?.isAfterLast == false)
        {
            val word = Word(c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5))
            wordsArrayList.add(word)
            c.moveToNext()
        }
        setMainWord(wordsArrayList.last())
        wordsArrayList.removeLast()
        wordsArrayList.reverse()
        adapter.notifyDataSetChanged()
    }

    private fun initializeDayCounter(sharedPreferences: SharedPreferences, editor: Editor)
    {
        editor.putInt(DAY_COUNTER, 1)
        editor.putString(CURRENT_DATE, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        editor.apply()
    }

    private fun goToNextDay(sharedPreferences: SharedPreferences, editor: Editor){
        val counter = sharedPreferences.getInt(DAY_COUNTER, 0)
        editor.putInt(DAY_COUNTER, counter+1)
        editor.putString(CURRENT_DATE, LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        editor.apply()
    }

    private fun increaseOffset()
    {
        val sharedPreference = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        if (!sharedPreference.contains(DAY_OFFSET))
        {
            editor.putInt(DAY_OFFSET, 1)
        }else
        {
            editor.putInt(DAY_OFFSET, sharedPreference.getInt(DAY_OFFSET, 0) + 1)
        }
        editor.apply()
    }

    private fun getDayCounter(): Int {
        return getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getInt(DAY_COUNTER, 0)
    }

    private fun getOffset(): Int {
        return getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getInt(DAY_OFFSET, 0)
    }

    private fun checkDayCounter()
    {
        val sharedPreference = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        if (!sharedPreference.contains(DAY_COUNTER) && !sharedPreference.contains(CURRENT_DATE))
        {
            initializeDayCounter(sharedPreference, editor)
        }

        val date = sharedPreference.getString(CURRENT_DATE, null)
        if (date != null)
        {
            val storedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE)
            val currentDate = LocalDate.now()
            if (currentDate.isAfter(storedDate))
            {
                goToNextDay(sharedPreference, editor)
            }
        }

    }

    companion object{
        private const val PREFERENCES = "day_counter_pref"
        private const val DAY_COUNTER = "day_counter"
        private const val CURRENT_DATE = "current_date"
        private const val DAY_OFFSET = "day_offset"
        private const val KANAMOJI = "kanamoji"
    }

}