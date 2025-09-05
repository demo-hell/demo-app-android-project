package br.com.mobicare.cielo.arv.presentation.historic.list.viewHolder

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.presentation.model.enum.ReceivableStatusEnum
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutCardHistoricBinding

class ArvHistoricCardViewHolder(
        private val binding: LayoutCardHistoricBinding,
        private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(negotiation: Item) {
        binding.apply {
            val amount = negotiation.grossAmount
            val descriptionAmount = AccessibilityUtils.convertAmount(amount ?: ZERO_DOUBLE, context)

            tvTitleCard.text = context.getString(R.string.txt_arv_historic_card_title, negotiation.negotiationType, negotiation.modality)
            tvTitleCard.contentDescription = context.getString(R.string.content_description_arv_historic_card_title, negotiation.negotiationType, negotiation.modality)
            tvAmount.text = HtmlCompat.fromHtml(
                    context.getString(R.string.txt_arv_historic_card_amount, amount?.toPtBrRealString()),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvAmount.contentDescription = context.getString(R.string.content_description_arv_historic_card_amount, descriptionAmount)
            tvDate.text = context.getString(R.string.txt_arv_historic_card_date, negotiation.negotiationDate?.dateFormatToBr())
            tvStatus.text = negotiation.status
            tvStatus.contentDescription = context.getString(R.string.content_description_arv_historic_card_status, negotiation.status)

            ReceivableStatusEnum.values().forEach { status ->
                if (negotiation.status == status.status) {
                    tvStatus.setTextColor(ContextCompat.getColor(context, status.getColor()))
                    ivIconStatus.setImageResource(status.getIcon())
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: () -> Unit) {
        binding.root.setOnClickListener {
            onClickListener.invoke()
        }
    }

}