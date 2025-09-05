package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.NINETY_DOUBLE
import br.com.mobicare.cielo.commons.constants.NINETY_DOUBLE_NEGATIVE
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.databinding.LayoutTapOnPhoneBrandsItemBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.tapOnPhone.model.TapOnPhoneMapperOffer
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.fees.adapter.detail.TapOnPhoneInstallmentFeesDetailAdapter
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneTransactionType
import com.squareup.picasso.Picasso

class TapOnPhoneInstallmentFeesViewHolder(
    private val binding: LayoutTapOnPhoneBrandsItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    private var isExpanded = false

    fun bind(fee: Brand) {
        binding.apply {
            tvBrandName.text = fee.name.toLowerCasePTBR().capitalizePTBR()
            Picasso.get()
                .load(fee.imgSource)
                .placeholder(R.drawable.ic_generic_brand)
                .into(ivBrand)
            setupRecyclerView(fee)

            root.setOnClickListener {
                modifyFeeContainer(fee)
            }
        }
    }

    private fun setupRecyclerView(fee: Brand) {
        var debit: TapOnPhoneMapperOffer? = null
        var credit: TapOnPhoneMapperOffer? = null
        val rates = ArrayList<TapOnPhoneMapperOffer>()
        val installments = ArrayList<TapOnPhoneMapperOffer>()

        fee.conditions?.forEach {
            when (it.type) {
                TapOnPhoneTransactionType.DEBIT.name -> {
                    debit = TapOnPhoneMapperOffer(
                        type = it.type,
                        description = it.label,
                        fee = it.flexibleTermPaymentMDR
                    )
                }
                TapOnPhoneTransactionType.CREDIT_IN_CASH.name -> {
                    credit = TapOnPhoneMapperOffer(
                        type = it.type,
                        description = it.label,
                        fee = it.flexibleTermPaymentMDR
                    )
                }
                else -> {
                    it.installments?.forEach { installment ->
                        installments.add(
                            TapOnPhoneMapperOffer(
                                type = it.type,
                                description = installment.installment?.toString(),
                                fee = installment.flexibleTermPaymentMDR
                            )
                        )
                    }
                }
            }
        }
        debit?.let {
            rates.add(it)
        }
        credit?.let {
            rates.add(it)
        }
        rates.addAll(installments)
        binding.rvDetail.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvDetail.adapter = TapOnPhoneInstallmentFeesDetailAdapter(
            transactionRate = rates,
            context = context
        )
    }

    private fun modifyFeeContainer(fee: Brand) {
        if (isExpanded) {
            isExpanded = false

            setupContainerStyle(
                contentDescriptionText = context.getString(
                    R.string.content_description_fee_container_to_open,
                    fee.name
                ),
                arrowIndicatorRotation = NINETY_DOUBLE,
                recyclerViewVisibility = View.GONE
            )
        } else {
            isExpanded = true

            setupContainerStyle(
                contentDescriptionText = context.getString(
                    R.string.content_description_fee_container_to_close,
                    fee.name
                ),
                arrowIndicatorRotation = NINETY_DOUBLE_NEGATIVE,
                recyclerViewVisibility = View.VISIBLE
            )
        }
    }

    private fun setupContainerStyle(
        contentDescriptionText: String,
        arrowIndicatorRotation: Float,
        recyclerViewVisibility: Int
    ) {
        binding.apply {
            ivSeeMore.contentDescription = contentDescriptionText
            ivSeeMore.rotation = arrowIndicatorRotation
            rvDetail.visibility = recyclerViewVisibility
        }
    }

}