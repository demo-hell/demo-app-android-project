package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixReceiptFeeTransferSentViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail,
    private val onAmountTap: (PixTransferDetail.Credit?) -> Unit,
    private val onNetAmountTap: (PixTransferDetail.Settlement?) -> Unit
) : PixReceiptTransferSentViewBuilder(layoutInflater, data) {

    override val headerInformation get() = super.headerInformation.copy(
        pixType = getString(R.string.pix_extract_detail_type_fee),
    )

    override val fields get() = listOf(
        Field(R.string.pix_extract_detail_label_value, creditAmountText, onFieldTap = ::fieldAmountAction),
        Field(R.string.pix_extract_detail_label_fee, tariffAmountText),
        Field(R.string.pix_extract_detail_label_net_amount, creditFinalAmountText, onFieldTap = ::fieldNetAmountAction),
        Field(R.string.pix_extract_detail_label_used_channel, data.originChannel),
        Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
        Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd)
    )

    private val tariffAmountText get() =
        data.tariffAmount?.let { (-it).toPtBrWithNegativeRealString() }

    private val creditAmountText get() = data.credit?.creditAmount?.toPtBrRealString()

    private val creditFinalAmountText get() = data.credit?.creditFinalAmount?.toPtBrRealString()

    private fun fieldAmountAction() = onAmountTap(data.credit)

    private fun fieldNetAmountAction() = onNetAmountTap(data.settlement)

}