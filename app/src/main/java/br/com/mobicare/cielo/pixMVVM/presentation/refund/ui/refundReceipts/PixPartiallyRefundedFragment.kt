package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.refundReceipts

import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_SECOND
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.extensions.safeNavigate

class PixPartiallyRefundedFragment : PixRefundReceiptsBaseFragment() {

    override val footerConfigurator get() = FooterConfigurator(
        availableAmountToRefundText = availableAmountToRefund?.toPtBrRealString(),
        noteText = getString(
            R.string.pix_refund_receipts_note_partial_refund,
            deadlineDate?.parseToString(SIMPLE_HOUR_MINUTE_SECOND),
            deadlineDate?.parseToString(SIMPLE_DT_FORMAT_MASK)
        ),
        primaryButton = FooterButtonConfigurator(
            text = getString(R.string.pix_refund_receipts_refund_button),
            onTap = ::onRefundTap
        ),
        secondaryButton = FooterButtonConfigurator(
            text = getString(R.string.text_close),
            onTap = ::onCloseTap
        )
    )

    private fun onRefundTap() {
        findNavController().safeNavigate(
            PixPartiallyRefundedFragmentDirections
                .actionPixPartiallyRefundedFragmentToPixRefundAmountFragment()
        )
    }

}