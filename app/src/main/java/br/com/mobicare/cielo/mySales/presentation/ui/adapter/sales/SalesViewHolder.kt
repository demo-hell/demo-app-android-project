package br.com.mobicare.cielo.mySales.presentation.ui.adapter.sales

import android.text.SpannableStringBuilder
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.SIXTEEN
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef.CANCELADA
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesStatusHelper
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pix.constants.EMPTY

class SalesViewHolder(
    private val binding: LayoutItemMinhasVendasBinding,
    private val clickListener: (Sale) -> Unit,
    private val quickFilter: QuickFilter
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(sale: Sale){
            var saleTime: String = EMPTY
            var saleStatusCode: Int? = null
            var cardBrandUrl: String? = null
            var saleValue: String = EMPTY

            saleTime = if(isCanceledSale()) sale.date?.convertTimeStampToDate() else {
                saleStatusCode = sale.statusCode ?: ExtratoStatusDef.APROVADA
                cardBrandUrl = sale.cardBrand
                saleValue = sale.amount?.toPtBrRealString().toString()
                sale.date?.substring(ELEVEN, SIXTEEN)
            }.toString()

            binding.tvTime.text = SpannableStringBuilder.valueOf(saleTime)
            binding.tvValue.text = SpannableStringBuilder.valueOf(saleValue)
            binding.tvPaymentType.text = sale.paymentType
            binding.tvStatus.text = sale.status?.toLowerCasePTBR()?.capitalizePTBR()
            configureSalesStatusCode(binding,saleStatusCode)
            configureCardBrandImage(binding, sale.cardBrand,cardBrandUrl)

            binding.root.setOnClickListener {
                clickListener(sale)
            }
        }

        private fun isCanceledSale(): Boolean = quickFilter.status?.contains(CANCELADA) ?: false

        private fun configureSalesStatusCode(
            binding: LayoutItemMinhasVendasBinding,
            salesStatusCode: Int?) {

            val context = binding.root.context
            MySalesStatusHelper.setSalesStatus(
                context,
                salesStatusCode,
                null,
                binding.tvStatus
            )
        }

        private fun configureCardBrandImage(
            binding: LayoutItemMinhasVendasBinding,
            cardBrand: String?,
            cardBrandUrl: String?) {

           if(cardBrand != null){
               BrandCardHelper.getUrlBrandImageByCode(cardBrand)?.let { imgUrl ->
                   ImageUtils.loadImage(
                       binding.ivBrandType,
                       imgUrl,
                       R.drawable.ic_generic_brand
                   )
                   binding.ivBrandType.contentDescription = cardBrandUrl
               }
           }else {
               binding.ivBrandType.setImageResource(R.drawable.ic_generic_brand)
           }
        }

}