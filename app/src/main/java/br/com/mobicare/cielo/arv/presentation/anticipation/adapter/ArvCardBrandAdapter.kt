package br.com.mobicare.cielo.arv.presentation.anticipation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.mobicare.cielo.arv.domain.model.CardBrand
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ItemFilterSelectionCardBrandBinding


class ArvCardBrandAdapter(
    private val items: List<CardBrand>,
) : RecyclerView.Adapter<ArvCardBrandAdapter.QueryByFlagViewHolder>() {

    inner class QueryByFlagViewHolder(var binding: ItemFilterSelectionCardBrandBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArvSelectableItem) {
            binding.apply {
                item.code?.let { ivCardBrand.setImageResource(CieloCardBrandIcons.getBrandFromCode(it).icon) }
                tvCardBrandName.text = item.name
                netValueTextView.text = item.netAmount?.toPtBrRealString()
                netValueTextView.contentDescription = AccessibilityUtils.convertAmount(
                    item.netAmount ?: ZERO_DOUBLE,
                    binding.root.context
                )
                totalValueTextView.text = item.grossAmount?.toPtBrRealString()
                totalValueTextView.contentDescription = AccessibilityUtils.convertAmount(
                    item.grossAmount ?: ZERO_DOUBLE,
                    binding.root.context
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryByFlagViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterSelectionCardBrandBinding.inflate(layoutInflater, parent, false)
        return QueryByFlagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueryByFlagViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}