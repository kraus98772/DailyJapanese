package com.example.dailyjapanese

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordAdapter(private val context: Context, private val words: ArrayList<Word>) : RecyclerView.Adapter<WordAdapter.WordHolder>() {

    class WordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var japaneseWordView: TextView = itemView.findViewById(R.id.japaneseWord)
//        private var kanaScriptView: TextView = itemView.findViewById(R.id.kanaScript)
//        private var romajiView: TextView = itemView.findViewById(R.id.romaji)
        private var englishWordView: TextView = itemView.findViewById(R.id.englishWord)
        var wordView: LinearLayout = itemView.findViewById(R.id.word_view)

        fun setDetails (newWord: Word)
        {
            japaneseWordView.text = newWord.japaneseWord
//            kanaScriptView.text = newWord.kanaScript
//            romajiView.text = newWord.romaji
            englishWordView.text = newWord.englishWord
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.word_list_element, parent, false)
        return WordHolder(view)
    }

    override fun getItemCount(): Int {
        return words.size
    }


    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        var word: Word = words[position];
        holder.setDetails(word)
        holder.wordView.setOnClickListener{
            var intent = Intent(context, WordActivity::class.java)
            var extras = Bundle()
            extras.putString("japaneseWord", word.japaneseWord)
            extras.putString("englishWord", word.englishWord)
            extras.putString("kanaScript", word.kanaScript)
            extras.putString("romaji", word.romaji)
            extras.putString("additional_info", word.additionalInfo)
            intent.putExtras(extras)
            context.startActivity(intent)
        }
    }

}
