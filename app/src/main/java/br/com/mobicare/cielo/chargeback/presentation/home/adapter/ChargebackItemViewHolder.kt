package br.com.mobicare.cielo.chargeback.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelector
import br.com.mobicare.cielo.chargeback.presentation.home.helper.ChargebackStatusStyleSelectorScreenType
import br.com.mobicare.cielo.chargeback.utils.createChargebackStatusLabel
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ItemChargebackBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible

class ChargebackItemViewHolder(
    private val binding: ItemChargebackBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var onItemClicked: ((Chargeback) -> Unit)? = null

    fun setOnItemClicked(callback: (Chargeback) -> Unit) {
        onItemClicked = callback
    }

    fun bind(chargeback: Chargeback) {
        binding.apply {
            tvEstablishmentCode.text = chargeback.merchantId.toString()
            tvCaseCode.text = chargeback.caseId.toString()
            tvValueChargeback.apply {
                text = chargeback.transactionAmount?.toPtBrRealString()
                contentDescription = context.getString(R.string.chargeback_accessibility_transaction_amount,chargeback.transactionAmount)
            }
            icCardLogo.apply {
                setImageResource(CieloCardBrandIcons.getCardBrandIconResourceId(chargeback.transactionDetails?.cardBrandCode))
                contentDescription = context.getString(R.string.chargeback_accessibility_card_brand,chargeback.transactionDetails?.cardBrandName)
            }

            if (chargeback.isDone) {
                tvStatusTreated.text = chargeback.lifecycle?.action
                llStatusPendingContainer.gone()
                llStatusTreatedContainer.visible()
            } else {
                tvStatusPending.text = createChargebackStatusLabel(chargeback.process,binding.root.context)
                llStatusTreatedContainer.gone()
                llStatusPendingContainer.visible()
            }

            tlStatusLabel.apply {
                ChargebackStatusStyleSelector(
                    root.resources, ChargebackStatusStyleSelectorScreenType.HOME, chargeback
                ).apply {
                    setTagIcon(tagIcon)
                    setBackgroundShape(backgroundShape)
                    setTextStyle(textStyle)
                    setText(text)
                }
            }
            root.setOnClickListener { onItemClicked?.invoke(chargeback) }
        }
    }
}