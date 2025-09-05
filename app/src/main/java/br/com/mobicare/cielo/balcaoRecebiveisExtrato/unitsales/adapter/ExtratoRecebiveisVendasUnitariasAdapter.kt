package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasItems
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.extensions.toRotationDown
import br.com.mobicare.cielo.extensions.toRotationUp
import kotlinx.android.synthetic.main.extract_receivable_unit_sales_item.view.*
import kotlinx.android.synthetic.main.extract_receivable_unit_sales_item.view.textViewValue
import kotlinx.android.synthetic.main.extract_receivable_unit_sales_item_content.view.*

class ExtratoRecebiveisVendasUnitariasAdapter(private val list: MutableList<ExtratoRecebiveisVendasUnitariasItems>) : RecyclerView.Adapter<ExtratoRecebiveisVendasUnitariasHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExtratoRecebiveisVendasUnitariasHolder {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.extract_receivable_unit_sales_item, parent, false)
                .let { ExtratoRecebiveisVendasUnitariasHolder(it) }
    }

    override fun onBindViewHolder(holder: ExtratoRecebiveisVendasUnitariasHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}

class ExtratoRecebiveisVendasUnitariasHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: Any) {
        val unitItem = item as ExtratoRecebiveisVendasUnitariasItems

        BrandCardHelper.getUrlBrandImageByCode(unitItem.cardBrandCode?.toString() ?: "-1")
                ?.let { itUrl ->
                    ImageUtils.loadImage(itemView.imageViewBrand, itUrl)
                }

        itemView.textViewCnpjCpf.text = if (unitItem.identificationNumber.length > 11) {
            unitItem.identificationNumber.addMaskCPForCNPJ(itemView.context.getString(R.string.mask_cnpj_step4))
        } else {
            unitItem.identificationNumber.addMaskCPForCNPJ(itemView.context.getString(R.string.mask_cpf_step4))
        }


        itemView.textViewType.text = unitItem.cardBrand
        itemView.textViewValue.text = unitItem.netAmount.toPtBrRealString()
        itemView.textViewTax.text = "${itemView.context.getString(R.string.title_name_taxa)} ${unitItem.effectiveFee.toPtBrRealStringWithoutSymbol()}%"

        if (unitItem.isExpaned) {
            itemView.imageViewArrowDown.toRotationUp()
            setContent(itemView, unitItem)
        } else {
            itemView.linearLayoutContent.removeAllViews()
        }

        itemView.imageViewArrowDown.setOnClickListener {
            if (unitItem.isExpaned) {
                it.toRotationDown()
                unitItem.isExpaned = false
                itemView.linearLayoutContent.removeAllViews()
            } else {
                it.toRotationUp()
                unitItem.isExpaned = true
                setContent(itemView, unitItem)
            }
        }
    }

    fun setContent(itemView: View, unitItem: ExtratoRecebiveisVendasUnitariasItems) {
        unitItem.contentList.forEach {
            val contentView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.extract_receivable_unit_sales_item_content, null)

            contentView.textViewLabel.text = it.lable
            contentView.textViewValue.text = it.value
            itemView.linearLayoutContent.addView(contentView)
        }
    }
}