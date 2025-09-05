package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.dialog

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixRefundReviewCancelBinding

class PixRefundCancelBottomSheet(
    private val context: Context,
    private val onCancelRefund: () -> Unit
) {

    fun show(fm: FragmentManager, tag: String? = null) {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(R.string.pix_refund_review_bs_cancel_title)
            ),
            contentLayoutRes = R.layout.layout_pix_refund_review_cancel,
            onContentViewCreated = ::buildContentView
        ).show(fm, tag)
    }

    private fun buildContentView(view: View, bs: CieloBottomSheet){
        LayoutPixRefundReviewCancelBinding.bind(view).apply {
            btCancel.setOnClickListener {
                bs.dismissAllowingStateLoss()
                onCancelRefund()
            }
            btClose.setOnClickListener {
                bs.dismissAllowingStateLoss()
            }
        }
    }

}