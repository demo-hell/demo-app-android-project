package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptQrCodeTransferSentViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixTransferDetail
) : PixReceiptTransferSentViewBuilder(layoutInflater, data) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_payment_qrcode),
    )

}