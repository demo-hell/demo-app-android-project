package br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout

import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import br.com.cielo.libflue.bottomsheet.CieloMessageBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.standalone.KoinComponent

class PixAlertNewLayoutBottomSheetHandler(val viewModel: PixAlertNewLayoutViewModel) : KoinComponent {
    private var activity: FragmentActivity? = null
    private val context get() = activity?.applicationContext

    init {
        setupObserver()
    }

    fun verifyShowBottomSheet(activity: FragmentActivity) {
        this.activity = activity
        viewModel.verifyShowBottomSheet()
    }

    private fun setupObserver() {
        viewModel.isShowBottomSheet.observeForever { isShow ->
            if (isShow) showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        activity?.supportFragmentManager?.let { fragmentManager ->
            CieloMessageBottomSheet.create(
                headerConfigurator =
                    CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.pix_alert_new_layout_title),
                        showCloseButton = true,
                        onDismiss = {
                            viewModel.saveViewed()
                        },
                    ),
                message =
                    CieloMessageBottomSheet.Message(
                        text = getString(R.string.pix_alert_new_layout_message),
                    ),
                mainButtonConfigurator =
                    CieloBottomSheet.ButtonConfigurator(
                        title = getString(R.string.text_close),
                        buttonType = CieloBottomSheet.ButtonType.ROUNDED,
                        onTap = { bottomSheet ->
                            bottomSheet.dismiss()
                        },
                    ),
            ).show(fragmentManager, EMPTY)
        }
    }

    private fun getString(
        @StringRes value: Int,
    ): String {
        return context?.getString(value) ?: EMPTY
    }
}
