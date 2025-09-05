package br.com.mobicare.cielo.arv.presentation.anticipation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.CHECKED
import br.com.mobicare.cielo.arv.analytics.ArvAnalytics.Companion.UNCHECKED
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.ADD
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.REMOVE
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_SINGLE_CONFIRMATION_FLAG_SELECTION
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ItemFilterSelectionBinding
import br.com.mobicare.cielo.extensions.gone

class ArvFilterSelectionAdapter(
    private val items: List<ArvSelectableItem>,
    private val callback: (List<ArvSelectableItem>) -> Unit = {},
    private val negotiationTypeArv: String?,
    private val analytics: ArvAnalytics,
    private val arvAnalytics: ArvAnalyticsGA4,
    private val whatTypeFlowReviewTag: String
) : RecyclerView.Adapter<ArvFilterSelectionAdapter.SelectionViewHolder>() {

    private var expandedPositions = mutableSetOf<Int>()

    inner class SelectionViewHolder(var binding: ItemFilterSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ArvSelectableItem) {
            binding.apply {
                tvItemName.text = item.name
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
                item.cardBrands?.let { cardBrands ->
                    if (cardBrands.isNotEmpty()) {
                        rvCardBrand.apply {
                            adapter = ArvCardBrandAdapter(cardBrands)
                            rvCardBrand.visible(bindingAdapterPosition in expandedPositions)
                        }
                        ivAcquirerBrandsArrowIndicator.visible()
                        ivAcquirerBrandsArrowIndicator.rotation = if(bindingAdapterPosition in expandedPositions) ARROW_ROTATION_UP else ARROW_ROTATION_DOWN
                        ivAcquirerBrandsArrowIndicator.setOnClickListener {
                            if(bindingAdapterPosition in expandedPositions) {
                                collapseBrands()
                            } else {
                                expandBrands()
                            }
                        }
                    }
                }
                checkedItemCheckBox.isChecked = item.isSelected
                checkedItemCheckBox.setOnClickListener {
                    item.isSelected = item.isSelected.not()
                    analytics.logScreenActionsWithCheckButton(
                        whatTypeFlowReviewTag,
                        ArvAnalytics.SINGLE_ARV,
                        negotiationTypeArv,
                        if(item.isSelected) CHECKED else UNCHECKED,
                        Label.CHECK_BOX,
                        item.name
                    )
                    arvAnalytics.logClick(
                        screenName = SCREEN_VIEW_ARV_SINGLE_CONFIRMATION_FLAG_SELECTION,
                        cardName = item.name,
                        contentName = if (item.isSelected) ADD else REMOVE
                    )
                    callback.invoke(items)
                }
            }
        }

        private fun expandBrands() {
            expandedPositions.add(bindingAdapterPosition)
            binding.apply {
                rvCardBrand.visible()
                ivAcquirerBrandsArrowIndicator.animate()?.rotation(ARROW_ROTATION_UP)?.start()
            }
        }

        private fun collapseBrands() {
            expandedPositions.remove(bindingAdapterPosition)
            binding.apply {
                rvCardBrand.gone()
                ivAcquirerBrandsArrowIndicator.animate()?.rotation(ARROW_ROTATION_DOWN)?.start()
            }
        }

        fun check(checked: Boolean) {
            binding.apply {
                checkedItemCheckBox.isChecked = checked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFilterSelectionBinding.inflate(layoutInflater, parent, false)
        return SelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: SelectionViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads.last() is Boolean) {
            val checked = payloads.last() as Boolean
            holder.check(checked)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun selectAll(selectAll: Boolean) {
        items.forEachIndexed { index, selectableItem ->
            if (selectableItem.isSelected != selectAll) {
                selectableItem.isSelected = selectAll
                notifyItemChanged(index, selectAll)
            }
        }
        callback.invoke(items)
    }
    companion object {
        private const val ARROW_ROTATION_UP = -90F
        private const val ARROW_ROTATION_DOWN = 90F
    }
}


