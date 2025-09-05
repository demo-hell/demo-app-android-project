package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptQrCodeChangeTransferReceivedViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail,
    onFeeTap: (PixTransferDetail.Fee?) -> Unit,
    onNetAmountTap: (PixTransferDetail.Settlement?) -> Unit
) : PixReceiptTransferReceivedViewBuilder(layoutInflater, data, onFeeTap, onNetAmountTap) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_received_qrcode_change)
    )

    override val fields get() = listOf(
        Field(R.string.pix_extract_detail_label_total_received, data.amount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_sale_value, data.purchaseAmount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_change_value, data.changeAmount?.toPtBrRealString()),
        Field(R.string.pix_extract_detail_label_fee, feeText, onFieldTap = fieldFeeAction),
        Field(R.string.pix_extract_detail_label_net_amount, netAmountText, onFieldTap = fieldNetAmountAction),
        Field(R.string.pix_extract_detail_label_received_message, data.payerAnswer),
        Field(R.string.pix_extract_detail_label_sale_channel, data.originChannel),
        Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
        Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd)
    )

    override val originFields get() = data.debitParty?.run {
        listOf(
            Field(R.string.pix_extract_detail_label_from, name),
            Field(R.string.pix_extract_detail_label_document, nationalRegistration)
        )
    }

}