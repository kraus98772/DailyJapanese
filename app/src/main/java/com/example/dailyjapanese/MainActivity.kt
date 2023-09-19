package com.example.dailyjapanese

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.ERROR
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
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
    private var isMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkDayCounter()

        tts = TextToSpeech(this, this)

        val newWordButton: MaterialButton = findViewById(R.id.newWordButton)

        val wordsRecyclerView:RecyclerView = findViewById(R.id.wordsRecyclerView);
        val wordsArrayList: ArrayList<Word> = ArrayList();
        val adapter: WordAdapter = WordAdapter(this, wordsArrayList);
        wordsRecyclerView.adapter = adapter;
        wordsRecyclerView.layoutManager = LinearLayoutManager(this)

        getData(wordsArrayList, adapter)

        newWordButton.setOnClickListener{
            increaseOffset()
            getData(wordsArrayList, adapter)
        }

        val japaneseWordView:TextView = findViewById(R.id.japaneseWord)
        val kanaScriptView:TextView = findViewById(R.id.kanaScript)
        //TODO: When japanese text is too big it cuts the last symbols
        japaneseWordView.setOnClickListener{
            tts.speak(kanaScriptView.text, TextToSpeech.QUEUE_FLUSH, null, null)
        }

        findViewById<ImageView>(R.id.openDrawerButton).setOnClickListener{
            if (isMenuOpen)
            {
                closeMenu()

            }else{
                openMenu()

            }
        }

        findViewById<View>(R.id.menu_dummy).setOnClickListener{
            closeMenu()
        }

    }

    private fun openMenu()
    {
        var menu = findViewById<LinearLayout>(R.id.menu_popup)
        findViewById<View>(R.id.menu_dummy).visibility = View.VISIBLE
        menu.visibility = View.VISIBLE
        menu.animate().alpha(1.0f).setDuration(200).start()
        isMenuOpen = true
    }

    private fun closeMenu()
    {
        var menu = findViewById<LinearLayout>(R.id.menu_popup)
        findViewById<View>(R.id.menu_dummy).visibility = View.GONE
        menu.animate().alpha(0f).setDuration(200).withEndAction(Runnable {
            menu.visibility = View.GONE
        }).start()
        isMenuOpen = false
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
        val additionalInfo : TextView = findViewById(R.id.additional_info_button)

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
            additionalInfo.visibility = View.GONE
        }else{
            kanaScript.visibility = View.VISIBLE
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
        // TODO: Add offset field if someone wants to generate more words so the day count stays the same
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
        private val PREFERENCES = "day_counter_pref"
        private val DAY_COUNTER = "day_counter"
        private val CURRENT_DATE = "current_date"
        private val DAY_OFFSET = "day_offset"
    }

}