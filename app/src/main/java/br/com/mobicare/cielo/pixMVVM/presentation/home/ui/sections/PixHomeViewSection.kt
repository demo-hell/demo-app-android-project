package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import androidx.annotation.StringRes
import br.com.mobicare.cielo.pix.constants.PIX_REDIRECT_RANDOM_KEY
import br.com.mobicare.cielo.pix.ui.keys.myKeys.PixMyKeysNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import org.jetbrains.anko.startActivity

open class PixHomeViewSection(
    protected val fragment: PixHomeFragment,
    protected val viewModel: PixHomeViewModel
) {

    protected val context get() = fragment.requireContext()
    protected val activity get() = fragment.requireActivity()

    protected val accountBalanceStore get() = viewModel.accountBalanceStore
    protected val keysStore get() = viewModel.keysStore

    protected fun navigateToAddRandomKey() =
        activity.startActivity<PixMyKeysNavigationFlowActivity>(
            PIX_REDIRECT_RANDOM_KEY to true
        )

    protected fun navigateToMyKeys() {
        activity.startActivity<PixMyKeysNavigationFlowActivity>()
    }

    protected fun getString(@StringRes resId: Int) = context.getString(resId)

}