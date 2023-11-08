package com.example.dailyjapanese

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView

class KanaAdapter(private var allKanaToggle: ToggleButton, private val context: Context, private val kanaList: ArrayList<Kana>, private val isWide: Boolean) : RecyclerView.Adapter<KanaAdapter.KanaHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanaHolder {

        var view: View = LayoutInflater.from(context).inflate(R.layout.kana_selectable_list_element, parent, false)
        if (isWide){
            view = LayoutInflater.from(context).inflate(R.layout.kana_selectable_list_element_wide, parent, false)
        }

        return KanaHolder(view)
    }

    override fun getItemCount(): Int {
        return kanaList.size
    }



    override fun onBindViewHolder(holder: KanaHolder, position: Int) {
        var kana = kanaList[position]
        var kanaText = kana.kana + "/" + kana.roman
        holder.setDetails(kanaText, kana.selected)
        holder.kanaToggle.setOnClickListener{
            if (allKanaToggle.isChecked)
            {
                allKanaToggle.isChecked = !allKanaToggle.isChecked
            }
            holder.kanaToggle.text = kanaText
            kana.selected = !kana.selected
        }
    }

    class KanaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var kanaToggle = itemView.findViewById<ToggleButton>(R.id.kana_toggle)
        fun setDetails(text: String, isSelected: Boolean)
        {
            kanaToggle.isChecked = isSelected
            kanaToggle.text = text
        }
    }
}