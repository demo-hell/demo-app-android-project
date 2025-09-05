package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptQrCodeWithdrawalTransferReceivedViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail
) : PixReceiptTransferReceivedViewBuilder(layoutInflater, data) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_received_qrcode_withdrawal)
    )

    override val fields get() = listOf(
        Field(R.string.pix_extract_detail_label_value, data.amount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_message, data.payerAnswer),
        Field(R.string.pix_extract_detail_label_sale_channel, data.originChannel),
        Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
        Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd)
    )

}