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
        //createListData()

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

        englishWord.text = word.englishWord
        japaneseWord.text = word.japaneseWord
        kanaScript.text = word.kanaScript
        romaji.text = word.romaji
    }

    private fun getData(wordsArrayList: ArrayList<Word>, adapter: WordAdapter)
    {
        println("making db helper")
        val dbHelper = DBHelper(this, null)
        println("db helper made")
        val c = dbHelper.getWord(getDayCounter() + getOffset())
        c?.moveToFirst()
        wordsArrayList.clear()
        while (c?.isAfterLast == false)
        {
            val word = Word(c.getString(1), c.getString(2), c.getString(3), c.getString(4))
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

    private fun createListData() {

        val dbHelper = DBHelper(this, null)
        dbHelper.addWord(Word("週", "しゅう", "shuu", "week"))
        dbHelper.addWord(Word("年", "とし", "toshi", "year"))
        dbHelper.addWord(Word("今日", "きょう", "kyou", "today"))
        dbHelper.addWord(Word("明日", "あす", "asu", "tomorrow"))
        dbHelper.addWord(Word("昨日", "さくじつ", "sakujitsu", "yesterday"))
        dbHelper.addWord(Word("カレンダー", "かれんだー", "karenda-", "calendar"))
        dbHelper.addWord(Word("秒", "びょう", "byou", "second"))
        dbHelper.addWord(Word("時間", "じかん", "jikan", "hour"))
        dbHelper.addWord(Word("分", "ぶん", "bun", "minute"))
        dbHelper.addWord(Word("時", "とき", "toki", "o'clock"))
        dbHelper.addWord(Word("時計", "とけい", "tokei", "clock"))
        dbHelper.addWord(Word("できる", "できる", "dekiru", "can"))
        dbHelper.addWord(Word("使う", "つかう", "tsukau", "use"))
        dbHelper.addWord(Word("する", "する", "suru", "do"))
        dbHelper.addWord(Word("行く", "いく", "iku", "go"))
        dbHelper.addWord(Word("来る", "くる", "kuru", "come"))
        dbHelper.addWord(Word("笑う", "わらう", "warau", "laugh"))
        dbHelper.addWord(Word("作る", "つくる", "tsukuru", "make"))
        dbHelper.addWord(Word("見る", "みる", "miru", "see"))
        dbHelper.addWord(Word("遠い", "とおい", "tooi", "far"))
        dbHelper.addWord(Word("小さい", "ちいさい", "chiisai", "small"))
        dbHelper.addWord(Word("良い", "よい", "yoi", "good"))
        dbHelper.addWord(Word("きれい", "きれい", "kirei", "beautiful"))
        dbHelper.addWord(Word("醜い", "みにくい", "minikui", "ugly"))
        dbHelper.addWord(Word("難しい", "むずかしい", "muzukashii", "difficult"))
        dbHelper.addWord(Word("簡単", "かんたん", "kantan", "easy"))
        dbHelper.addWord(Word("悪い", "わるい", "warui", "bad"))
        dbHelper.addWord(Word("近い", "ちかい", "chikai", "near"))
        dbHelper.addWord(Word("初めまして", "はじめまして", "hajimemashite", "Nice to meet you"))
        dbHelper.addWord(Word("こんにちは", "こんにちは", "kon'nichiha", "Hello"))
        dbHelper.addWord(Word("おはよう", "おはよう", "ohayou", "Good morning"))
        dbHelper.addWord(Word("こんにちは", "こんにちは", "kon'nichiha", "Good afternoon"))
        dbHelper.addWord(Word("こんばんは", "こんばんは", "konbanha", "Good evening"))
        dbHelper.addWord(Word("お休みなさい", "おやすみなさい", "oyasuminasai", "Good night"))
        dbHelper.addWord(Word("お元気ですか", "おげんきですか", "ogenkidesuka", "How are you?"))
        dbHelper.addWord(Word("ありがとう", "ありがとう", "arigatou", "Thank you!"))
        dbHelper.addWord(Word("いいえ", "いいえ", "iie", "No"))
        dbHelper.addWord(Word("美味しい！", "おいしい！", "oishii!", "Delicious!"))
        dbHelper.addWord(Word("私は～です", "わたしは～です", "watashiha～desu", "I'm...(name)"))
        dbHelper.addWord(Word("さようなら", "さようなら", "sayounara", "Goodbye"))
        dbHelper.addWord(Word("はい", "はい", "hai", "Yes"))
        dbHelper.addWord(Word("月曜日", "げつようび", "getsuyoubi", "Monday"))
        dbHelper.addWord(Word("火曜日", "かようび", "kayoubi", "Tuesday"))
        dbHelper.addWord(Word("水曜日", "すいようび", "suiyoubi", "Wednesday"))
        dbHelper.addWord(Word("木曜日", "もくようび", "mokuyoubi", "Thursday"))
        dbHelper.addWord(Word("金曜日", "きんようび", "kin'youbi", "Friday"))
        dbHelper.addWord(Word("土曜日", "どようび", "doyoubi", "Saturday"))
        dbHelper.addWord(Word("日曜日", "にちようび", "nichiyoubi", "Sunday"))
        dbHelper.addWord(Word("五月", "ごがつ", "gogatsu", "May"))
        dbHelper.addWord(Word("一月", "いちがつ", "ichigatsu", "January"))
        dbHelper.addWord(Word("二月", "にがつ", "nigatsu", "February"))
        dbHelper.addWord(Word("三月", "さんがつ", "sangatsu", "March"))
        dbHelper.addWord(Word("四月", "しがつ", "shigatsu", "April"))
        dbHelper.addWord(Word("六月", "ろくがつ", "rokugatsu", "June"))
        dbHelper.addWord(Word("七月", "しちがつ", "shichigatsu", "July"))
        dbHelper.addWord(Word("八月", "はちがつ", "hachigatsu", "August"))
        dbHelper.addWord(Word("九月", "くがつ", "kugatsu", "September"))
        dbHelper.addWord(Word("十月", "じゅうがつ", "juugatsu", "October"))
        dbHelper.addWord(Word("十一月", "じゅういちがつ", "juuichigatsu", "November"))
        dbHelper.addWord(Word("十二月", "じゅうにがつ", "juunigatsu", "December"))
        dbHelper.addWord(Word("零", "れい", "rei", "zero"))
        dbHelper.addWord(Word("一", "いち", "ichi", "one"))
        dbHelper.addWord(Word("二", "に", "ni", "two"))
        dbHelper.addWord(Word("三", "さん", "san", "three"))
        dbHelper.addWord(Word("四", "よん", "yon", "four"))
        dbHelper.addWord(Word("五", "ご", "go", "five"))
        dbHelper.addWord(Word("六", "ろく", "roku", "six"))
        dbHelper.addWord(Word("七", "なな", "nana", "seven"))
        dbHelper.addWord(Word("八", "はち", "hachi", "eight"))
        dbHelper.addWord(Word("九", "きゅー", "kyu-", "nine"))
        dbHelper.addWord(Word("十", "じゅー", "ju-", "ten"))
        dbHelper.addWord(Word("コーヒー", "こーひー", "ko-hi-", "coffee"))
        dbHelper.addWord(Word("ビール", "びーる", "bi-ru", "beer"))
        dbHelper.addWord(Word("茶", "ちゃ", "cha", "tea"))
        dbHelper.addWord(Word("ワイン", "わいん", "wain", "wine"))
        dbHelper.addWord(Word("水", "みず", "mizu", "water"))
        dbHelper.addWord(Word("牛肉", "ぎゅうにく", "gyuuniku", "beef"))
        dbHelper.addWord(Word("豚肉", "ぶたにく", "butaniku", "pork"))
        dbHelper.addWord(Word("鶏肉", "けいにく", "keiniku", "chicken"))
        dbHelper.addWord(Word("ラム肉", "らむにく", "ramuniku", "lamb"))
        dbHelper.addWord(Word("魚", "さかな", "sakana", "fish"))
        dbHelper.addWord(Word("足", "あし", "ashi", "foot"))
        dbHelper.addWord(Word("足", "あし", "ashi", "leg"))
        dbHelper.addWord(Word("頭", "あたま", "atama", "head"))
        dbHelper.addWord(Word("腕", "うで", "ude", "arm"))
        dbHelper.addWord(Word("手", "て", "te", "hand"))
        dbHelper.addWord(Word("指", "ゆび", "yubi", "finger"))
        dbHelper.addWord(Word("体", "からだ", "karada", "body"))
        dbHelper.addWord(Word("胃", "い", "i", "stomach"))
        dbHelper.addWord(Word("背中", "せなか", "senaka", "back"))
        dbHelper.addWord(Word("胸", "むね", "mune", "chest"))
        dbHelper.addWord(Word("看護師", "かんごし", "kangoshi", "nurse"))
        dbHelper.addWord(Word("従業員", "じゅうぎょういん", "juugyouin", "employee"))
        dbHelper.addWord(Word("警察官", "けいさつかん", "keisatsukan", "police officer"))
        dbHelper.addWord(Word("料理人", "りょうりにん", "ryourinin", "cook"))
        dbHelper.addWord(Word("エンジニア", "えんじにあ", "enjinia", "engineer"))
        dbHelper.addWord(Word("医者", "いしゃ", "isha", "doctor"))
        dbHelper.addWord(Word("マネージャー", "まねーじゃー", "mane-ja-", "manager"))
        dbHelper.addWord(Word("先生", "せんせい", "sensei", "teacher"))
        dbHelper.addWord(Word("プログラマー", "ぷろぐらまー", "purogurama-", "programmer"))
        dbHelper.addWord(Word("セールスマン", "せーるすまん", "se-rusuman", "salesman"))

    }

}