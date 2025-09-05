package br.com.mobicare.cielo.simulator.simulation.presentation.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.moneyUtils.toPtBrRealString
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.databinding.LayoutSimulatorInstallmentItemBinding
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.updateMargins
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.simulator.simulation.domain.model.Installment
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation


class SimulationInstallmentAdapter(
    private val simulation: Simulation,
    private val showReceiveTotalValue: Pair<Boolean, String>,
) : RecyclerView.Adapter<SimulationInstallmentAdapter.InstallmentViewHolder>() {

    private val expandedPositions = mutableSetOf<Int>()

    inner class InstallmentViewHolder(var binding: LayoutSimulatorInstallmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Installment) {
            binding.apply {
                tvInstallmentValue.text = with(itemView.context) {
                    when (simulation.installments?.size) {
                        ONE -> getString(
                            R.string.sales_simulator_installment_format,
                            ONE.toString(),
                            item.totalValue?.toPtBrRealString()
                        )

                        else -> getString(
                            R.string.sales_simulator_installment_format,
                            item.installmentNumber.toString(),
                            item.customerInstallmentValue?.toPtBrRealString()
                        )
                    }

                }
                tvFeeValue.text = item.finalMdrTax?.formatRate() ?: ZERO_DOUBLE.formatRate()
                tvMDRValue.text = when (simulation.flexibleTerm) {
                    true -> itemView.context.getString(
                        R.string.sales_simulator_MDR_RA_format,
                        item.mdrTax?.formatRate() ?: ZERO_DOUBLE.formatRate(),
                        item.fastRepayTax?.formatRate() ?: ZERO_DOUBLE.formatRate()
                    )

                    else -> itemView.context.getString(R.string.sales_simulator_MDR)
                }
                tvTotalReceivingValue.text = item.receivableValue?.toPtBrRealString()
                tvReceivingTermValue.text = with(itemView.context) {
                    getString(
                        R.string.sales_simulator_receiving_term_format,
                        resources?.getQuantityString(
                            R.plurals.recepitDeadline,
                            simulation.receivableRemainingDays?.toInt() ?: ZERO,
                            simulation.receivableRemainingDays
                        )
                    ).fromHtml()
                }

                tvToReceiveSimulatedValueLabel.text = showReceiveTotalValue.second
                tvToReceiveSimulatedValue.text = item.saleAmount?.toPtBrRealString()
                totalReceivingValueGroup.visible(isExpanded() && shouldShowTotalReceiveValue())

                expandableGroup.visible(isExpanded())
                ivInstallmentArrowIndicator.rotation =
                    if (isExpanded()) ARROW_ROTATION_UP else ARROW_ROTATION_DOWN
                ivInstallmentArrowIndicator.setOnClickListener {
                    if (isExpanded()) collapse() else expand()
                }
            }
        }

        private fun expand() {
            expandedPositions.add(bindingAdapterPosition)
            binding.apply {
                tvTotalReceivingValueLabel.updateMargins(top = itemView.resources.getDimensionPixelOffset(R.dimen.dimen_16dp))
                expandableGroup.visible()
                if(shouldShowTotalReceiveValue()) {
                    totalReceivingValueGroup.visible()
                }
                ivInstallmentArrowIndicator.animate()?.rotation(ARROW_ROTATION_UP)?.start()
            }
        }

        private fun collapse() {
            expandedPositions.remove(bindingAdapterPosition)
            binding.apply {
                tvTotalReceivingValueLabel.updateMargins(top = itemView.resources.getDimensionPixelOffset(R.dimen.dimen_32dp))
                expandableGroup.gone()
                totalReceivingValueGroup.gone()
                ivInstallmentArrowIndicator.animate()?.rotation(ARROW_ROTATION_DOWN)?.start()
            }
        }

        private fun isExpanded() = bindingAdapterPosition in expandedPositions
        private fun shouldShowTotalReceiveValue() = showReceiveTotalValue.first
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutSimulatorInstallmentItemBinding.inflate(layoutInflater, parent, false)
        return InstallmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstallmentViewHolder, position: Int) {
        simulation.installments?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = simulation.installments?.size ?: ZERO

    private companion object {
        const val ARROW_ROTATION_UP = -90F
        const val ARROW_ROTATION_DOWN = 90F
    }
}