package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.refundReceipts

import br.com.mobicare.cielo.R

class PixFullyRefundedFragment : PixRefundReceiptsBaseFragment() {

    override val footerConfigurator get() = FooterConfigurator(
        noteText = getString(R.string.pix_refund_receipts_note_fully_refunded),
        primaryButton = FooterButtonConfigurator(
            text = getString(R.string.text_close),
            onTap = ::onCloseTap
        )
    )

}