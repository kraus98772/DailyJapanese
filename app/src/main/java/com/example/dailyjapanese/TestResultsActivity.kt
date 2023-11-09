package com.example.dailyjapanese

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class TestResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_results)

        var spacingH = resources.getDimensionPixelSize(R.dimen.kana_spacingH2)
        var spacingV = resources.getDimensionPixelSize(R.dimen.kana_spacingV)

        val testAnswers = intent.getSerializableExtra("testResults") as? ArrayList<KanaTestAnswer>

        if (testAnswers != null) {
            for (i in testAnswers){
                println("Is correct?" + i.isCorrect)
            }
        }
        var resultsRecyclerView = findViewById<RecyclerView>(R.id.test_results_recycler)

        if (testAnswers != null)
        {
            resultsRecyclerView.adapter = TestResultsAdapter(this, testAnswers)
            resultsRecyclerView.layoutManager = GridLayoutManager(this, 2)
            resultsRecyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingH, spacingV, true, 0))
        }

        findViewById<ImageView>(R.id.open_drawer_button).visibility = View.GONE

        findViewById<ImageView>(R.id.go_back_button).setOnClickListener{
            startActivity(Intent(this, KanaSelectionActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.finish_test_button).setOnClickListener{
            startActivity(Intent(this, KanaSelectionActivity::class.java))
        }
    }
}