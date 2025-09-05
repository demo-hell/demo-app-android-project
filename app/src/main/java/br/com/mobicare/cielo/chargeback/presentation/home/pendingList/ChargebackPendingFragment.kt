package br.com.mobicare.cielo.chargeback.presentation.home.pendingList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.data.model.request.OnResultFilterListener
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.presentation.ChargebackUIConstants.FILTERS_BOTTOM_SHEET
import br.com.mobicare.cielo.chargeback.presentation.filters.ChargebackFiltersBottomSheetDialog
import br.com.mobicare.cielo.chargeback.presentation.home.ChargebackHomeFragmentDirections
import br.com.mobicare.cielo.chargeback.presentation.home.ChargebackHomeViewModel
import br.com.mobicare.cielo.chargeback.presentation.home.adapter.ChargebackAdapter
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.databinding.FragmentChargebackPendingBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChargebackPendingFragment : Fragment() {

    private val ga4: ChargebackGA4 by inject()
    private val viewModel: ChargebackHomeViewModel by viewModel()
    private var _binding: FragmentChargebackPendingBinding? = null
    private val binding get() = _binding
    private var chargebacksList: MutableList<Chargeback> = mutableListOf()
    private val adapter: ChargebackAdapter by lazy {
        ChargebackAdapter(::onItemClicked)
    }

    private var filterDialog: ChargebackFiltersBottomSheetDialog? = null
    private var appliedFiltersNumber = ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getChargebackPending()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargebackPendingBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewSetup()
        observeUiStateSetup()
        openFiltersBottomSheetListener()
        onFilterEmptyResultReloadClickListener()
    }

    override fun onResume(){
        super.onResume()
        configureNumberOfAppliedFilters(appliedFiltersNumber)
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDING)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViewSetup() {
        binding?.apply {
            rvChargebackPending.adapter = adapter
        }
        reloadChargebackOnError()
    }

    private fun observeUiStateSetup() {
        viewModel.pendingChargebackListLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onLoading()
                is UiState.HideLoading -> onHideLoading()
                is UiState.Empty -> onEmpty()
                is UiState.HideMoreLoading -> onHideLoadNextPage()
                is UiState.MoreLoading -> onLoadNextPage()
                is UiState.Error -> onError(state.error)
                is UiState.Success -> state.data?.let { onSuccess(it) }
                is UiState.FilterEmpty -> onFilterEmpty()
            }
        }
    }

    private fun checkPaginationAndGetChargebackPending() {
        binding?.nsChargebackPending?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val viewNested = (v.getChildAt(ZERO).measuredHeight) - v.measuredHeight
            if (scrollY == viewNested && viewModel.isMoreLoadingPending.not()) {
                viewModel.pagePending += ONE
                getChargeback()
            }
        })
    }

    private fun onLoadNextPage() {
        viewModel.isMoreLoadingPending = true
        binding?.clContainerPaginationLoad.visible()
    }

    private fun onFilterEmpty() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            rvChargebackPending.gone()
            includeFilterEmptyResult.root.visible()
        }
    }


    private fun onFilterEmptyResultReloadClickListener() {
        binding?.apply {
            includeFilterEmptyResult.buttonClearFilters.setOnClickListener {
                includeFilterEmptyResult.root.gone()
                clearFilters()

            }
        }
    }

    private fun onHideLoadNextPage() {
        viewModel.isMoreLoadingPending = false
        binding?.clContainerPaginationLoad.gone()
    }

    private fun getChargeback() = viewModel.getChargebackPending()

    private fun reloadChargebackOnError() {
        binding?.includeErrorPendingChargeback?.btReloadChargeback?.setOnClickListener {
            getChargeback()
        }
    }

    private fun onItemClicked(chargeback: Chargeback) {
        findNavController().navigate(
            ChargebackHomeFragmentDirections
                .actionChargebackInitFragmentToChargebackPendingDetailsFragment(chargeback)
        )
    }

    private fun onEmpty() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeEmptyPendingChargeback.root.visible()
            rvChargebackPending.gone()
        }
    }

    private fun onLoading() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeErrorPendingChargeback.root.gone()
            includeEmptyPendingChargeback.root.gone()
            includeFilterEmptyResult.root.gone()
            rvChargebackPending.gone()
            llContainerShimmer.apply {
                visible()
                contentDescription = getString(R.string.chargeback_accessibility_shimmer_load)
            }
        }
    }

    private fun onHideLoading() = binding?.llContainerShimmer.gone()

    private fun onError(error: NewErrorMessage?) {
        binding?.apply {
            showOrHideFilterContainerLayout(viewModel.isFiltering)
            includeEmptyPendingChargeback.root.gone()
            rvChargebackPending.gone()
            includeFilterEmptyResult.root.gone()
            includeErrorPendingChargeback.root.visible()
        }
        ga4.logException(SCREEN_VIEW_CHARGEBACK_PENDING, error)
    }

    private fun onSuccess(chargebacks: Chargebacks) {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeErrorPendingChargeback.root.gone()
            includeEmptyPendingChargeback.root.gone()
            rvChargebackPending.visible()
            if(viewModel.isPendingChargebackAlreadyLoaded.not()){
                chargebacksList.addAll(chargebacks.content)
            }
            adapter.clear()
            setupRecyclerView()
            checkPaginationAndGetChargebackPending()
            viewModel.isPendingChargebackAlreadyLoaded = true
        }
    }

    private fun setupRecyclerView(){
        binding?.apply {
            chargebacksList.let {
                rvChargebackPending.visible()
                adapter.update(it)
            }
        }
    }


    private fun showOrHideFilterContainerLayout(toShow: Boolean) {
        binding?.filterAreaContainerLayout?.root?.visible(toShow)
    }

    private fun configureNumberOfAppliedFilters(numberOfFilters: Int){
        binding?.apply {
            if(numberOfFilters == ZERO){
                this.filterAreaContainerLayout.openFilterScreenButtonText.text = resources.getString(R.string.chargeback_filter_open_filter_screen_text_no_filters)
            }else {
                this.filterAreaContainerLayout.openFilterScreenButtonText.text = resources.getString(R.string.chargeback_filter_open_filter_screen_text,numberOfFilters.toString())
            }
        }
    }

    private fun clearFilters(){
        configureNumberOfAppliedFilters(ZERO)
        chargebacksList.clear()
        filterDialog = null
        appliedFiltersNumber = ZERO
        viewModel.clearFilters(ChargebackConstants.PENDING)
    }


    private fun initializeFilterDialog() {
        filterDialog =  ChargebackFiltersBottomSheetDialog.newInstance(
            applyFilterListener = object : OnResultFilterListener {

                override fun onResultFilterListener(
                    chargebackListParams: ChargebackListParams,
                    numberOfAppliedFilters: Int
                ) {
                    configureNumberOfAppliedFilters(numberOfAppliedFilters)
                    chargebacksList.clear()
                    appliedFiltersNumber = numberOfAppliedFilters
                    binding?.includeFilterEmptyResult?.root.gone()
                    viewModel.applyFilter(chargebackListParams)
                }

                override fun onClearFilterListener(chargebackStatus: String) {
                    clearFilters()
                }

            }, ChargebackConstants.PENDING )
    }

    private fun openFiltersBottomSheetListener() {
        binding?.apply {
            this.filterAreaContainerLayout.openFilterScreenButton.setOnClickListener {
                if(filterDialog == null)
                    initializeFilterDialog()

                filterDialog?.show(childFragmentManager,FILTERS_BOTTOM_SHEET)
            }
        }
    }
}