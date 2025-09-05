package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.scheduled

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.orSimpleLine
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail

class PixReceiptRecurrentTransferScheduledViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixSchedulingDetail,
) : PixReceiptTransferScheduledViewBuilder(layoutInflater, data) {
    override val headerInformation get() =
        HeaderInformation(
            pixType = getString(R.string.pix_extract_detail_type_recurrent_transfer_scheduled),
            date = null,
        )

    override val recurrenceFields get() =
        data.run {
            listOf(
                Field(
                    R.string.pix_extract_detail_label_frequency,
                    frequencyText,
                ),
                Field(
                    R.string.pix_extract_detail_label_total_recurrence,
                    getNumberOfRecurrencesText(totalScheduled),
                ),
                Field(
                    R.string.pix_extract_detail_label_total_done,
                    getNumberOfRecurrencesText(totalScheduledProcessed),
                ),
                Field(
                    R.string.pix_extract_detail_label_total_not_done,
                    totalScheduledErrors.toString().orSimpleLine(),
                ),
                Field(
                    R.string.pix_extract_detail_label_recurrence_start,
                    schedulingCreationDate?.parseToString(SIMPLE_DT_FORMAT_MASK),
                ),
                Field(
                    R.string.pix_extract_detail_label_recurrence_end,
                    scheduledEndDate?.parseToString(SIMPLE_DT_FORMAT_MASK),
                ),
                Field(
                    documentAndMerchantLabelRes,
                    documentAndMerchantText,
                ),
            )
        }

    private val frequencyText get() =
        data.frequencyTime?.let {
            context.resources.getStringArray(
                R.array.pix_extract_detail_schedule_frequency_time,
            )[it.ordinal]
        }.orSimpleLine()

    private val documentAndMerchantLabelRes get() =
        data.run {
            when {
                ValidationUtils.isCNPJ(documentNumber) ->
                    R.string.pix_extract_detail_label_creation_cnpj_and_ec
                ValidationUtils.isCPF(documentNumber) ->
                    R.string.pix_extract_detail_label_creation_cpf_and_ec
                else ->
                    R.string.pix_extract_detail_label_creation_document_and_ec
            }
        }

    private val documentAndMerchantText get() =
        data.run {
            when {
                documentNumber.isNullOrBlank().not() && merchantNumber.isNullOrBlank().not() ->
                    "$documentNumber • $merchantNumber"
                documentNumber.isNullOrBlank().not() ->
                    documentNumber
                merchantNumber.isNullOrBlank().not() ->
                    merchantNumber
                else ->
                    SIMPLE_LINE
            }
        }

    private fun getNumberOfRecurrencesText(quantity: Int?) =
        quantity?.let { total ->
            val paddedTotal = total.toString().padStart(TWO, ZERO_CHAR)
            context.resources.getQuantityString(
                R.plurals.pix_extract_detail_total_recurrence,
                total,
                paddedTotal,
            )
        }.orSimpleLine()

    companion object {
        const val ZERO_CHAR = '0'
    }
}
