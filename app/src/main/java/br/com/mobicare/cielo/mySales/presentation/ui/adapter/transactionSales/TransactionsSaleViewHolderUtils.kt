package br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales

import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SIXTEEN
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesStatusHelper

object TransactionsSaleViewHolderUtils {

    fun bindSaleTransactionItem(
        sale: Sale,
        binding: LayoutItemMinhasVendasBinding,
        clickListener: (Sale) -> Unit) {

        binding.apply {
            this.tvTime.text = sale.date?.substring(ELEVEN,SIXTEEN)
            this.tvValue.text = sale.grossAmount?.toPtBrRealString()
            this.tvPaymentType.text = sale.paymentType
            this.tvStatus.text = sale.status?.replaceFirstChar{ it.uppercase() } ?: binding.root.context.getString(R.string.text_aproved)

            setupCardBrandImage(sale.cardBrandCode,sale.cardBrand,this)

            MySalesStatusHelper.setSalesStatus(
                binding.root.context,
                ONE,
                null,
                this.tvStatus
            )
            this.llMv.setOnClickListener {
                clickListener.invoke(sale)
            }
        }
    }

    private fun setupCardBrandImage(
        cardBrandCode: Int?,
        cardBrand: String?,
        binding: LayoutItemMinhasVendasBinding){

        cardBrandCode?.let {
            BrandCardHelper.Companion.getUrlBrandImageByCode(it)?.let { url->
                ImageUtils.loadImage(
                    binding.ivBrandType,
                    url,
                    R.drawable.ic_generic_brand
                )
                binding.ivBrandType.contentDescription = binding.root.context.getString(
                    R.string.description_focused_flag_card,
                    cardBrand
                )
            } ?: run {
                binding.ivBrandType.setImageResource(R.drawable.ic_generic_brand)
                binding.ivBrandType.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        }
    }

}