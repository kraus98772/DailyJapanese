package com.example.dailyjapanese

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class KanaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kana)

        var dbHelper = DBHelper(this, null)
        var spacingH = resources.getDimensionPixelSize(R.dimen.kana_spacingH2)
        var spacingV = resources.getDimensionPixelSize(R.dimen.kana_spacingV)


        setupKanaSelection(dbHelper,
            findViewById(R.id.main_kana_recycler),
            findViewById(R.id.all_main_kana),
            Kanamoji.hiragana, KanaType.MAIN,
            2,
            spacingH, spacingV)

        setupKanaSelection(dbHelper,
            findViewById(R.id.dakuten_kana_recycler),
            findViewById(R.id.all_dakuten_kana),
            Kanamoji.hiragana, KanaType.DAKUTEN,
            1,
            spacingH, spacingV)

        setupKanaSelection(dbHelper,
            findViewById(R.id.combination_kana_recycler),
            findViewById(R.id.all_combination_kana),
            Kanamoji.hiragana, KanaType.COMBINATION,
            2,
            spacingH, spacingV)


        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }
    }


    private fun setupKanaSelection(dbHelper: DBHelper, recyclerView: RecyclerView, allKanaSelectable: SelectableView, kanamoji: Kanamoji, kanaType: KanaType, columnSpan: Int, spacingH: Int, spacingV: Int)
    {
        val kana: ArrayList<Kana> = dbHelper.getDisplayKana(kanaType, kanamoji)
        val kanaAdapter = KanaAdapter(allKanaSelectable,this, kana, false)

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

}