package com.example.dailyjapanese

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TestResultsAdapter (private var context: Context, private val kanaTestAnswers: ArrayList<KanaTestAnswer>) :
    RecyclerView.Adapter<TestResultsAdapter.KanaTestAnswerHolder>() {

    class KanaTestAnswerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var resultCard = itemView.findViewById<TextView>(R.id.result_card)
        var kana = itemView.findViewById<TextView>(R.id.kana)
        var wrongAnswer = itemView.findViewById<TextView>(R.id.wrong_answer)
        var correctAnswer = itemView.findViewById<TextView>(R.id.correct_answer)

        fun setDetails(context: Context,kanaTestAnswer: KanaTestAnswer)
        {
            kana.text = kanaTestAnswer.kana.kana
            correctAnswer.text = kanaTestAnswer.expected
            if(kanaTestAnswer.actual != kanaTestAnswer.expected)
            {
                wrongAnswer.text = kanaTestAnswer.actual
                wrongAnswer.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                resultCard.setBackgroundColor(ContextCompat.getColor(context, R.color.wrong_answer))
            }else{
                wrongAnswer.visibility = View.GONE
                resultCard.setBackgroundColor(ContextCompat.getColor(context, R.color.correct_answer))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanaTestAnswerHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.test_result_element, parent, false)
        return KanaTestAnswerHolder(view)
    }

    override fun getItemCount(): Int {
        return kanaTestAnswers.size
    }

    override fun onBindViewHolder(holder: KanaTestAnswerHolder, position: Int) {
        var answer = kanaTestAnswers[position]
        holder.setDetails(context, answer)
    }
}