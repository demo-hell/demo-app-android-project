package br.com.mobicare.cielo.posVirtual.presentation.home.views

import android.content.Context
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import br.com.cielo.libflue.cardbutton.CieloCardButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct

open class PosMenuItemViewBuilder(
    private val product: PosVirtualProduct,
    private val title: String,
    @DrawableRes private val iconRes: Int,
    private val contentDescription: String? = null,
) {

    private var _listener: ((PosVirtualProduct) -> Unit)? = null

    open fun build(context: Context) = CieloCardButton(context).also { it ->
        it.title = title
        it.icon = iconRes
        it.contentDescription = contentDescription
        it.layoutParams = LinearLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(
                it.marginLeft,
                context.resources.getDimensionPixelOffset(R.dimen.dimen_16dp),
                it.marginRight,
                it.marginBottom
            )
        }
        it.setOnClickListener {
            _listener?.invoke(product)
        }
    }

    fun setOnClickListener(listener: (PosVirtualProduct) -> Unit): PosMenuItemViewBuilder {
        _listener = listener
        return this
    }

}