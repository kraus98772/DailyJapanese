package com.example.dailyjapanese

import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)

//@Config(constants = BuildConfig, sdk = 34, packageName = "com.example.dailyjapanese")
class DBHelperTest {

    private lateinit var dbHelper: DBHelper
    @Before
    fun setUp() {
        ShadowLog.stream = System.out
        dbHelper = DBHelper(RuntimeEnvironment.getApplication(), null)
    }

    @After
    fun tearDown() {
        dbHelper.close()
    }

    @Test
    fun getDisplayKana() {
        var kana = dbHelper.getDisplayKana(KanaType.MAIN, Kanamoji.hiragana)

        for (i in kana)
        {
            println(i.kana)
        }
        assertTrue(true)
    }

    @Test
    fun getSelectedKana(){
        var displayKana = dbHelper.getDisplayKana(KanaType.MAIN, Kanamoji.hiragana)
//        あ
//        か
//        さ
//        た
//        な
//        は
//        ま
//        や
//        ら
//        わ

        var res = dbHelper.getKanaBySelectedDisplayKana(displayKana, Kanamoji.hiragana)
        for (i in res)
        {
            println(i.kana)
        }
    }


}