package com.example.dailyjapanese

import android.view.View
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.TextView

class PopupHelper {

    companion object{
        fun setupPopup(openButton: View, popup: View, menuDummy: View){

            openButton.setOnClickListener{

                if (popup.visibility == View.GONE)
                {
                    popup.visibility = View.VISIBLE
                    popup.animate().alpha(1f).setDuration(200).start()
                    menuDummy.visibility = View.VISIBLE
                }else{
                    popup.animate().alpha(0f).setDuration(200).withEndAction{
                        popup.visibility = View.GONE
                    }.start()
                    menuDummy.visibility = View.GONE
                }
            }

            menuDummy.setOnClickListener{
                menuDummy.visibility = View.GONE
                popup.animate().alpha(0f).setDuration(200).withEndAction{
                    popup.visibility = View.GONE
                }.start()
            }
        }
//

    }
}