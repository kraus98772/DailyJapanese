package com.example.dailyjapanese

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton

class KanaSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana_selection)

        var dbHelper = DBHelper(this, null)
        var spacingH = resources.getDimensionPixelSize(R.dimen.kana_spacingH2)
        var spacingV = resources.getDimensionPixelSize(R.dimen.kana_spacingV)

        val kanamoji = intent.getSerializableExtra("kanamoji") as? Kanamoji

        var mainKana = ArrayList<Kana>()
        var dakutenKana = ArrayList<Kana>()
        var combinationKana = ArrayList<Kana>()

        if (kanamoji != null) {
            setupKanaSelection(dbHelper,
                mainKana,
                findViewById(R.id.main_kana_recycler),
                findViewById(R.id.all_main_kana),
                kanamoji, KanaType.MAIN,
                2,
                spacingH, spacingV)

            setupKanaSelection(dbHelper,
                dakutenKana,
                findViewById(R.id.dakuten_kana_recycler),
                findViewById(R.id.all_dakuten_kana),
                kanamoji, KanaType.DAKUTEN,
                1,
                spacingH, spacingV)

            setupKanaSelection(dbHelper,
                combinationKana,
                findViewById(R.id.combination_kana_recycler),
                findViewById(R.id.all_combination_kana),
                kanamoji, KanaType.COMBINATION,
                2,
                spacingH, spacingV)
        }

        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        findViewById<MaterialButton>(R.id.start_the_test_button).setOnClickListener{
            startTheTest(dbHelper, mainKana, dakutenKana, combinationKana)
        }

        findViewById<ImageButton>(R.id.returnButton).setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun setupKanaSelection(dbHelper: DBHelper, kana: ArrayList<Kana>, recyclerView: RecyclerView, allKanaSelectable: SelectableView, kanamoji: Kanamoji, kanaType: KanaType, columnSpan: Int, spacingH: Int, spacingV: Int)
    {
        kana.addAll(dbHelper.getDisplayKana(kanaType, kanamoji))
        val kanaAdapter = KanaAdapter(allKanaSelectable,this, kana, columnSpan == 1)

        recyclerView.adapter = kanaAdapter;
        recyclerView.layoutManager = object: GridLayoutManager(this, columnSpan){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        recyclerView.addItemDecoration(GridSpacingItemDecoration(columnSpan, spacingH, spacingV, true, 0))

        allKanaSelectable.setOnClickListener{
            selectAll(allKanaSelectable, kana, kanaAdapter, recyclerView, columnSpan)
        }
    }

    private fun selectAll(allKanaSelectable: SelectableView, kanaArrayList: ArrayList<Kana>, kanaAdapter: KanaAdapter, recyclerView: RecyclerView, columnSpan: Int)
    {
        if (!allKanaSelectable.isViewSelected())
        {
            for(field in kanaArrayList.indices)
            {
                if (!kanaArrayList[field].selected)
                {
                    kanaArrayList[field].selected = true
                }
            }
        }
        else if(allKanaSelectable.isViewSelected()){
            for(field in kanaArrayList.indices)
            {
                if (kanaArrayList[field].selected)
                {
                    kanaArrayList[field].selected = false
                }
            }
        }
        recyclerView.adapter = kanaAdapter
        recyclerView.layoutManager = GridLayoutManager(this, columnSpan)
        kanaAdapter.notifyDataSetChanged()
        allKanaSelectable.toggleSelect()
    }

    private fun startTheTest(dbHelper: DBHelper, vararg kana: ArrayList<Kana>)
    {
        var kanaForTest = ArrayList<Kana>()
        for (i in kana)
        {
            // CHange the name of the functions
            kanaForTest.addAll(getSelectedKana(i))
        }

        var intent = Intent(this, KanaTestActivity::class.java)
        var finalKana = dbHelper.getSelectedKana(kanaForTest, Kanamoji.hiragana)

        intent.putExtra("kanaForTest", finalKana)
        startActivity(intent)
    }

    private fun getSelectedKana(kana: ArrayList<Kana>) : ArrayList<Kana>
    {
        var selectedKana = ArrayList<Kana>()
        for (i in kana)
        {
            if (i.selected)
            {
                selectedKana.add(i)
            }

        }
        return selectedKana
    }

}