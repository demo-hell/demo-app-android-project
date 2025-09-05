package br.com.mobicare.cielo.arv.presentation.home.whatsAppNews

import androidx.fragment.app.Fragment
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R

class ArvWhatsAppNewsBottomSheet(
    private val fragment: Fragment,
    private val onConfirmTap: () -> Unit,
    onDismiss: () -> Unit,
) {
    private val bottomSheet =
        CieloContentBottomSheet.create(
            headerConfigurator =
                CieloBottomSheet.HeaderConfigurator(
                    title = fragment.getString(R.string.anticipation_whatsapp_news_title),
                    showCloseButton = true,
                    onDismiss = onDismiss,
                ),
            contentLayoutRes = R.layout.layout_arv_whatsapp_news,
            mainButtonConfigurator =
                CieloBottomSheet.ButtonConfigurator(
                    title = fragment.getString(R.string.confer),
                    buttonType = CieloBottomSheet.ButtonType.ROUNDED,
                    onTap = {
                        it.dismissAllowingStateLoss()
                        onConfirmTap()
                    },
                ),
        )

    fun show() {
        bottomSheet.show(fragment.childFragmentManager, fragment.javaClass.simpleName)
    }
}
