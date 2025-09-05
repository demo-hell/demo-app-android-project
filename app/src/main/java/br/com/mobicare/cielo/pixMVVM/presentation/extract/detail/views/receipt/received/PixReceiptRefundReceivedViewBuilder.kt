package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.PixReceiptViewBuilder

class PixReceiptRefundReceivedViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixRefundDetailFull
) : PixReceiptViewBuilder(layoutInflater) {

    private val refund = data.refundDetail
    private val transfer = data.transferDetail

    override val headerInformation get() = HeaderInformation(
        pixType = getString(R.string.pix_extract_detail_type_refund_received),
        date = refund?.transactionDate?.let {
            context.getString(
                R.string.pix_extract_detail_realized_in,
                it.parseToString(DATE_FORMAT_PIX_TRANSACTION)
            )
        }
    )

    override val fields get() = listOf(
        Field(R.string.pix_extract_detail_label_value, refund?.amount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_reason, refund?.payerAnswer),
        Field(R.string.pix_extract_detail_label_authentication_code_refund, refund?.idEndToEndReturn)
    )

    override val originalTransactionFields get() = listOf(
        Field(R.string.pix_extract_detail_label_transaction_type, getPixTypeTextOrNull(transfer?.type)),
        Field(R.string.pix_extract_detail_label_original_value, transfer?.amount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_date_and_time, transfer?.transactionDate?.parseToString(DATE_FORMAT_PIX_TRANSACTION)),
        Field(R.string.pix_extract_detail_label_original_value_payee, transfer?.creditParty?.name),
        Field(R.string.pix_extract_detail_label_document, transfer?.creditParty?.nationalRegistration),
        Field(R.string.pix_extract_detail_label_institution, transfer?.creditParty?.bankName),
        Field(R.string.pix_extract_detail_label_authentication_code_original_transaction, refund?.idEndToEndOriginal)
    )

}