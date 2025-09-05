package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.viewHolder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Condition
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationRatesDetailsCardBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.posVirtual.domain.model.RateUI
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.adapter.PosVirtualAccreditationRatesDetailsBrandRateAdapter
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualTransactionTypeEnum

class PosVirtualAccreditationRatesDetailsBrandViewHolder(
    private val binding: LayoutPosVirtualAccreditationRatesDetailsCardBinding,
) : RecyclerView.ViewHolder(binding.root) {

    private val adapter = PosVirtualAccreditationRatesDetailsBrandRateAdapter()
    private var isExpanded = false
    private val context = binding.root.context

    fun bind(brand: Brand) {
        adapter.setRates(mapRateUI(brand.conditions))
        binding.apply {
            llCard.setOnClickListener { onClickCard(brand.name.orEmpty()) }
            tvLabelBrand.text = brand.name?.lowercase().capitalizePTBR()
            rvRates.adapter = adapter
            rvRates.layoutManager = ScrollControlledLinearManager(context)
            ivIconBrand.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    CieloCardBrandIcons.getBrandFromCode(
                        brand.code?.toIntOrNull() ?: ONE_NEGATIVE
                    ).icon
                )
            )
        }
        showRates(brand.name ?: EMPTY)
    }

    private fun mapRateUI(conditions: List<Condition>?): List<RateUI> {
        val ratesUI = ArrayList<RateUI>()

        conditions?.let {
            it.forEach { condition ->
                when (condition.type) {
                    PosVirtualTransactionTypeEnum.DEBIT.name -> {
                        val rateUI = RateUI(
                            label = context.getString(R.string.pos_virtual_rates_details_card_text_debit),
                            rate = condition.flexibleTermPaymentMDR?.formatRate() ?: SIMPLE_LINE
                        )
                        ratesUI.add(ZERO, rateUI)
                    }
                    PosVirtualTransactionTypeEnum.CREDIT_IN_CASH.name -> {
                        val rateUI = RateUI(
                            label = context.getString(R.string.pos_virtual_rates_details_card_text_in_cash),
                            rate = condition.flexibleTermPaymentMDR?.formatRate() ?: SIMPLE_LINE
                        )
                        if (ratesUI.size > ONE) ratesUI.add(ONE, rateUI)
                        else ratesUI.add(rateUI)
                    }
                    PosVirtualTransactionTypeEnum.CREDIT_IN_INSTALLMENTS.name -> {
                        condition.installments?.forEach { installment ->
                            val rateUI = RateUI(
                                label = context.getString(
                                    R.string.pos_virtual_rates_details_card_text_in_installment,
                                    installment.installment
                                ),
                                rate = installment.flexibleTermPaymentMDR?.formatRate()
                                    ?: SIMPLE_LINE
                            )
                            ratesUI.add(rateUI)
                        }
                    }
                }
            }
        }

        return ratesUI
    }

    private fun onClickCard(brandName: String) {
        isExpanded = isExpanded.not()
        showRates(brandName)
    }

    private fun showRates(brandName: String) = binding.apply {
        ivBtnExpanded.apply {
            setImageDrawable(
                ContextCompat.getDrawable(
                    context, if (isExpanded) R.drawable.ic_directions_chevron_up_cloud_200_24_dp
                    else R.drawable.ic_directions_chevron_down_cloud_200_24_dp
                )
            )
            contentDescription =
                context.getString(
                    if (isExpanded) R.string.pos_virtual_accreditation_rates_details_content_description_close_rates
                    else R.string.pos_virtual_accreditation_rates_details_content_description_open_rates,
                    brandName
                )
        }
        rvRates.visible(isExpanded)
    }

}