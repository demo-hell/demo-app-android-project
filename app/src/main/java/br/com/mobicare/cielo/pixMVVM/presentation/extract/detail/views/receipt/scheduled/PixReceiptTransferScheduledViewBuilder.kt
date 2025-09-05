package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.scheduled

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.PixReceiptViewBuilder

open class PixReceiptTransferScheduledViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixSchedulingDetail,
) : PixReceiptViewBuilder(layoutInflater) {
    override val headerInformation get() =
        HeaderInformation(
            pixType = getString(R.string.pix_extract_detail_type_transfer_scheduled),
            date =
                data.schedulingCreationDate?.let {
                    context.getString(
                        R.string.pix_extract_detail_scheduled_in,
                        it.parseToString(DATE_FORMAT_PIX_TRANSACTION),
                    )
                },
        )

    override val fields get() =
        data.run {
            listOf(
                Field(R.string.pix_extract_detail_label_value, finalAmount?.toPtBrRealString()),
                Field(R.string.pix_extract_detail_label_sent_message, message),
                Field(
                    label = R.string.pix_extract_detail_label_scheduled_to,
                    value = schedulingDate?.parseToString(SIMPLE_DT_FORMAT_MASK),
                    caption = getString(R.string.pix_extract_detail_caption_schedule_info),
                ),
            )
        }

    override val destinationFields get() =
        data.run {
            listOf(
                Field(R.string.pix_extract_detail_label_to, payeeName),
                Field(R.string.pix_extract_detail_label_document, payeeDocumentNumber),
                Field(R.string.pix_extract_detail_label_institution, payeeBankName),
            )
        }
}
