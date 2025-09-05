package br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.databinding.LayoutItemPaymentTypeSalesFilterBinding
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable

class FilterPaymentTypeViewHolder(
    private val binding: LayoutItemPaymentTypeSalesFilterBinding
): RecyclerView.ViewHolder(binding.root) {

    private val ANALYTICS_LABEL = "forma-de-pagamento"

    fun bind(item: ItemSelectable<PaymentType>) {
        binding.paymentTypeNameText.text = item.data.name
        changeBorder(item,binding)
        setupContentDescription(item,binding)
        binding.root.setOnClickListener {
            item.isSelected = item.isSelected.not()
            changeBorder(item,binding)
            setupContentDescription(item,binding)
            gaSendButtonCancelCheckbox(item.data.name)
        }
    }


    private fun changeBorder(item: ItemSelectable<PaymentType>,
                             binding: LayoutItemPaymentTypeSalesFilterBinding) {
        if(item.isSelected){
            binding.root.setBackgroundResource(R.drawable.filled_rounded_shape_017ceb)
            binding.paymentTypeNameText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    android.R.color.white
                )
            )
        }else {
            binding.root.setBackgroundResource(R.drawable.rounded_border_gray)
            binding.paymentTypeNameText.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.color_353A40
                )
            )
        }
    }

    private fun setupContentDescription(item: ItemSelectable<PaymentType>, binding: LayoutItemPaymentTypeSalesFilterBinding){
        binding.root.contentDescription = binding.root.context.getString(
            if(item.isSelected) R.string.description_focused_selected_payment_method else R.string.description_focused_unselected_payment_method,
            item.data.name
        )
    }


    private fun gaSendButtonCancelCheckbox(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.BOTAO, ANALYTICS_LABEL, name)
        )
    }


}