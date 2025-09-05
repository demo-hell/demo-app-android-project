package br.com.mobicare.cielo.posVirtual.presentation.home.views

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct
import br.com.mobicare.cielo.posVirtual.presentation.home.enum.PosVirtualProductUiContent

class PosMenuItemViewFactory(
    private val context: Context
) {

    fun create(product: PosVirtualProduct, onClick: (PosVirtualProduct) -> Unit) =
        when (product.id) {
            PosVirtualProductId.TAP_ON_PHONE -> createTapOnPhone(product, onClick)
            PosVirtualProductId.PIX -> createQrCodePix(product, onClick)
            PosVirtualProductId.SUPERLINK_ADDITIONAL -> createSuperLink(product, onClick)
            else -> null
        }

    private fun createTapOnPhone(product: PosVirtualProduct, onClick: (PosVirtualProduct) -> Unit) =
        PosVirtualProductUiContent.TAP_ON_PHONE.run {
            buildMenuItem(
                title = title,
                subtitle = subtitle,
                icon = icon,
                contentDescription = contentDescription,
                product = product,
                onClick = onClick
            )
        }

    private fun createQrCodePix(product: PosVirtualProduct, onClick: (PosVirtualProduct) -> Unit) =
        PosVirtualProductUiContent.PIX.run {
            buildMenuItem(
                title = title,
                subtitle = subtitle,
                icon = icon,
                contentDescription = contentDescription,
                product = product,
                onClick = onClick
            )
        }

    private fun createSuperLink(product: PosVirtualProduct, onClick: (PosVirtualProduct) -> Unit) =
        PosVirtualProductUiContent.SUPER_LINK.run {
            buildMenuItem(
                title = title,
                subtitle = subtitle,
                icon = icon,
                contentDescription = contentDescription,
                product = product,
                onClick = onClick
            )
        }

    private fun buildMenuItem(
        @StringRes title: Int,
        @StringRes subtitle: Int,
        @DrawableRes icon: Int,
        @StringRes contentDescription: Int,
        product: PosVirtualProduct,
        onClick: (PosVirtualProduct) -> Unit
    ) =
        if (product.status == PosVirtualStatus.SUCCESS)
            PosMenuItemEnabledViewBuilder(
                product,
                title = context.getString(title),
                subtitle = context.getString(subtitle),
                iconRes = icon,
                contentDescription = context.getString(contentDescription)
            ).run {
                setOnClickListener(onClick)
                build(context)
            }
        else
            PosMenuItemPendingViewBuilder(
                product,
                title = context.getString(title),
                iconRes = icon,
                contentDescription = context.getString(contentDescription)
            ).run {
                setOnClickListener(onClick)
                build(context)
            }

}