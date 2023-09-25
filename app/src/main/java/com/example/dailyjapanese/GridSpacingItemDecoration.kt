package com.example.dailyjapanese

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacingH: Int,
    private val spacingV: Int,
    private val includeEdge: Boolean,
    private val headerNum: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) - headerNum // item position

        if (position >= 0) {
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacingH - column * spacingH / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacingH / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacingV
                }
                outRect.bottom = spacingV // item bottom
            } else {
                outRect.left = column * spacingH / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacingH - (column + 1) * spacingH / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacingV // item top
                }
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }

}