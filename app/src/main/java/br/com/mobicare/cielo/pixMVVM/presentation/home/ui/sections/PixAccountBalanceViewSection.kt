package br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections

import android.view.View
import br.com.mobicare.cielo.databinding.IncludePixHomeSectionBalanceBinding
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixHomeFragment
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.builders.PixAccountBalanceViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.AccountBalanceUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import org.jetbrains.anko.startActivity

class PixAccountBalanceViewSection(
    fragment: PixHomeFragment,
    viewModel: PixHomeViewModel,
    binding: IncludePixHomeSectionBalanceBinding,
) : PixHomeViewSection(fragment, viewModel), ViewModelResultHandler<AccountBalanceUiState> {

    private val builder = PixAccountBalanceViewBuilder(
        view = binding.root,
        viewModel = viewModel,
        onShowExtractClick = ::onShowExtractClick
    )

    override fun handleObservableResult(value: AccountBalanceUiState) {
        builder.handleObservableResult(value)
    }

    private fun onShowExtractClick(v: View) {
        activity.startActivity<PixNewExtractNavigationFlowActivity>(
            PixNewExtractNavigationFlowActivity.NavArgs.PROFILE_TYPE to ProfileType.FREE_MOVEMENT
        )
    }

}