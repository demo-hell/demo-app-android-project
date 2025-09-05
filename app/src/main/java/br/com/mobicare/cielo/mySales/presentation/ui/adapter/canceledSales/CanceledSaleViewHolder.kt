package br.com.mobicare.cielo.mySales.presentation.ui.adapter.canceledSales

import android.view.View
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
import br.com.mobicare.cielo.minhasVendas.enums.CanceledSalesStatusEnum
import br.com.mobicare.cielo.mySales.data.model.CanceledSale

class CanceledSaleViewHolder(
    private val binding: LayoutItemMinhasVendasBinding,
    private val clickListener: (canceledSale: CanceledSale) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

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
                } ?: run {
                    ivBrandType.setImageResource(R.drawable.ic_generic_brand)
                    binding.ivBrandType.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            tvTime.text = canceledSale.date?.dateFormatToBr()
            tvPaymentType.text = canceledSale.paymentType
            tvValue.text = canceledSale.refundAmount?.toPtBrRealString()

            val status = canceledSale.status?.lowercase().capitalizePTBR()
            tvStatus.text = status
            tvStatus.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    CanceledSalesStatusEnum.getColor(status)
                )
            )
            card.setOnClickListener {
                clickListener(canceledSale)
            }
        }
    }
}



