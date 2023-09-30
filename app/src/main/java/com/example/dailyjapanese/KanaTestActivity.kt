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
        var kanaToTest = intent.getSerializableExtra("kanaForTest") as? ArrayList<Kana>
        var currentIndex : Int = 0
        var currentKana = findViewById<TextView>(R.id.currentKana)
        var testResults = ArrayList<Boolean>()
        var inputField = findViewById<EditText>(R.id.input_field)

        if (kanaToTest != null) {
            println("Kana to test size: " + kanaToTest.size)
            currentKana.text = kanaToTest[currentIndex].kana

            findViewById<EditText>(R.id.input_field).setOnEditorActionListener{ v, actionId, event ->
                if(actionId == EditorInfo.IME_ACTION_DONE){

                    if ((currentIndex + 1) < kanaToTest.size)
                    {
                        testResults.add(kanaToTest[currentIndex].roman.lowercase(Locale.ROOT) == inputField.text.toString().lowercase(Locale.ROOT))
                        currentIndex += 1
                        currentKana.text = kanaToTest[currentIndex].kana
                        inputField.setText("")
                    }
                    else if((currentIndex + 1) == kanaToTest.size) {
                        testResults.add(kanaToTest[currentIndex].roman.lowercase(Locale.ROOT) == inputField.text.toString().lowercase(Locale.ROOT))
                        currentIndex += 1
                        Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
                    }

                    true
                } else {
                    false
                }
            }

            findViewById<ImageButton>(R.id.next).setOnClickListener{

                if ((currentIndex + 1) < kanaToTest.size)
                {
                    testResults.add(kanaToTest[currentIndex].roman.lowercase(Locale.ROOT) == inputField.text.toString().lowercase(Locale.ROOT))
                    currentIndex += 1
                    currentKana.text = kanaToTest[currentIndex].kana
                    inputField.setText("")
                }
                else if((currentIndex + 1) == kanaToTest.size) {
                    testResults.add(kanaToTest[currentIndex].roman.lowercase(Locale.ROOT) == inputField.text.toString().lowercase(Locale.ROOT))
                    currentIndex += 1
                    Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, String.format("%.2f", getPercentage(testResults)) + "%", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<ImageButton>(R.id.returnButton).setOnClickListener{
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
        println("Correct:$correctCounter")
        println("All:" + results.size)
        return (correctCounter.toFloat() / results.size.toFloat()) * 100
    }

}