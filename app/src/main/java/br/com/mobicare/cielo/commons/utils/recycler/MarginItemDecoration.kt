package br.com.mobicare.cielo.commons.utils.recycler

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

private const val FIRST_VALUE = 0

class MarginItemDecoration(private val spaceSize: Int, private val orientation: Int = DividerItemDecoration.VERTICAL) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            val isFirst = parent.getChildAdapterPosition(view) == FIRST_VALUE

            when(orientation) {
                DividerItemDecoration.VERTICAL -> {
                    if(isFirst.not()) top = spaceSize
                }
                DividerItemDecoration.HORIZONTAL -> {
                    if(isFirst.not()) left = spaceSize
                }
            }
        }
    }
}