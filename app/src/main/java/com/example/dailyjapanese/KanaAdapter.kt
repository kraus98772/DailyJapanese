package com.example.dailyjapanese

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class KanaAdapter(private var allKanaSelectableView: SelectableView, private val context: Context, private val kanaList: ArrayList<Kana>, private val isWide: Boolean) : RecyclerView.Adapter<KanaAdapter.KanaHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KanaHolder {

        var view: View = LayoutInflater.from(context).inflate(R.layout.kana_list_element, parent, false)
        if (isWide){
            view = LayoutInflater.from(context).inflate(R.layout.kana_list_element_wide, parent, false)
        }

        return KanaHolder(view)
    }

    override fun getItemCount(): Int {
        return kanaList.size
    }



    override fun onBindViewHolder(holder: KanaHolder, position: Int) {
        var kana = kanaList[position]
        holder.setDetails(kana.kana + "/" + kana.roman, kana.selected)
        holder.selectableView.setOnClickListener{
            kana.selected = !kana.selected
            if (allKanaSelectableView.isViewSelected())
            {
                allKanaSelectableView.toggleSelect()
            }
            holder.selectableView.toggleSelect()
        }
    }

    class KanaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectableView: SelectableView = itemView.findViewById(R.id.kana_selectable_view)
        fun setDetails(text: String, isSelected: Boolean)
        {
            selectableView.text = text
            if (isSelected)
            {
                selectableView.toggleSelect()
            }
        }
    }
}