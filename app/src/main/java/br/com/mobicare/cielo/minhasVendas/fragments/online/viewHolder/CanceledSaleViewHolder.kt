package br.com.mobicare.cielo.minhasVendas.fragments.online.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.minhasVendas.enums.CanceledSalesStatusEnum.*
import br.com.mobicare.cielo.minhasVendas.fragments.online.MinhasVendasOnlineContract

class CanceledSaleViewHolder(
    private val binding: LayoutItemMinhasVendasBinding,
    private val listener: MinhasVendasOnlineContract.WithCanceledSellsView
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(canceledSale: CanceledSale) {
        binding.apply {
            BrandCardHelper.getUrlBrandImageByCode(canceledSale.cardBrandCode ?: ONE_NEGATIVE)
                ?.let { itUrl ->
                    ImageUtils.loadImage(
                        ivBrandType,
                        itUrl,
                        R.drawable.ic_generic_brand
                    )
                    ivBrandType.contentDescription = canceledSale.cardBrand
                }
                ?: ivBrandType.setImageResource(R.drawable.ic_generic_brand)
            tvTime.text = canceledSale.date?.dateFormatToBr()
            tvPaymentType.text = canceledSale.paymentType
            tvValue.text = canceledSale.refundAmount?.toPtBrRealString()

            val status = canceledSale.status?.lowercase().capitalizePTBR()
            tvStatus.text = status
            tvStatus.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    defineStatusTextColor(status)
                )
            )

            card.setOnClickListener {
                listener.showCanceledSaleDetail(canceledSale)
            }
        }
    }

    private fun defineStatusTextColor(status: String): Int {
        return when (status) {
            REVIEW.value -> REVIEW.color
            EFFECTIVE.value -> EFFECTIVE.color
            REVERSED.value -> REVERSED.color
            REJECTED.value -> REJECTED.color
            else -> R.color.color_cloud_600
        }
    }
}