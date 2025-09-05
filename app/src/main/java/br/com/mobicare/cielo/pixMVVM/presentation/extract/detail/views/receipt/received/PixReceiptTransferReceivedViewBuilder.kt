package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.PixReceiptViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.helper.TransferReceivedAmountHelper

open class PixReceiptTransferReceivedViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail,
    private val onFeeTap: ((PixTransferDetail.Fee?) -> Unit)? = null,
    private val onNetAmountTap: ((PixTransferDetail.Settlement?) -> Unit)? = null,
) : PixReceiptViewBuilder(layoutInflater) {
    private val helper = TransferReceivedAmountHelper(data)

    override val headerInformation
        get() =
            HeaderInformation(
                pixType = getString(R.string.pix_extract_detail_type_transfer_received),
                date =
                    data.transactionDate?.let {
                        context.getString(
                            R.string.pix_extract_detail_realized_in,
                            it.parseToString(DATE_FORMAT_PIX_TRANSACTION),
                        )
                    },
                aboutSettlement = data.settlement?.let { getAboutSettlement(it) },
            )

    override val fields
        get() =
            listOf(
                Field(R.string.pix_extract_detail_label_value, data.amount?.toPtBrRealString()),
                Field(R.string.pix_extract_detail_label_fee, feeText, onFieldTap = fieldFeeAction),
                Field(
                    R.string.pix_extract_detail_label_net_amount,
                    netAmountText,
                    onFieldTap = fieldNetAmountAction,
                ),
                Field(R.string.pix_extract_detail_label_received_message, data.payerAnswer),
                Field(R.string.pix_extract_detail_label_sale_channel, data.originChannel),
                Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
                Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd),
            )

    override val destinationFields
        get() =
            data.creditParty?.run {
                listOf(
                    Field(R.string.pix_extract_detail_label_to, name),
                    Field(R.string.pix_extract_detail_label_document, nationalRegistration),
                    Field(R.string.pix_extract_detail_label_institution, bankName),
                    Field(R.string.pix_extract_detail_label_agency, bankBranchNumber),
                    Field(R.string.pix_extract_detail_label_account, bankAccountNumber),
                )
            }

    override val originFields
        get() =
            data.debitParty?.run {
                listOf(
                    Field(R.string.pix_extract_detail_label_from, name),
                    Field(R.string.pix_extract_detail_label_document, nationalRegistration),
                    Field(R.string.pix_extract_detail_label_institution, bankName),
                    Field(R.string.pix_extract_detail_label_agency, bankBranchNumber),
                    Field(R.string.pix_extract_detail_label_account, bankAccountNumber),
                )
            }

    protected val feeText
        get() =
            helper.run {
                if (isFeePendingOrProcessing) {
                    getString(R.string.pix_extract_detail_to_be_discounted)
                } else if (hasFee) {
                    formattedTariffAmount
                } else {
                    null
                }
            }

    protected val netAmountText
        get() =
            helper.run {
                if (isSettlementProcessing) {
                    getString(R.string.pix_extract_detail_in_transfer)
                } else if (isSettlementCompletelyExecuted || isSettlementPartiallyExecuted) {
                    formattedSettlementFinalAmount
                } else {
                    null
                }
            }

    protected val fieldFeeAction
        get(): (() -> Unit)? =
            if (helper.isFeeClickable) ({ onFeeTap?.invoke(data.fee) }) else null

    protected val fieldNetAmountAction
        get(): (() -> Unit)? =
            if (helper.isNetAmountClickable) ({ onNetAmountTap?.invoke(data.settlement) }) else null
}
