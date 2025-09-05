package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptQrCodeTransferReceivedViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixTransferDetail,
    onFeeTap: (PixTransferDetail.Fee?) -> Unit,
    onNetAmountTap: (PixTransferDetail.Settlement?) -> Unit
) : PixReceiptTransferReceivedViewBuilder(layoutInflater, data, onFeeTap, onNetAmountTap) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_received_qrcode)
    )

}