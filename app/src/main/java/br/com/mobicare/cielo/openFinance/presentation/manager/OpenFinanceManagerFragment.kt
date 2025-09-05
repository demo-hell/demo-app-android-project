package br.com.mobicare.cielo.openFinance.presentation.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieFragment.Companion.TAG
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceBsBankItemBinding
import br.com.mobicare.cielo.databinding.OpenFinanceManagerFragmentBinding
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.OpenFinanceFlowNewShareActivity
import br.com.mobicare.cielo.openFinance.presentation.utils.DefaultIconBank.checkTypeImage
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFilterList
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFeatureToggles
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.BRAND_SELECTED
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_SHARE
import com.google.gson.Gson
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceManagerFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceManagerFragmentBinding? = null
    private var navigation: CieloNavigation? = null
    private val managerViewModel: OpenFinanceManagerViewModel by viewModel()
    private var bsLoading: CieloBottomSheet? = null
    private var bsBanks: CieloBottomSheet? = null

    private val toolbarDefault
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.open_finance),
                showBackButton = true,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_only_faq_blue,
                    onOptionsItemSelected = {}
                )
            )
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceManagerFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        managerViewModel.checkFeatureToggles()
        mountView()
        showOnboarding()
        setListeners()
        observeFeatureToggles()
        observeBanks()
        observeList()
    }

    private fun showOnboarding() {
        if (managerViewModel.checkSeenOnboarding().not()) {
            requireActivity().startActivity<OpenFinanceOnBoardingActivity>()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarDefault)
    }

    private fun mountView() {
        binding?.apply {
            shimmerLifecycleIndicator.gone()
            containerMain.visible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setListeners() {
        binding?.containerDataShared?.setOnClickListener {
            goToSharedData()
        }
        binding?.newSharing?.setOnClickListener {
            managerViewModel.getBanks(EMPTY)
        }
    }

    private fun observeNewSharing() {
        managerViewModel.getNewSharingLiveData.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowFeatureToggles.ShowFeatureToggles -> {
                        containerNewSharing.visible()
                    }

                    is UIStateShowFeatureToggles.HideFeatureToggles -> {
                        containerNewSharing.gone()
                    }
                }
            }
        }
    }

    private fun observeDataShared() {
        managerViewModel.getDataSharedLiveData.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowFeatureToggles.ShowFeatureToggles -> {
                        containerDataShared.visible()
                    }

                    is UIStateShowFeatureToggles.HideFeatureToggles -> {
                        containerDataShared.gone()
                    }
                }
            }

        }
    }

    private fun observePayments() {
        managerViewModel.getPaymentsLiveData.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowFeatureToggles.ShowFeatureToggles -> {
                        containerPayments.visible()
                    }

                    is UIStateShowFeatureToggles.HideFeatureToggles -> {
                        containerPayments.gone()
                    }
                }
            }
        }
    }

    private fun observeBanks() {
        managerViewModel.getBanksLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsentDetail.Loading -> {
                    showBottomSheetLoading()
                }

                is UIStateConsentDetail.Success -> {
                    bsLoading?.dismiss()
                    uiState.data?.let { openNewSharing(it) }
                }

                is UIStateConsentDetail.ErrorWithoutAccess -> {
                    bsLoading?.dismiss()
                    RoleWithoutAccessHandler.showNoAccessAlert(requireActivity())
                }

                is UIStateConsentDetail.Error -> {
                    bsLoading?.dismiss()
                    showBottomSheetError()
                }
            }
        }
    }

    private fun goToSharedData() {
        findNavController().navigate(
            OpenFinanceManagerFragmentDirections
                .actionOpenFinanceManagerFragmentToOpenFinanceSharedDataFragment(false)
        )
    }

    private fun openNewSharing(data: List<Brand>) {
        var bankSelected: Brand? = null
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.select_institution),
            ),
            searchConfigurator = CieloBottomSheet.SearchConfigurator(
                isShowSearchBar = true,
                isShowSearchIcon = true,
                hintSearchBar = getString(R.string.busca),
                onSearch = { searchString, bottomSheet ->
                    onSearch(searchString, bottomSheet)
                }
            ),
            layoutItemRes = R.layout.layout_open_finance_bs_bank_item,
            data = data,
            initialSelectedItem = bankSelected,
            onViewBound = { bank, isSelected, itemView ->
                mountViewBound(bank, isSelected, itemView)
            },
            onItemClicked = { bankAccount, position, bottomSheet ->
                bankSelected = bankAccount
                itemSelected(position, bottomSheet, bankSelected)
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.selecionar),
                onTap = {},
                startEnabled = false,
            )
        ).show(childFragmentManager, TAG)
    }

    private fun onSearch(searchString: String, bottomSheet: CieloBottomSheet) {
        (bottomSheet as CieloListBottomSheet<Brand>).updateSelectedPosition(ONE_NEGATIVE)
        bottomSheet.changeButtonStatus(false)
        managerViewModel.filterList(searchString)
        bsBanks = bottomSheet
    }

    private fun observeList() {
        managerViewModel.getListFilterLiveData.observe(viewLifecycleOwner) { uiState ->
            if (bsBanks == null) return@observe
            val bottomSheet = (bsBanks as CieloListBottomSheet<Brand>)
            when (uiState) {
                is UIStateFilterList.ListFiltered -> {
                    bsBanks?.hideSearchErrorMessage()
                    uiState.data?.let { bottomSheet.updateList(it) }
                }

                is UIStateFilterList.NotFound -> {
                    uiState.data?.let { bottomSheet.updateList(it) }
                    bottomSheet.showSearchErrorMessage(getString(R.string.brands_opf_filter_list_error))
                }
            }
        }
    }

    private fun mountViewBound(bank: Brand, isSelected: Boolean, itemView: View) {
        LayoutOpenFinanceBsBankItemBinding.bind(itemView).apply {
            bank.institutions?.firstOrNull()
                ?.let { checkTypeImage(it.logoUri, ivIconBank, requireContext()) }
            tvBankName.text = bank.brand
            tvInstitutions.text = getString(
                if ((bank.institutions?.size
                        ?: 0) > 1
                ) R.string.institutions
                else R.string.institution, bank.institutions?.size
            )
            ivRadioButton.isSelected = isSelected
            root.isSelected = isSelected
        }
    }

    private fun itemSelected(
        position: Int,
        bottomSheet: CieloListBottomSheet<Brand>,
        bankSelected: Brand?
    ) {
        bottomSheet.updateSelectedPosition(position)
        bottomSheet.updateMainButtonConfigurator(
            CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.selecionar),
                onTap = {
                    managerViewModel.resetState()
                    bottomSheet.dismiss()
                    requireActivity().startActivity<OpenFinanceFlowNewShareActivity>(
                        BRAND_SELECTED to Gson().toJson(bankSelected),
                        TYPE_SHARE to ZERO
                    )
                },
                startEnabled = true,
            )
        )
    }

    private fun observeFeatureToggles() {
        observeNewSharing()
        observeDataShared()
        observePayments()
    }

    private fun showBottomSheetLoading() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.select_institution)),
                contentLayoutRes = R.layout.layout_open_finance_loading_bottomsheet,
                onContentViewCreated = { view, bs -> bsLoading = bs }
            ).show(childFragmentManager, EMPTY)
    }

    private fun showBottomSheetError() {
        CieloContentBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(title = getString(R.string.select_institution)),
                contentLayoutRes = R.layout.layout_open_finance_error_bottomsheet,
            ).show(childFragmentManager, EMPTY)
    }

    override fun onStop() {
        super.onStop()
        managerViewModel.resetState()
    }
}