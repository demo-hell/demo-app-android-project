package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter.detail

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.DOT
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneBrandsDetailItemBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.tapOnPhone.model.TapOnPhoneMapperOffer

class TapOnPhoneInstallmentFeesDetailViewHolder(
    private val binding: LayoutTapOnPhoneBrandsDetailItemBinding,
    private val items: List<TapOnPhoneMapperOffer>,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {

    fun setupTransactionRateDebit(fee: TapOnPhoneMapperOffer) {
        setupCommonText(
            paymentTypeText = context.getString(R.string.tax_label_debit),
            rate = context.getString(
                R.string.x_percent_text,
                fee.fee?.toPtBrRealStringWithoutSymbol()
            )
        )
    }

    fun setupTransactionRate(fee: TapOnPhoneMapperOffer) {
        setupCommonText(
            paymentTypeText = context.getString(R.string.tax_label_in_cash),
            rate = context.getString(
                R.string.x_percent_text,
                fee.fee?.toPtBrRealStringWithoutSymbol()
            )
        )
    }

    fun setupInstallmentRate(item: TapOnPhoneMapperOffer) {
        setupCommonText(
            paymentTypeText = context.getString(
                R.string.fee_label_in_x_installments,
                item.description
            ),
            rate = context.getString(
                R.string.x_percent_text,
                item.fee?.toPtBrRealStringWithoutSymbol()
            ),
            item = item
        )
    }

    private fun setupCommonText(
        paymentTypeText: String,
        rate: String,
        item: TapOnPhoneMapperOffer? = null
    ) {
        binding.apply {
            tvPaymentType.text = paymentTypeText
            tvFeeQuantity.text = rate.replace(DOT, COMMA)

            if (items.isEmpty() || item == items.last()) divider.gone()
        }
    }
}