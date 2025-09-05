package br.com.mobicare.cielo.minhasVendas.fragments.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ScrollControlledLinearManager(context: Context) : LinearLayoutManager(context) {
    private var isCanScrolled: Boolean = true

    fun setIsCanScroll(isCanScroll: Boolean) {
        this.isCanScrolled = isCanScroll
    }

    override fun canScrollVertically(): Boolean {
        return isCanScrolled && super.canScrollVertically()
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch(e: IndexOutOfBoundsException) {
        }
    }
}