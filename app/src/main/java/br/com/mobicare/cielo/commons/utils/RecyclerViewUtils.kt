package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat


fun androidx.recyclerview.widget.RecyclerView.configure(context: Context, adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>) {
    this.setHasFixedSize(true)
    this.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    this.adapter = adapter
}

fun androidx.recyclerview.widget.RecyclerView.configureItemDecoration(context: Context,
                                                                      linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager,
                                                                      @DrawableRes drawable: Int) {
    val dividerItemDecor = androidx.recyclerview.widget.DividerItemDecoration(context, linearLayoutManager.orientation)
    dividerItemDecor.setDrawable(ContextCompat
            .getDrawable(context, drawable) as Drawable)
    this.addItemDecoration(dividerItemDecor)
}