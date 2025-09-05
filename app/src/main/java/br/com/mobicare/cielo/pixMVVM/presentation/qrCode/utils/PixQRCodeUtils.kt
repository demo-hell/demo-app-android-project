package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import androidx.annotation.StringRes
import br.com.cielo.libflue.util.dateUtils.removeTimeAttributes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.commons.utils.orSimpleLine
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toStringWithTodayCondition
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQRCodeType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model.DetailDataFieldModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model.PaymentDetailDataModel
import java.util.Calendar
import java.util.GregorianCalendar

object PixQRCodeUtils {
    @StringRes
    fun getTitleToolbar(qrCode: PixDecodeQRCode): Int =
        when (qrCode.pixType) {
            PixQrCodeOperationType.CHANGE -> R.string.pix_qr_code_payment_summary_toolbar_title_change
            PixQrCodeOperationType.WITHDRAWAL -> R.string.pix_qr_code_payment_summary_toolbar_title_withdrawal
            else -> R.string.pix_qr_code_payment_summary_toolbar_title_default
        }

    fun getPaymentAmount(qrCode: PixDecodeQRCode): Double =
        when (qrCode.pixType) {
            PixQrCodeOperationType.CHANGE -> qrCode.originalAmount.ifNull { ZERO_DOUBLE }
            PixQrCodeOperationType.WITHDRAWAL -> qrCode.withDrawAmount.ifNull { ZERO_DOUBLE }
            else -> qrCode.finalAmount.ifNull { ZERO_DOUBLE }
        }

    fun isAllowedChangePaymentValue(qrCode: PixDecodeQRCode): Boolean {
        val allows =
            when {
                isPixTypeCharge(qrCode) -> PixAllowsChangeValueEnum.NOT_ALLOWED
                isPixTypeWithdrawal(qrCode) -> qrCode.modalityAltWithDraw
                else -> qrCode.modalityAlteration
            }

        return allows == PixAllowsChangeValueEnum.ALLOWED
    }

    fun isAllowedChangeChangeValue(qrCode: PixDecodeQRCode): Boolean =
        isPixTypeChange(qrCode) && qrCode.modalityAltChange == PixAllowsChangeValueEnum.ALLOWED

    fun getBankName(qrCode: PixDecodeQRCode): String =
        when (qrCode.pixType) {
            PixQrCodeOperationType.CHANGE -> qrCode.ispbChangeName
            PixQrCodeOperationType.WITHDRAWAL -> qrCode.ispbWithDrawName
            else -> qrCode.participantName
        }

    fun getAgentMode(qrCode: PixDecodeQRCode): String? =
        when (qrCode.pixType) {
            PixQrCodeOperationType.CHANGE -> qrCode.modalityChangeAgent
            PixQrCodeOperationType.WITHDRAWAL -> qrCode.modalityWithDrawAgent
            else -> null
        }

    fun getAgentWithdrawalIspb(qrCode: PixDecodeQRCode): String? =
        when (qrCode.pixType) {
            PixQrCodeOperationType.CHANGE -> qrCode.ispbChange?.toString()
            PixQrCodeOperationType.WITHDRAWAL -> qrCode.ispbWithDraw?.toString()
            else -> null
        }

    fun getPaymentBillingDetail(qrCode: PixDecodeQRCode): List<DetailDataFieldModel> =
        listOf(
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_billing_details_label_original_amount,
                value = qrCode.originalAmount?.toPtBrRealString().orSimpleLine(),
            ),
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_billing_details_label_interest,
                value = qrCode.interest?.toPtBrRealString().orSimpleLine(),
            ),
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_billing_details_label_penalty,
                value = qrCode.penalty?.toPtBrRealString().orSimpleLine(),
            ),
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_billing_details_label_abatement,
                value = qrCode.abatement?.toPtBrRealString().orSimpleLine(),
            ),
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_billing_details_label_discount,
                value = qrCode.discount?.toPtBrRealString().orSimpleLine(),
            ),
        )

    fun getPaymentDetailsData(qrCode: PixDecodeQRCode): List<PaymentDetailDataModel> {
        val list = mutableListOf<PaymentDetailDataModel>()

        list.add(getPaymentDestinationData(qrCode))

        if (isPixTypeCharge(qrCode)) {
            list.add(getDebtorData(qrCode))
        }

        if (isPixTypeCharge(qrCode) || isPixTypeChangeOrWithdrawal(qrCode)) {
            list.add(getTransactionData(qrCode))
        }

        return list
    }

    private fun getPaymentDestinationData(qrCode: PixDecodeQRCode) =
        PaymentDetailDataModel(
            titleRes = R.string.pix_qr_code_payment_summary_payment_destination_title,
            fields =
                listOf(
                    DetailDataFieldModel(
                        titleRes = R.string.pix_qr_code_payment_summary_payment_destination_title_to,
                        value = qrCode.receiverName,
                    ),
                    DetailDataFieldModel(
                        titleRes = R.string.pix_transfer_review_label_document,
                        title = qrCode.receiverPersonType?.documentType,
                        value = qrCode.receiverDocument,
                    ),
                    DetailDataFieldModel(
                        titleRes = R.string.pix_qr_code_payment_summary_payment_destination_title_bank,
                        value = getBankName(qrCode),
                    ),
                    DetailDataFieldModel(
                        titleRes = R.string.pix_qr_code_payment_summary_payment_destination_title_identifier,
                        value = qrCode.idTx,
                    ),
                    DetailDataFieldModel(
                        titleRes =
                            if (isPixTypeChangeOrWithdrawal(qrCode)) {
                                R.string.pix_qr_code_payment_summary_payment_destination_title_collector_message
                            } else {
                                R.string.pix_qr_code_payment_summary_payment_destination_title_collector_additional_information
                            },
                        value = qrCode.additionalData,
                    ),
                ),
        )

    private fun getDebtorData(qrCode: PixDecodeQRCode) =
        PaymentDetailDataModel(
            titleRes = R.string.pix_qr_code_payment_summary_payment_debtor_title,
            fields =
                listOf(
                    DetailDataFieldModel(
                        titleRes = R.string.pix_qr_code_payment_summary_payment_debtor_title_name,
                        value = qrCode.payerName,
                    ),
                    DetailDataFieldModel(
                        titleRes = R.string.pix_qr_code_payment_summary_payment_debtor_title_document,
                        titleArgs = qrCode.payerType?.documentType.orSimpleLine(),
                        value = qrCode.payerDocument,
                    ),
                ),
        )

    private fun getTransactionData(qrCode: PixDecodeQRCode): PaymentDetailDataModel {
        val list = mutableListOf<DetailDataFieldModel>()

        list.add(
            DetailDataFieldModel(
                titleRes = R.string.pix_qr_code_payment_summary_payment_transaction_title_type,
                valueRes =
                    when {
                        isPixTypeChange(qrCode) -> R.string.pix_extract_detail_type_payment_qrcode_change
                        isPixTypeWithdrawal(qrCode) -> R.string.pix_extract_detail_type_payment_qrcode_withdrawal
                        else -> R.string.pix_extract_detail_type_payment_qrcode
                    },
            ),
        )

        if (isPixTypeChangeOrWithdrawal(qrCode)) {
            list.add(
                DetailDataFieldModel(
                    titleRes =
                        if (isPixTypeChange(qrCode)) {
                            R.string.pix_qr_code_payment_summary_payment_payment_date
                        } else {
                            R.string.pix_qr_code_payment_summary_payment_transaction_title_withdrawal_date
                        },
                    value = Calendar.getInstance().removeTimeAttributes().toStringWithTodayCondition(SIMPLE_DT_FORMAT_MASK),
                ),
            )
        }

        if (isPixTypeCharge(qrCode)) {
            list.add(
                DetailDataFieldModel(
                    titleRes = R.string.pix_qr_code_payment_summary_payment_transaction_title_due_date,
                    value =
                        qrCode.dueDate,
                ),
            )

            list.add(
                DetailDataFieldModel(
                    titleRes = R.string.pix_qr_code_payment_summary_payment_transaction_title_deadline,
                    value =
                        qrCode.expireDate?.let {
                            GregorianCalendar.from(it).toStringWithTodayCondition(DATE_FORMAT_PIX_TRANSACTION).orSimpleLine()
                        },
                ),
            )
        }

        return PaymentDetailDataModel(
            titleRes = R.string.pix_qr_code_payment_summary_payment_transaction_title,
            fields = list,
        )
    }

    fun isPixTypeChangeOrWithdrawal(qrCode: PixDecodeQRCode): Boolean = isPixTypeChange(qrCode) || isPixTypeWithdrawal(qrCode)

    fun isPixTypeCharge(qrCode: PixDecodeQRCode): Boolean = qrCode.type == PixQRCodeType.DYNAMIC_COBV

    fun isPixTypeChange(qrCode: PixDecodeQRCode): Boolean = qrCode.pixType == PixQrCodeOperationType.CHANGE

    fun isPixTypeWithdrawal(qrCode: PixDecodeQRCode): Boolean = qrCode.pixType == PixQrCodeOperationType.WITHDRAWAL
}
