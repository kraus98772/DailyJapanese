package com.example.dailyjapanese

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class KanaTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana_test)

        //Use only this_notation_for_layout_elements
        val kanaToTest = intent.getSerializableExtra("kanaForTest") as? ArrayList<Kana>
        val currentIndex = Position(0)
        val currentKana = findViewById<TextView>(R.id.currentKana)
        var testResults = ArrayList<Boolean>()
        val inputField = findViewById<EditText>(R.id.input_field)

        if (kanaToTest != null) {
            currentKana.text = kanaToTest[currentIndex.index].kana

            findViewById<EditText>(R.id.input_field).setOnEditorActionListener{ v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    nextKana(currentIndex, inputField, currentKana, testResults, kanaToTest)
                    true
                } else {
                    false
                }
            }

            findViewById<ImageButton>(R.id.next).setOnClickListener{

                nextKana(currentIndex, inputField, currentKana, testResults, kanaToTest)
            }
        }

        findViewById<ImageButton>(R.id.go_back_button).setOnClickListener{
            finish()
        }
    }

    private fun getPercentage(results: ArrayList<Boolean>) : Float
    {
        var correctCounter = 0
        for (result in results)
        {
            if (result)
            {
                correctCounter += 1
            }
        }
        return (correctCounter.toFloat() / results.size.toFloat()) * 100
    }

    private fun nextKana(currentIndex: Position, inputField: EditText, currentKana: TextView, testResults: ArrayList<Boolean>, kanaToTest: ArrayList<Kana>)
    {
        if ((currentIndex.index + 1) < kanaToTest.size)
        {
            testResults.add(isAnswerCorrect(kanaToTest[currentIndex.index].roman, inputField.text.toString()))
            currentIndex.next()
            currentKana.text = kanaToTest[currentIndex.index].kana
            inputField.setText("")
        }
        else if((currentIndex.index + 1) == kanaToTest.size) {
            testResults.add(isAnswerCorrect(kanaToTest[currentIndex.index].roman, inputField.text.toString()))
            currentIndex.next()
            Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAnswerCorrect(expected: String, actual: String) : Boolean
    {
        return expected.lowercase(Locale.ROOT) == actual.lowercase(Locale.ROOT)
    }
}