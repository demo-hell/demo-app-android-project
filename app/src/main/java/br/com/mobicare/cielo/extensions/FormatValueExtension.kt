package br.com.mobicare.cielo.extensions

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.utils.Utils

fun View.formatarValores(valor: Double, corValor: Boolean) {
    val textValor = this as TypefaceTextView
    val linearParent = (textValor.getParent() as ViewGroup).parent as LinearLayout
    linearParent.visible()
    if (valor == null) {
        linearParent.gone()
        return
    }
    textValor.text = Utils.formatValue(valor)
    if (valor > 0) {
        if (corValor) textValor.setTextColor(ContextCompat.getColor(context, R.color.green))
    } else if (valor < 0) {
        textValor.text = Utils.formatValue(valor)
        if (corValor) textValor.setTextColor(ContextCompat.getColor(context, R.color.red))
    }
}