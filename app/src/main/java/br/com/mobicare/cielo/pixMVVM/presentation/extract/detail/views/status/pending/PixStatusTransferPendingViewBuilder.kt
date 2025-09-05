package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

open class PixStatusTransferPendingViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixTransferDetail
) : PixStatusPendingViewBuilder(layoutInflater) {

    override val content = Content(
        status = getString(R.string.pix_extract_detail_type_processing),
        date = data.transactionDate?.let {
            context.getString(
                R.string.pix_extract_detail_realized_in,
                it.parseToString(DATE_FORMAT_PIX_TRANSACTION)
            )
        },
        label = getString(R.string.pix_extract_detail_label_value),
        amount = getFormattedAmount(data.amount),
        sentTo = context.getString(R.string.pix_extract_detail_sent_to, data.creditParty?.name),
        document = context.getString(
            R.string.pix_extract_detail_document_and_bankName,
            data.creditParty?.nationalRegistration,
            data.creditParty?.bankName
        ),
        payerAnswer = data.payerAnswer
    )

    override val information = Information(
        channel = data.originChannel,
        merchant = data.merchantNumber
    )

}