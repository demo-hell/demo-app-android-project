package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable

import br.com.mobicare.cielo.databinding.LayoutPixExtractBalanceSectionBinding
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.PixExtractAccountBalanceViewSection

class PixFreeMovementCollapsableViewSection(
    fragment: PixHomeExtractFragment,
    viewModel: PixHomeExtractViewModel,
) : PixExtractCollapsableViewBase(fragment, viewModel) {

    private var _binding: LayoutPixExtractBalanceSectionBinding? =
        LayoutPixExtractBalanceSectionBinding.inflate(fragment.layoutInflater)
    private val binding get() = requireNotNull(_binding)

    override val view get() = binding.root

    override val accountBalanceViewSection = PixExtractAccountBalanceViewSection(
        viewModel = viewModel,
        balanceView = view
    )

    override fun onDestroyView() {
        _binding = null
    }

}