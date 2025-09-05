package br.com.mobicare.cielo.commons.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.presentation.filter.model.PaymentType
import br.com.mobicare.cielo.databinding.LayoutItemPaymentTypeBinding
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable

@SuppressLint("NotifyDataSetChanged")
class PaymentTypeAdapter : RecyclerView.Adapter<PaymentTypeAdapter.FilterPaymentTypeViewHolder>() {

    private var paymentTypesList: MutableList<ItemSelectable<PaymentType?>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterPaymentTypeViewHolder {
        val binding = LayoutItemPaymentTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterPaymentTypeViewHolder(binding)
    }

    override fun getItemCount(): Int = paymentTypesList.size

    override fun onBindViewHolder(holder: FilterPaymentTypeViewHolder, position: Int) {
        holder.bind(paymentTypesList[position])
    }

    fun updateAdapter(paymentTypesList: List<ItemSelectable<PaymentType?>>) {
        this.paymentTypesList.clear()
        this.paymentTypesList.addAll(paymentTypesList)
        notifyDataSetChanged()
    }

    inner class FilterPaymentTypeViewHolder(

        private val binding: LayoutItemPaymentTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val ANALYTICS_LABEL = "forma-de-pagamento"

        fun bind(item: ItemSelectable<PaymentType?>) {
            binding.paymentTypeNameText.text = item?.data?.name
            item?.let { changeBorder(it, binding) }
            binding.root.setOnClickListener {
                item?.isSelected = item?.isSelected?.not()
                changeBorder(item, binding)
                item.data?.name?.let { it1 -> gaSendButtonCancelCheckbox(it1) }
            }
        }

        private fun changeBorder(
            item: ItemSelectable<PaymentType?>,
            binding: LayoutItemPaymentTypeBinding
        ) {
            if (item.isSelected) {
                binding.root.setBackgroundResource(R.drawable.filled_rounded_shape_017ceb)
                binding.paymentTypeNameText.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.white
                    )
                )
            } else {
                binding.root.setBackgroundResource(R.drawable.rounded_border_gray)
                binding.paymentTypeNameText.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.color_353A40
                    )
                )
            }
        }

        private fun gaSendButtonCancelCheckbox(name: String) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
                action = listOf(Action.MODAL, Action.MAIS_FILTROS),
                label = listOf(Label.BOTAO, ANALYTICS_LABEL, name)
            )
        }
    }
}