package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneBrandsDetailItemBinding
import br.com.mobicare.cielo.tapOnPhone.model.TapOnPhoneMapperOffer
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneTransactionType

class TapOnPhoneInstallmentFeesDetailAdapter(
    private val transactionRate: List<TapOnPhoneMapperOffer>,
    private val context: Context
) : RecyclerView.Adapter<TapOnPhoneInstallmentFeesDetailViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TapOnPhoneInstallmentFeesDetailViewHolder {
        val binding = LayoutTapOnPhoneBrandsDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TapOnPhoneInstallmentFeesDetailViewHolder(binding, transactionRate, context)
    }

    override fun getItemCount() = transactionRate.size

    override fun onBindViewHolder(
        holder: TapOnPhoneInstallmentFeesDetailViewHolder,
        position: Int
    ) {
        val item = transactionRate[position]
        when (item.type) {
            TapOnPhoneTransactionType.DEBIT.name -> holder.setupTransactionRateDebit(item)
            TapOnPhoneTransactionType.CREDIT_IN_CASH.name -> holder.setupTransactionRate(item)
            else -> holder.setupInstallmentRate(item)
        }
    }

}