package com.example.dailyjapanese

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ToggleButton
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
                findViewById(R.id.all_main_kana_selectable),
                kanamoji, KanaType.MAIN,
                2,
                spacingH, spacingV)

            setupKanaSelection(dbHelper,
                dakutenKana,
                findViewById(R.id.dakuten_kana_recycler),
                findViewById(R.id.all_dakuten_kana_selectable),
                kanamoji, KanaType.DAKUTEN,
                1,
                spacingH, spacingV)

            setupKanaSelection(dbHelper,
                combinationKana,
                findViewById(R.id.combination_kana_recycler),
                findViewById(R.id.all_combination_kana_selectable),
                kanamoji, KanaType.COMBINATION,
                2,
                spacingH, spacingV)
        }

        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        findViewById<MaterialButton>(R.id.start_test_button).setOnClickListener{
            if (kanamoji != null)
            {
                startTheTest(dbHelper, kanamoji, mainKana, dakutenKana, combinationKana)
            }
        }

        findViewById<ImageButton>(R.id.go_back_button).setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<ImageView>(R.id.open_drawer_button).visibility = View.GONE
    }


    private fun setupKanaSelection(dbHelper: DBHelper, kana: ArrayList<Kana>, recyclerView: RecyclerView, allKanaToggle: ToggleButton, kanamoji: Kanamoji, kanaType: KanaType, columnSpan: Int, spacingH: Int, spacingV: Int)
    {
        kana.addAll(dbHelper.getDisplayKana(kanaType, kanamoji))
        val kanaAdapter = KanaAdapter(allKanaToggle,this, kana, columnSpan == 1)

        recyclerView.adapter = kanaAdapter;
        recyclerView.layoutManager = object: GridLayoutManager(this, columnSpan){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }

        recyclerView.addItemDecoration(GridSpacingItemDecoration(columnSpan, spacingH, spacingV, true, 0))

        allKanaToggle.setOnClickListener{
            // the isChecked changes before the if statement is executed that's why the values are inversed
            if (allKanaToggle.isChecked){
                toggleAll(kana, kanaAdapter, recyclerView, columnSpan, true)
            }else
            {
                toggleAll(kana, kanaAdapter, recyclerView, columnSpan, false)
            }
        }
    }

    private fun toggleAll(kanaArrayList: ArrayList<Kana>, kanaAdapter: KanaAdapter, recyclerView: RecyclerView, columnSpan: Int, toggleOn: Boolean)
    {

        for(field in kanaArrayList.indices)
        {
            kanaArrayList[field].selected = toggleOn
        }
        recyclerView.adapter = kanaAdapter
        recyclerView.layoutManager = GridLayoutManager(this, columnSpan)
        kanaAdapter.notifyDataSetChanged()
    }

    private fun startTheTest(dbHelper: DBHelper, kanamoji: Kanamoji, vararg kana: ArrayList<Kana>)
    {
        var kanaForTest = ArrayList<Kana>()
        for (i in kana)
        {
            kanaForTest.addAll(getSelectedDisplayKana(i))
        }

        var intent = Intent(this, KanaTestActivity::class.java)
        var finalKana = dbHelper.getKanaBySelectedDisplayKana(kanaForTest, kanamoji)

        intent.putExtra("kanaForTest", finalKana)
        startActivity(intent)
    }

    private fun getSelectedDisplayKana(kana: ArrayList<Kana>) : ArrayList<Kana>
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