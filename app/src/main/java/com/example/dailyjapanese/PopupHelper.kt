package com.example.dailyjapanese

import android.view.View

class PopupHelper {

    companion object{
        // Find a better word for dummy
        fun setupPopup(openButton: View, popup: View, menuDummy: View){

            openButton.setOnClickListener{

                if (popup.visibility == View.GONE)
                {
                    openPopup(popup, menuDummy)
                }else{
                    closePopup(popup, menuDummy)
                }
            }

            menuDummy.setOnClickListener{
                closePopup(popup, menuDummy)
            }
        }

        fun openPopup(popup: View, menuDummy: View)
        {
            popup.visibility = View.VISIBLE
            popup.animate().alpha(1f).setDuration(200).start()
            menuDummy.visibility = View.VISIBLE
        }

        fun closePopup(popup: View, menuDummy: View)
        {
            popup.animate().alpha(0f).setDuration(200).withEndAction{
                popup.visibility = View.GONE
            }.start()
            menuDummy.visibility = View.GONE
        }
    }
}