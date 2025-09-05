package br.com.mobicare.cielo.newRecebaRapido.presentation.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.DOUBLE_LINE
import br.com.mobicare.cielo.databinding.LayoutItemInstallmentAutomaticReceiveBinding
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentSummary

class InstallmentsFeesAdapter(
    private val items: List<InstallmentSummary>
) :
    RecyclerView.Adapter<InstallmentsFeesAdapter.InstallmentsSummaryViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InstallmentsSummaryViewHolder {
        val binding = LayoutItemInstallmentAutomaticReceiveBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return InstallmentsSummaryViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: InstallmentsSummaryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class InstallmentsSummaryViewHolder(
        private val binding: LayoutItemInstallmentAutomaticReceiveBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InstallmentSummary) {
            binding.apply {
                tvInstallmentNumber.text = root.context.getString(
                    R.string.receive_auto_installment_in,
                    item.number
                )
                tvInstallmentFee.text = item.fee?.formatRate() ?: DOUBLE_LINE
            }
        }
    }
}