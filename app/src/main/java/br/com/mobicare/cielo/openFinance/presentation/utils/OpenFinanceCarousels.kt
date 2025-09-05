package br.com.mobicare.cielo.openFinance.presentation.utils

import android.content.Context
import br.com.cielo.libflue.carousel.model.ItemCarouselModel
import br.com.mobicare.cielo.R

object OpenFinanceCarousels {
    fun getListReceiving(context: Context): List<ItemCarouselModel> {
        return listOf(
            ItemCarouselModel(
                R.drawable.ic_clipboard_check_cloud_600,
                context.getString(R.string.register_information),
                context.getString(R.string.register_information_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_security_cloud_600,
                context.getString(R.string.complement_data),
                context.getString(R.string.complement_data_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_slip_cloud_600,
                context.getString(R.string.balance_limits),
                context.getString(R.string.balance_limits_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_bar_cloud_600,
                context.getString(R.string.investment),
                context.getString(R.string.investment_desc)
            ),
        )
    }

    fun getListTransmitting(context: Context): List<ItemCarouselModel> {
        return listOf(
            ItemCarouselModel(
                R.drawable.ic_clipboard_check_cloud_600,
                context.getString(R.string.register_information),
                context.getString(R.string.register_information_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_security_cloud_600,
                context.getString(R.string.complement_data),
                context.getString(R.string.complement_data_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_slip_cloud_600,
                context.getString(R.string.data_account),
                context.getString(R.string.data_account_desc)
            ),
            ItemCarouselModel(
                R.drawable.ic_pig_bank_coin_cloud_600,
                context.getString(R.string.balance_statements),
                context.getString(R.string.balance_statements_desc)
            )
        )
    }
}