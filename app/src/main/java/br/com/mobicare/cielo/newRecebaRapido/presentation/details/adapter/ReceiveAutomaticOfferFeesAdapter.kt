package br.com.mobicare.cielo.newRecebaRapido.presentation.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.cielo.libflue.util.extensions.gone
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.commons.constants.Text.DOUBLE_LINE
import br.com.mobicare.cielo.databinding.LayoutItemFeesAutomaticReceiveBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.OfferSummary
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.CREDIT
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.INSTALLMENT

class ReceiveAutomaticOfferFeesAdapter(
    private val items: List<OfferSummary>, private val typeTransactionSelected: String
) : RecyclerView.Adapter<ReceiveAutomaticOfferFeesAdapter.ReceiveAutomaticOfferFeesViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ReceiveAutomaticOfferFeesViewHolder {
        val binding = LayoutItemFeesAutomaticReceiveBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ReceiveAutomaticOfferFeesViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ReceiveAutomaticOfferFeesViewHolder, position: Int) {
        val firstPositionOfRow = (position / COLUMNS_SIZE) * COLUMNS_SIZE
        val installmentRowsCount = maxOf(
            items.getOrNull(firstPositionOfRow)?.installments?.size ?: ZERO,
            items.getOrNull(firstPositionOfRow + ONE)?.installments?.size ?: ZERO
        )
        holder.bind(items[position], installmentRowsCount)
    }

    inner class ReceiveAutomaticOfferFeesViewHolder(
        private val binding: LayoutItemFeesAutomaticReceiveBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OfferSummary, installmentRowsCount: Int) {
            binding.apply {
                ivBrand.setImageResource(
                    CieloCardBrandIcons.getBrandFromCode(item.brandCode).icon
                )
                tvBrandName.text = item.brandName.toLowerCasePTBR().capitalizePTBR()

                when (typeTransactionSelected) {
                    CREDIT -> {
                        setupCredit(item)
                        tvInstallmentsTotalValue.gone()
                        tvInstallmentLabel.gone()
                        dividerInstallments.gone()
                        rvInstallments.gone()
                    }

                    INSTALLMENT -> {
                        setupInstallments(item, installmentRowsCount)
                        tvCashLabel.gone()
                        tvCashFeeValue.gone()
                    }

                    BOTH -> {
                        setupCredit(item)
                        setupInstallments(item, installmentRowsCount)
                    }
                }
            }
        }

        private fun setupInstallments(item: OfferSummary, installmentRowsCount: Int) {
            binding.apply {
                tvInstallmentLabel.visible()
                tvInstallmentsTotalValue.apply {
                    tvInstallmentsTotalValue.text = item.installments?.let {
                        "${item.installments.maxOf { it.number }}x"
                    } ?: DOUBLE_LINE
                    visible()
                }
                dividerInstallments.visible()
                rvInstallments.apply {
                    var installments = item.installments.orEmpty()
                    if (installments.size < installmentRowsCount) {
                        val extraInstallments =
                            (installments.size + ONE..installmentRowsCount).map {
                                InstallmentSummary(
                                    number = it + ONE,
                                    null
                                )
                            }
                        installments = installments + extraInstallments
                    }
                    rvInstallments.adapter = InstallmentsFeesAdapter(installments)
                    visible()
                }
            }
        }

        private fun setupCredit(item: OfferSummary) {
            binding.apply {
                tvCashLabel.visible()
                tvCashFeeValue.apply {
                    text = item.cashFee?.let {
                        it.formatRate()
                    } ?: DOUBLE_LINE
                    visible()
                }
            }
        }
    }

    private companion object {
        const val COLUMNS_SIZE = 2
    }
}