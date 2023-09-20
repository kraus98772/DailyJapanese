package com.example.dailyjapanese

import android.view.View
import android.view.animation.Animation
import android.widget.TextView

class PopupHelper {

    companion object{
        fun setupPopup(openButton: View, popup: View, menuDummy: View, animationOn: Animation?, animationOff: Animation?){

            openButton.setOnClickListener{

                if (popup.visibility == View.GONE)
                {
                    popup.visibility = View.VISIBLE
                    popup.startAnimation(animationOn)
                    menuDummy.visibility = View.VISIBLE
                }else{
                    popup.visibility = View.GONE
                    popup.startAnimation(animationOff)
                }
            }

            menuDummy.setOnClickListener{
                menuDummy.visibility = View.GONE
                popup.visibility = View.GONE
                popup.startAnimation(animationOff)
            }
        }
    }
}