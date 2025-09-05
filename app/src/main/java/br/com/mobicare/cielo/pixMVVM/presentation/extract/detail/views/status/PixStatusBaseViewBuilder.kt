package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString

abstract class PixStatusBaseViewBuilder {

    abstract val context: Context

    protected fun View.applyCustomStyle(
        @ColorRes colorRes: Int,
        @DimenRes radiusRes: Int = R.dimen.dimen_12dp
    ) {
        setCustomDrawable {
            radius = radiusRes
            solidColor = colorRes
        }
    }

    protected fun applyTextStrikeThrough(vararg views: TextView) {
        views.forEach {
            it.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    protected fun getFormattedAmount(value: Double?) = value?.let { (-it).toPtBrWithNegativeRealString() }

    protected fun getString(@StringRes resId: Int) = context.getString(resId)

    data class Content(
        val status: String?,
        val date: String?,
        val label: String?,
        val amount: String?,
        val sentTo: String?,
        val document: String?,
        val payerAnswer: String? = null
    )

    data class Information(
        val channel: String?,
        val merchant: String?
    )

}