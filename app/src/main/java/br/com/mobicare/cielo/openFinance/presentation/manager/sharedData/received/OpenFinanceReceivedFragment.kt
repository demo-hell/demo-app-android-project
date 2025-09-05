package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.received

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieFragment
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceBsBankItemBinding
import br.com.mobicare.cielo.databinding.OpenFinanceReceivedFragmentBinding
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.presentation.manager.OpenFinanceManagerViewModel
import br.com.mobicare.cielo.openFinance.presentation.manager.adapter.OpenFinanceSharedDataAdapter
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.OpenFinanceFlowNewShareActivity
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.OpenFinanceSharedDataViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.DefaultIconBank.checkTypeImage
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsents
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFilterList
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFeatureToggles
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFinalList
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.closeShimmerLoading
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.hideList
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.hideNoDataMessage
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.showList
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.showNoDataMessage
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData.showShimmerLoading
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_SHARE
import com.google.gson.Gson
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceReceivedFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceReceivedFragmentBinding? = null
    private lateinit var adapterSharedData: OpenFinanceSharedDataAdapter
    private val dataSharedViewModel: OpenFinanceSharedDataViewModel by viewModel()
    private val managerViewModel: OpenFinanceManagerViewModel by viewModel()
    private var navigation: CieloNavigation? = null
    private var totalItensReceived = ONE_NEGATIVE
    private var oldState = ZERO
    private var bsLoading: CieloBottomSheet? = null
    private var bsBanks: CieloBottomSheet? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceReceivedFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        dataSharedViewModel.checkFeatureToggleNewSharing()
        observeConsentsReceived()
        observeFinalList()
        setMinimumHeight()
        observeNewSharing()
        setListeners()
        observeBanks()
        observeList()
    }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@OpenFinanceReceivedFragment)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.containerShowFinalListReceived.gone()
        dataSharedViewModel.reloadPageReceived()
        setRecycler()
    }

    private fun observeConsentsReceived() {
        dataSharedViewModel.getConsentsReceivedLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsents.Success -> {
                    totalItensReceived = uiState.data?.summary?.totalQuantity ?: ONE_NEGATIVE
                    uiState.data?.items?.let {
                        uiStateSuccessConsentsReceived()
                        adapterSharedData.update(it)
                    }
                }

                is UIStateConsents.ErrorWithoutAccess -> {
                    RoleWithoutAccessHandler.showNoAccessAlert(requireActivity())
                }

                is UIStateConsents.Error -> {
                    uiStateErrorConsentsReceived()
                }

                is UIStateConsents.Loading -> {
                    uiStateLoadingConsentsReceived()
                }
            }
        }
    }

    private fun setRecycler() {
        adapterSharedData = OpenFinanceSharedDataAdapter()
        binding?.apply {
            listReceived.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterSharedData

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        scrollStateChanged(recyclerView, newState)
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        scrolled(recyclerView)
                    }
                })
            }
        }
    }

    private fun scrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        binding?.listReceived?.post {
            val isScrollingDownAndReachedEnd =
                recyclerView.canScrollVertically(ONE).not() && newState > oldState
            if (isScrollingDownAndReachedEnd) {
                oldState = newState
                dataSharedViewModel.getNextPageReceived()
            }
        }
    }

    private fun scrolled(recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
        val totalItemCount = layoutManager?.itemCount
        if (totalItemCount == totalItensReceived) {
            dataSharedViewModel.checkFinalListReceived(true)
        } else {
            dataSharedViewModel.checkFinalListReceived(false)
        }
    }

    private fun observeFinalList() {
        dataSharedViewModel.showFinalListReceived.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowFinalList.ShowFinalList -> {
                        containerShowFinalListReceived.visible()
                    }

                    is UIStateShowFinalList.HideFinalList -> {
                        containerShowFinalListReceived.gone()
                    }
                }
            }
        }
    }

    private fun uiStateSuccessConsentsReceived() {
        binding?.apply {
            shimmerIndicatorReceived.let { closeShimmerLoading(it) }
            listReceived.let { showList(it) }
        }
    }

    private fun uiStateErrorConsentsReceived() {
        binding?.apply {
            shimmerIndicatorReceived.let { closeShimmerLoading(it) }
            listReceived.let { hideList(it) }
            dontHaveSharedDataReceived.let { showNoDataMessage(it) }
        }
    }

    private fun uiStateLoadingConsentsReceived() {
        binding?.apply {
            shimmerIndicatorReceived?.let { showShimmerLoading(it) }
            listReceived?.let { hideList(it) }
            dontHaveSharedDataReceived?.let { hideNoDataMessage(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun observeNewSharing() {
        dataSharedViewModel.getNewSharingLiveData.observe(viewLifecycleOwner) { uiState ->
            binding?.btnNewSharing.apply {
                when (uiState) {
                    is UIStateShowFeatureToggles.ShowFeatureToggles -> {
                        visible()
                    }

                    is UIStateShowFeatureToggles.HideFeatureToggles -> {
                        gone()
                    }
                }
            }
        }
    }

    private fun setListeners() {
        binding?.btnNewSharing?.setOnClickListener {
            managerViewModel.getBanks(EMPTY)
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
        ).show(childFragmentManager, BiometricTokenSelfieFragment.TAG)
    }

    private fun onSearch(searchString: String, bottomSheet: CieloBottomSheet) {
        (bottomSheet as CieloListBottomSheet<Brand>).updateSelectedPosition(ONE_NEGATIVE)
        bottomSheet.changeButtonStatus(false)
        managerViewModel.filterList(searchString)
        bsBanks = bottomSheet
    }

    private fun mountViewBound(bank: Brand, isSelected: Boolean, itemView: View) {
        LayoutOpenFinanceBsBankItemBinding.bind(itemView).apply {
            bank.institutions?.firstOrNull()?.let { checkTypeImage(it.logoUri, ivIconBank, requireContext()) }
            tvBankName.text = bank.brand
            tvInstitutions.text = getString(if ((bank.institutions?.size ?: 0) > 1) R.string.institutions
            else R.string.institution, bank.institutions?.size)
            ivRadioButton.isSelected = isSelected
            root.isSelected = isSelected
        }
    }

    private fun itemSelected(position: Int, bottomSheet: CieloListBottomSheet<Brand>, bankSelected: Brand?) {
        bottomSheet.updateSelectedPosition(position)
        bottomSheet.updateMainButtonConfigurator(
            CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.selecionar),
                    onTap = {
                        requireActivity().startActivity<OpenFinanceFlowNewShareActivity>(
                            OpenFinanceConstants.BRAND_SELECTED to Gson().toJson(bankSelected),
                            TYPE_SHARE to ZERO
                        )
                        managerViewModel.resetState()
                        bottomSheet.dismiss()
                    },
                startEnabled = true,
            )
        )
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