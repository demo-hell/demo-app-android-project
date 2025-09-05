package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.dialog

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.joinWithLastCustomSeparator
import br.com.mobicare.cielo.databinding.LayoutPixScheduledTransferBinding

class PixScheduledSettlementBottomSheet(
    private val context: Context,
    private val hours: List<String>
) {

    fun show(fm: FragmentManager, tag: String? = null) {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = context.getString(R.string.pix_extract_scheduled_settlement_title),
                showCloseButton = true
            ),
            contentLayoutRes = R.layout.layout_pix_scheduled_transfer,
            onContentViewCreated = ::buildContentView
        ).show(fm, tag)
    }

    private fun buildContentView(view: View, bs: CieloBottomSheet){
        LayoutPixScheduledTransferBinding.bind(view).apply {
            tvActiveModel.setCustomDrawable {
                solidColor = R.color.green_100
                radius = R.dimen.dimen_8dp
            }
            tvReceivingHours.text = context.getString(
                R.string.pix_extract_scheduled_settlement_description,
                hours.joinWithLastCustomSeparator()
            )
        }
    }

}