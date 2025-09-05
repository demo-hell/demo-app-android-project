package br.com.mobicare.cielo.posVirtual.presentation.home.enum

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId

enum class PosVirtualProductUiContent(
    val id: PosVirtualProductId,
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @DrawableRes val icon: Int,
    @StringRes val contentDescription: Int,
) {

    PIX(
        PosVirtualProductId.PIX,
        R.string.txt_item_qr_code_pix_pos_virtual_home_title,
        R.string.txt_item_qr_code_pix_pos_virtual_home_subtitle,
        R.drawable.ic_payments_qr_code_scan_cloud_300_24_dp,
        R.string.content_description_item_qr_code_pix_pos_virtual_home,
    ),

    TAP_ON_PHONE(
        PosVirtualProductId.TAP_ON_PHONE,
        R.string.txt_item_cielo_tap_pos_virtual_home_title,
        R.string.txt_item_cielo_tap_pos_virtual_home_subtitle,
        R.drawable.ic_cielo_system_cielo_tap_cloud_400_24_dp,
        R.string.content_description_item_cielo_tap_pos_virtual_home,
    ),

    SUPER_LINK(
        PosVirtualProductId.SUPERLINK_ADDITIONAL,
        R.string.txt_item_payment_link_pos_virtual_home_title,
        R.string.txt_item_payment_link_pos_virtual_home_subtitle,
        R.drawable.ic_cielo_system_super_link_cloud_400_24_dp,
        R.string.content_description_item_payment_link_pos_virtual_home,
    );

    companion object {
        fun find(id: PosVirtualProductId?) = values().firstOrNull { it.id == id }
    }

}