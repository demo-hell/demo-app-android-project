package br.com.mobicare.cielo.tapOnPhone.presentation.sale.type.installment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneChooseInstallmentsItemBinding
import br.com.mobicare.cielo.extensions.toStringOrEmpty
import br.com.mobicare.cielo.extensions.visible

class TapOnPhoneChooseInstallmentsAdapter(
    private val installments: List<Int>,
    private var currentSelectedInstallment: String?,
    private val context: Context,
    private val onItemClicked: ((installment: String?) -> Unit)
) : RecyclerView.Adapter<TapOnPhoneChooseInstallmentsAdapter.TapOnPhoneInstallmentsViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TapOnPhoneInstallmentsViewHolder {
        val binding = LayoutTapOnPhoneChooseInstallmentsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TapOnPhoneInstallmentsViewHolder(binding, context, onItemClicked)
    }

    override fun getItemCount() = installments.size

    override fun onBindViewHolder(holder: TapOnPhoneInstallmentsViewHolder, position: Int) {
        holder.binding.rbValue.isChecked = position == selectedPosition
        holder.bind(installments[position])
    }

    inner class TapOnPhoneInstallmentsViewHolder(
        val binding: LayoutTapOnPhoneChooseInstallmentsItemBinding,
        val context: Context,
        private val onItemClicked: ((installment: String?) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(installment: Int) {
            binding.apply {
                divider.visible((installment == installments.last()).not())
                rbValue.text = context.getString(
                    R.string.tap_on_phone_sale_total_installments_value,
                    installment.toStringOrEmpty()
                )

                if (currentSelectedInstallment == installment.toStringOrEmpty()) {
                    rbValue.isChecked = true
                    selectedPosition = adapterPosition
                    onItemClicked.invoke(currentSelectedInstallment)
                    return
                }

                rbValue.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        notifyItemChanged(selectedPosition)
                        selectedPosition = adapterPosition

                        onItemClicked.invoke(installment.toStringOrEmpty())
                        currentSelectedInstallment = null
                    }

                    setOptionColorText(context, isChecked)
                }
            }
        }

        private fun setOptionColorText(context: Context, isOptionChecked: Boolean) {
            val color = if (isOptionChecked) R.color.brand_400 else (R.color.cloud_400)
            binding.rbValue.setTextColor(ContextCompat.getColor(context, color))
        }
    }
}