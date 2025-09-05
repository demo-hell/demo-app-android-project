package br.com.mobicare.cielo.eventTracking.utils

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.mobicare.cielo.R

fun dividerItemDecoration(context: Context) = DividerItemDecoration(
    context,
    DividerItemDecoration.VERTICAL
).apply {
    AppCompatResources.getDrawable(
        context,
        R.drawable.bottom_sheet_cielo_divider_inset
    )?.let {
        setDrawable(it)
    }
}