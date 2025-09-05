package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.collapsable

import br.com.mobicare.cielo.databinding.LayoutPixScheduledSettlementCollapsableViewSectionBinding
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.PixExtractAccountBalanceViewSection
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.views.PixExtractHeaderViewSection

class PixScheduledSettlementCollapsableViewSection(
    private val fragment: PixHomeExtractFragment,
    private val viewModel: PixHomeExtractViewModel,
    private val onAccountManagementTap: () -> Unit,
    private val onBankDomicileTap: (() -> Unit)? = null,
    private val onScheduledTransferTap: () -> Unit,
) : PixExtractCollapsableViewBase(fragment, viewModel) {

    private var _binding: LayoutPixScheduledSettlementCollapsableViewSectionBinding? =
        LayoutPixScheduledSettlementCollapsableViewSectionBinding.inflate(fragment.layoutInflater)
    private val binding get() = requireNotNull(_binding)

    override val view get() = binding.root

    override val accountBalanceViewSection = PixExtractAccountBalanceViewSection(
        viewModel = viewModel,
        balanceView = binding.containerBalance,
    )

    init {
        initializeHeaderView()
        configureScheduledTransferButton()
    }

    override fun onDestroyView() {
        _binding = null
    }

    private fun initializeHeaderView() {
        PixExtractHeaderViewSection(
            fragment = fragment,
            viewModel = viewModel,
            headerView = binding.includeHeader.root,
            onAccountManagementTap = onAccountManagementTap,
            onBankDomicileTap = onBankDomicileTap,
        )
    }

    private fun configureScheduledTransferButton() {
        binding.btnScheduledTransfer.setOnClickListener { onScheduledTransferTap() }
    }

}