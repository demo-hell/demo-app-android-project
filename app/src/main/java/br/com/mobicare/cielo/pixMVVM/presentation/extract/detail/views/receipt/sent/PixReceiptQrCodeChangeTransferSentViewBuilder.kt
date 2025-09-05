package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptQrCodeChangeTransferSentViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail
) : PixReceiptTransferSentViewBuilder(layoutInflater, data) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_payment_qrcode_change)
    )

    override val fields get() = listOf(
        Field(R.string.pix_extract_detail_label_value, data.amount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_purchase_value, data.purchaseAmount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_change_value, data.changeAmount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_fee, getTariffOrFreeText(data.tariffAmount)),
        Field(R.string.pix_extract_detail_label_sent_message, data.payerAnswer),
        Field(R.string.pix_extract_detail_label_used_channel, data.originChannel),
        Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
        Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd)
    )

}