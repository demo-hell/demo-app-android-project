package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views

import android.view.View
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.builders.PixAccountBalanceViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.sections.ViewModelResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.AccountBalanceUiState
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixAccountBalanceViewModel

class PixExtractAccountBalanceViewSection(
    viewModel: PixAccountBalanceViewModel,
    balanceView: View,
) : ViewModelResultHandler<AccountBalanceUiState> {

    private val builder = PixAccountBalanceViewBuilder(
        view = balanceView,
        viewModel = viewModel
    )

    override fun handleObservableResult(value: AccountBalanceUiState) {
        builder.handleObservableResult(value)
    }

    override fun reloadSetup() {
        builder.reloadSetup()
    }

}