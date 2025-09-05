package br.com.mobicare.cielo.mdr.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.DOT
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.utils.roundToTwoDecimal
import br.com.mobicare.cielo.databinding.BrandsTaxMdrItemBinding
import br.com.mobicare.cielo.mdr.domain.model.CardFees

class MdrFeeAndPlansViewHolder(private val binding: BrandsTaxMdrItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(cardsInformation: CardFees?) {
        binding.apply {
            itemView.context?.let { itContext ->
                cardsInformation?.let {
                    ivBrand.setImageResource(it.icon)
                    tvCardName.text = it.cardType
                    tvDebitValue.text =
                        setFeeText(
                            it.debitFee,
                            itContext,
                        )
                    tvCreditValue.text =
                        setFeeText(
                            it.creditFee,
                            itContext,
                        )
                    tvPaidInSixInstallmentsValue.text =
                        setFeeText(
                            it.fewInstallmentsFee,
                            itContext,
                        )
                    tvPaidInUpToTwelveInstallmentsValue.text =
                        setFeeText(
                            it.installmentsFee,
                            itContext,
                        )
                }
            }
        }
    }

    private fun setFeeText(
        fee: Double?,
        context: Context,
    ): String {
        return fee?.let { itFee ->
            context.getString(
                R.string.mdr_x_per_cent_string,
                convertPercentageToString(itFee),
            )
        } ?: SIMPLE_LINE
    }

    private fun convertPercentageToString(percentageValue: Double): String {
        return percentageValue.roundToTwoDecimal().replace(DOT, COMMA)
    }
}
