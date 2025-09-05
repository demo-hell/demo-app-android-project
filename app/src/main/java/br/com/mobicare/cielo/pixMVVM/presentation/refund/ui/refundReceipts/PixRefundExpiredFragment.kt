package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.refundReceipts

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_SECOND
import br.com.mobicare.cielo.commons.utils.parseToString

class PixRefundExpiredFragment : PixRefundReceiptsBaseFragment() {

    override val footerConfigurator get() = FooterConfigurator(
        noteText = getString(
            R.string.pix_refund_receipts_note_partial_refund_expired,
            deadlineDate?.parseToString(SIMPLE_HOUR_MINUTE_SECOND),
            deadlineDate?.parseToString(SIMPLE_DT_FORMAT_MASK)
        ),
        primaryButton = FooterButtonConfigurator(
            text = getString(R.string.text_close),
            onTap = ::onCloseTap
        )
    )

}