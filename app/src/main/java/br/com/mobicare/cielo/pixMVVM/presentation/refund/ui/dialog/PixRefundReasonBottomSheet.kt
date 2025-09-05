package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.dialog

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixRefundReviewMessageBinding

class PixRefundReasonBottomSheet(
    private val context: Context,
    private val message: String? = null,
    private val onSaveMessage: (String) -> Unit
) {

    fun show(fm: FragmentManager, tag: String? = null) {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(R.string.pix_refund_review_bs_refund_reason_title)
            ),
            contentLayoutRes = R.layout.layout_pix_refund_review_message,
            onContentViewCreated = ::buildContentView
        ).show(fm, tag)
    }

    private fun buildContentView(view: View, bs: CieloBottomSheet){
        LayoutPixRefundReviewMessageBinding.bind(view).apply {
            if (message.isNullOrBlank().not()) {
                itmMessage.inputEditText.setText(message)
            }
            btSaveMessage.setOnClickListener {
                bs.dismissAllowingStateLoss()
                onSaveMessage(itmMessage.getText().trim())
            }
        }
    }

}