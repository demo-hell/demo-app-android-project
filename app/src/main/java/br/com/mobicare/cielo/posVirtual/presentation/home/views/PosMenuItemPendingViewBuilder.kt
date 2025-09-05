package br.com.mobicare.cielo.posVirtual.presentation.home.views

import android.content.Context
import androidx.annotation.DrawableRes
import br.com.cielo.libflue.cardbutton.CieloCardButton
import br.com.cielo.libflue.flextag.CieloFlexTag
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct

class PosMenuItemPendingViewBuilder(
    private val product: PosVirtualProduct,
    title: String,
    @DrawableRes iconRes: Int,
    contentDescription: String? = null,
) : PosMenuItemViewBuilder(product, title, iconRes, contentDescription) {

    private val isCanceled get() = product.status == PosVirtualStatus.CANCELED

    private val tagType
        get() = if (isCanceled) {
            CieloFlexTag.Type.DANGER
        } else {
            CieloFlexTag.Type.WARNING
        }

    override fun build(context: Context) = super.build(context).apply {
        setTagList(
            listOf(
                CieloCardButton.Tag(
                    tagType = tagType,
                    text = context.getString(
                        product.status?.label ?: R.string.pos_virtual_text_failed
                    )
                )
            )
        )
    }

}