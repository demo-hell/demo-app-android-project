package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import android.view.View
import android.widget.Toast
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.THOUSAND
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionOptionsBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.PIX_MY_LIMITS_IS_HOME_ARGS
import br.com.mobicare.cielo.pix.ui.mylimits.PixMyLimitsNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.account.PixAccountNavigationFlowActivity.NavArgs.CURRENT_BALANCE_ARGS
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import org.jetbrains.anko.startActivity

class PixMenuViewSection(
    fragment: PixHomeFragment,
    viewModel: PixHomeViewModel,
    binding: IncludePixHomeSectionOptionsBinding,
) : PixHomeViewSection(fragment, viewModel) {

    init {
        binding.apply {
            root.postDelayed({
                shimmerLoading.gone()
                content.visible()
                btnPixLimits.setOnClickListener(::onMyLimitsTap)
                btnAccountManagement.setOnClickListener(::onAccountManagementTap)
            }, THOUSAND)
        }
    }

    private fun onMyLimitsTap(v: View) {
        activity.startActivity<PixMyLimitsNavigationFlowActivity>(
            PIX_MY_LIMITS_IS_HOME_ARGS to true
        )
    }

    private fun onAccountManagementTap(v: View) {
        accountBalanceStore.balance?.let { balance ->
            activity.startActivity<PixAccountNavigationFlowActivity>(
                CURRENT_BALANCE_ARGS to balance
            )
        }.ifNull {
            Toast.makeText(context, R.string.pix_home_menu_reload_balance, Toast.LENGTH_LONG).show()
        }

    }

}