package com.example.dailyjapanese

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class KanaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana)

        var allMainKanaSelectable = findViewById<SelectableView>(R.id.all_main_kana)
        var allDakutenKanaSelectable = findViewById<SelectableView>(R.id.all_dakuten_kana)
        var allCombinationKanaSelectable = findViewById<SelectableView>(R.id.all_combination_kana)

        var dbHelper = DBHelper(this, null)

        var spacingH = resources.getDimensionPixelSize(R.dimen.kana_spacingH)
        var spacingV = resources.getDimensionPixelSize(R.dimen.kana_spacingV)

        val mainKanaArrayList: ArrayList<Kana> = dbHelper.getDisplayKana(KanaType.MAIN, Kanamoji.hiragana)
        val mainKanaAdapter = KanaAdapter(allMainKanaSelectable,this, mainKanaArrayList, false)
        setupKanaSelectionRecycler(findViewById(R.id.main_kana_recycler), mainKanaAdapter, 2, spacingH, spacingV, allMainKanaSelectable);

        val dakutenArrayList: ArrayList<Kana> = dbHelper.getDisplayKana(KanaType.DAKUTEN, Kanamoji.hiragana)
        val dakutenKanaAdapter = KanaAdapter(allDakutenKanaSelectable,this, dakutenArrayList, true)
        setupKanaSelectionRecycler(findViewById(R.id.dakuten_kana_recycler), dakutenKanaAdapter, 1, 0, spacingV, allDakutenKanaSelectable)

        val combinationArrayList: ArrayList<Kana> = dbHelper.getDisplayKana(KanaType.COMBINATION, Kanamoji.hiragana)
        val combinationKanaAdapter = KanaAdapter(allCombinationKanaSelectable,this, combinationArrayList, false)
        setupKanaSelectionRecycler(findViewById(R.id.combination_kana_recycler), combinationKanaAdapter, 2, spacingH, spacingV, allCombinationKanaSelectable)

        allMainKanaSelectable.setOnClickListener{
            selectAll(allMainKanaSelectable, mainKanaArrayList, mainKanaAdapter, findViewById(R.id.main_kana_recycler), 2)
        }

        allDakutenKanaSelectable.setOnClickListener{
            selectAll(allDakutenKanaSelectable, dakutenArrayList, dakutenKanaAdapter, findViewById(R.id.dakuten_kana_recycler), 1)
        }

        allCombinationKanaSelectable.setOnClickListener{
            selectAll(allCombinationKanaSelectable, combinationArrayList, combinationKanaAdapter, findViewById(R.id.combination_kana_recycler), 2)
        }

    }

    private fun setupKanaSelectionRecycler(recyclerView: RecyclerView, kanaAdapter: KanaAdapter, columnSpan: Int, spacingH: Int, spacingV: Int, allKanaSelector: SelectableView)
    {

        recyclerView.adapter = kanaAdapter;
        recyclerView.layoutManager = GridLayoutManager(this, columnSpan)

        recyclerView.addItemDecoration(GridSpacingItemDecoration(columnSpan, spacingH, spacingV, true, 0))
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
        recyclerView.adapter = kanaAdapter;
        recyclerView.layoutManager = GridLayoutManager(this, columnSpan);
        kanaAdapter.notifyDataSetChanged();
        allKanaSelectable.toggleSelect()
    }

}