package com.example.dailyjapanese

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.util.Locale

class KanaTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana_test)

        val kanaToTest = intent.getSerializableExtra("kanaForTest") as? ArrayList<Kana>
        val currentIndex = Position(0)
        val currentKana = findViewById<TextView>(R.id.currentKana)
        var testResults = ArrayList<KanaTestAnswer>()
        val inputField = findViewById<EditText>(R.id.input_field)


        if (kanaToTest != null) {
            createTestAnswersList(testResults, kanaToTest)
            currentKana.text = kanaToTest[currentIndex.index].kana

            findViewById<EditText>(R.id.input_field).setOnEditorActionListener{ v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    setAnswer(testResults)
                    animateKanaAnswer(kanaToTest, currentIndex, inputField, currentKana, testResults)
                    true
                } else {
                    false
                }
            }

            findViewById<ImageButton>(R.id.next).setOnClickListener{
                setAnswer(testResults)
                animateKanaAnswer(kanaToTest, currentIndex, inputField, currentKana, testResults)
            }
        }

        findViewById<ImageButton>(R.id.go_back_button).setOnClickListener{
            finish()
        }

    }


    private fun createTestAnswersList(kanaTestAnswers: ArrayList<KanaTestAnswer>, kanaToTest: ArrayList<Kana>)
    {
        for (kana in kanaToTest)
        {
            kanaTestAnswers.add(KanaTestAnswer(kana, ""))
        }
    }

    private fun setAnswer(kanaTestAnswers: ArrayList<KanaTestAnswer>)
    {
        var answer = findViewById<EditText>(R.id.input_field).text.toString()
        var currentKana = findViewById<TextView>(R.id.currentKana).text.toString()
        for (i in kanaTestAnswers)
        {
            if (i.kana.kana == currentKana)
            {
                i.answer = answer
                i.isCorrect = isAnswerCorrect(i.kana.roman, answer)
            }
        }
    }

    private fun getNextKana(currentIndex: Position, kanaToTest: ArrayList<Kana>, testResults: ArrayList<KanaTestAnswer>){
        var currentKana = findViewById<TextView>(R.id.currentKana)
        var inputField = findViewById<EditText>(R.id.input_field)

        resetKanaColor()
        if ((currentIndex.index + 1) < kanaToTest.size)
        {
            currentIndex.next()
            currentKana.text = kanaToTest[currentIndex.index].kana
            inputField.setText("")
        }
        else if((currentIndex.index + 1) == kanaToTest.size) {

            goToResultsActivity(testResults)
        }
        else
        {
            goToResultsActivity(testResults)
        }
    }

    private fun animateKanaAnswer(kanaToTest: ArrayList<Kana>, currentIndex: Position, inputField: EditText, currentKana: TextView, testResults: ArrayList<KanaTestAnswer>)
    {

        val originalColor = ContextCompat.getColor(this, R.color.onSurface)
        val wrongAnswerColor = ContextCompat.getColor(this, R.color.wrong_answer)
        val correctAnswerColor = ContextCompat.getColor(this, R.color.correct_answer)

        val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(),
            originalColor,
            if (isAnswerCorrect(kanaToTest[currentIndex.index].roman, inputField.text.toString())) correctAnswerColor else wrongAnswerColor)

        colorAnimator.duration = 400

        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            currentKana.setTextColor(animatedColor)
        }

        colorAnimator.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator) {
                getNextKana(currentIndex, kanaToTest, testResults)
            }
        })

        colorAnimator.start()
    }

    private fun goToResultsActivity(testResults: ArrayList<KanaTestAnswer>)
    {
        var intent = Intent(this, TestResultsActivity::class.java)
        intent.putExtra("testResults", testResults)
        startActivity(intent)
    }
    private fun resetKanaColor()
    {
        findViewById<TextView>(R.id.currentKana).setTextColor(ContextCompat.getColor(this, R.color.onSurface))
    }
    private fun isAnswerCorrect(expected: String, actual: String) : Boolean
    {
        return expected.lowercase(Locale.ROOT) == actual.lowercase(Locale.ROOT)
    }

    private fun exitTestResults()
    {
        startActivity(Intent(this, KanaSelectionActivity::class.java))
    }
}