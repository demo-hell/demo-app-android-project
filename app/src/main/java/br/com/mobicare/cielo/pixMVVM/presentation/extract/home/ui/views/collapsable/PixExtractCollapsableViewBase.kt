package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable

import android.view.View
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.PixExtractAccountBalanceViewSection

abstract class PixExtractCollapsableViewBase(
    private val fragment: PixHomeExtractFragment,
    private val viewModel: PixHomeExtractViewModel
) {

    abstract val view: View
    abstract val accountBalanceViewSection: PixExtractAccountBalanceViewSection
    abstract fun onDestroyView()

    init {
        initializeAccountBalanceObserver()
    }

    fun loadAccountBalance() {
        viewModel.loadAccountBalance()
    }

    fun refreshView() {
        accountBalanceViewSection.reloadSetup()
    }

    private fun initializeAccountBalanceObserver() {
        viewModel.accountBalanceUiState.observe(fragment.viewLifecycleOwner) { state ->
            accountBalanceViewSection.handleObservableResult(state)
        }
    }

}