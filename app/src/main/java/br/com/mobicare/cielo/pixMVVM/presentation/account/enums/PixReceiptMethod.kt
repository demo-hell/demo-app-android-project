package br.com.mobicare.cielo.pixMVVM.presentation.account.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PixReceiptMethod(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int
) {

    TRANSFER_BY_SALE(
        titleRes = R.string.pix_account_receipt_method_transfer_by_sale_title,
        descriptionRes = R.string.pix_account_receipt_method_transfer_by_sale_description,
        iconRes = R.drawable.ic_arrows_refresh_brand_400_24_dp
    ),

    SCHEDULED_TRANSFER(
        titleRes = R.string.pix_account_receipt_method_scheduled_transfer_title,
        descriptionRes = R.string.pix_account_receipt_method_scheduled_transfer_description,
        iconRes = R.drawable.ic_date_time_clock_24_dp
    ),

    CIELO_ACCOUNT(
        titleRes = R.string.pix_account_receipt_method_cielo_account_title,
        descriptionRes = R.string.pix_account_receipt_method_cielo_account_description,
        iconRes = R.drawable.ic_devices_smartphone_money_24_dp
    )

}