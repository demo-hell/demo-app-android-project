package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull

open class PixStatusRefundFailedViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixRefundDetailFull
) : PixStatusFailedViewBuilder(layoutInflater) {

    private val refund = data.refundDetail
    private val transfer = data.transferDetail
    private val refundCreditParty = refund?.creditParty

    override val content = Content(
        status = getString(R.string.pix_extract_detail_type_transfer_sent),
        date = refund?.transactionDate?.let {
            context.getString(
                R.string.pix_extract_detail_not_done_in,
                it.parseToString(DATE_FORMAT_PIX_TRANSACTION)
            )
        },
        label = getString(R.string.pix_extract_detail_label_value),
        amount = getFormattedAmount(refund?.amount),
        sentTo = context.getString(R.string.pix_extract_detail_sent_to, refundCreditParty?.name),
        document =  context.getString(
            R.string.pix_extract_detail_document_and_bankName,
            refundCreditParty?.nationalRegistration,
            refundCreditParty?.bankName
        )
    )

    override val information = Information(
        channel = transfer?.originChannel,
        merchant = transfer?.merchantNumber
    )

}