package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailStatusScheduleCanceledBinding
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail

class PixStatusScheduleCanceledViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixSchedulingDetail
) : PixStatusBaseViewBuilder() {

    private val binding = LayoutPixExtractDetailStatusScheduleCanceledBinding.inflate(layoutInflater)

    override val context: Context = binding.root.context

    private val statusText = getString(R.string.pix_extract_detail_type_transfer_sent)
    private val dateText = data.schedulingCancellationDate?.let {
        context.getString(
            R.string.pix_extract_detail_canceled_in,
            it.parseToString(DATE_FORMAT_PIX_TRANSACTION)
        )
    }
    private val labelText = getString(R.string.pix_extract_detail_label_value)
    private val amountText = getFormattedAmount(data.finalAmount)
    private val sentToText = context.getString(R.string.pix_extract_detail_sent_to, data.payeeName)
    private val documentText = context.getString(
        R.string.pix_extract_detail_document_and_bankName,
        data.payeeDocumentNumber,
        data.payeeBankName
    )

    fun build(): View {
        configureViews()
        configureContent()
        selectTextStrikeThrough()

        return binding.root
    }

    private fun configureViews() {
        binding.includeContent.apply {
            containerRoot.apply {
                applyCustomStyle(R.color.cloud_100)
            }
            ivStatus.apply {
                applyCustomStyle(R.color.color_2F363E_alpha_12, R.dimen.dimen_8dp)
                setImageResource(R.drawable.ic_symbols_slash_neutral_main_20_dp)
            }
        }
    }

    private fun configureContent() {
        binding.includeContent.apply {
            tvStatus.text = statusText
            tvDate.text = dateText
            tvLabel.text = labelText
            tvAmount.text = amountText
            tvSentTo.text = sentToText
            tvDocument.text = documentText
            ivStatus.setImageResource(R.drawable.ic_symbols_slash_neutral_main_20_dp)
        }
    }

    private fun selectTextStrikeThrough() {
        binding.includeContent.apply {
            applyTextStrikeThrough(
                tvStatus,
                tvLabel,
                tvAmount,
                tvDocument
            )
        }
    }

}