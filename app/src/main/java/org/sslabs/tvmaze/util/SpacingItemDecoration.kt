package org.sslabs.tvmaze.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.apply {
            top = padding
            left = padding / 2
            right = padding / 2
        }
    }
}
