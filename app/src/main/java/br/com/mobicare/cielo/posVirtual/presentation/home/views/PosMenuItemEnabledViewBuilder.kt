package br.com.mobicare.cielo.posVirtual.presentation.home.views

import android.content.Context
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct

class PosMenuItemEnabledViewBuilder(
    product: PosVirtualProduct,
    title: String,
    private val subtitle: String,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null,
) : PosMenuItemViewBuilder(product, title, iconRes, contentDescription) {

    override fun build(context: Context) = super.build(context).apply {
        firstSubtitle = subtitle
    }

}