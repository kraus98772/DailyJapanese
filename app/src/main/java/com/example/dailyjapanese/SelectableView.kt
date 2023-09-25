package com.example.dailyjapanese

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet

class SelectableView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatTextView(context, attrs){

    private var selected:Boolean = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SelectableView,
            0, 0
        ).apply {
            try {
                selected = getBoolean(R.styleable.SelectableView_selected, false)
            } finally {
                recycle()
            }
        }
    }

    fun isViewSelected():Boolean
    {
        return selected
    }

    fun toggleSelect()
    {
        if (this.isViewSelected())
        {
            this.setBackgroundResource(R.drawable.kana_select_button_off)
            this.setTextColor(Color.parseColor("#333333"))
        }
        else{
            this.setBackgroundResource(R.drawable.kana_select_button_on)
            this.setTextColor(Color.parseColor("#CCCCCC"))
        }
        selected = !selected
        invalidate()
        requestLayout()
    }

}