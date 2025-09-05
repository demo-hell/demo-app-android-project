package br.com.mobicare.cielo.arv.presentation.home.whatsAppNews

import androidx.fragment.app.Fragment

class ArvWhatsAppNewsHandler(
    private val fragment: Fragment,
    private val viewModel: ArvWhatsAppNewsViewModel,
    private val onConfirmTap: () -> Unit,
) {
    private var isEnablementAlreadyChecked = false

    init {
        viewModel.enableNews.apply {
            if (hasObservers()) return@apply
            observe(fragment.viewLifecycleOwner) { enableNews ->
                if (enableNews) showBottomSheet()
            }
        }
    }

    fun checkEnablement() {
        if (isEnablementAlreadyChecked.not()) {
            viewModel.checkEnablement()
            isEnablementAlreadyChecked = true
        }
    }

    private fun showBottomSheet() {
        ArvWhatsAppNewsBottomSheet(
            fragment = fragment,
            onConfirmTap = {
                viewModel.saveViewed()
                onConfirmTap()
            },
            onDismiss = {
                viewModel.updateDismissCounter()
            },
        ).show()
    }
}
