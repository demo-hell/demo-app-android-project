package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptAutomaticTransferSentViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail
) : PixReceiptTransferSentViewBuilder(layoutInflater, data) {

    override val destinationFields get() = data.run {
        listOf(
            Field(R.string.pix_extract_detail_label_to, credit?.creditTransactionCode),
            Field(R.string.pix_extract_detail_label_document, creditParty?.nationalRegistration),
            Field(R.string.pix_extract_detail_label_institution, creditParty?.bankName)
        )
    }

}