package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.PixReceiptViewBuilder

open class PixReceiptTransferSentViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail,
) : PixReceiptViewBuilder(layoutInflater) {
    override val headerInformation get() =
        HeaderInformation(
            pixType = getString(R.string.pix_extract_detail_type_transfer_sent),
            date =
                data.transactionDate?.let {
                    context.getString(
                        R.string.pix_extract_detail_realized_in,
                        it.parseToString(DATE_FORMAT_PIX_TRANSACTION),
                    )
                },
            aboutSettlement = data.settlement?.let { getAboutSettlement(it) },
        )

    override val fields get() =
        listOf(
            Field(R.string.pix_extract_detail_label_value, data.amount?.toPtBrRealString()),
            Field(R.string.pix_extract_detail_label_sent_message, data.payerAnswer),
            Field(R.string.pix_extract_detail_label_used_channel, data.originChannel),
            Field(R.string.pix_extract_detail_label_merchant, data.merchantNumber),
            Field(R.string.pix_extract_detail_label_authentication_code, data.idEndToEnd),
        )

    override val destinationFields get() =
        data.creditParty?.run {
            listOf(
                Field(R.string.pix_extract_detail_label_to, name),
                Field(R.string.pix_extract_detail_label_document, nationalRegistration),
                Field(R.string.pix_extract_detail_label_institution, bankName),
                Field(R.string.pix_extract_detail_label_agency, bankBranchNumber),
                Field(R.string.pix_extract_detail_label_account, bankAccountNumber),
            )
        }

    override val originFields get() =
        data.debitParty?.run {
            listOf(
                Field(R.string.pix_extract_detail_label_from, name),
                Field(R.string.pix_extract_detail_label_document, nationalRegistration),
                Field(R.string.pix_extract_detail_label_institution, bankName),
            )
        }

    protected fun getTariffOrFreeText(tariffAmount: Double?) =
        tariffAmount?.let {
            if (it > ZERO_DOUBLE) it.toPtBrRealString() else getString(R.string.pix_extract_detail_no_fee)
        }
}
