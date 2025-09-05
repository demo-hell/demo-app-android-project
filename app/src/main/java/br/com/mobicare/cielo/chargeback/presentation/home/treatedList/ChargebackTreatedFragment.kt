package br.com.mobicare.cielo.chargeback.presentation.home.treatedList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_TREATED
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.data.model.request.OnResultFilterListener
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.presentation.ChargebackUIConstants
import br.com.mobicare.cielo.chargeback.presentation.filters.ChargebackFiltersBottomSheetDialog
import br.com.mobicare.cielo.chargeback.presentation.home.ChargebackHomeFragmentDirections
import br.com.mobicare.cielo.chargeback.presentation.home.ChargebackHomeViewModel
import br.com.mobicare.cielo.chargeback.presentation.home.adapter.ChargebackAdapter
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentChargebackTreatedBinding
import br.com.mobicare.cielo.extensions.visible
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChargebackTreatedFragment : BaseFragment() {

    private val ga4: ChargebackGA4 by inject()
    private val viewModel: ChargebackHomeViewModel by viewModel()
    private var _binding: FragmentChargebackTreatedBinding? = null
    private val binding get() = _binding
    private var chargebacksList: MutableList<Chargeback> = mutableListOf()
    private val adapter: ChargebackAdapter by lazy {
        ChargebackAdapter(::onItemClicked)
    }

    private var filterDialog: ChargebackFiltersBottomSheetDialog? = null
    private var appliedFiltersNumber = ZERO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getChargebackTreated()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargebackTreatedBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewSetup()
        observeUiStateSetup()
        initializeFilterDialog()
        openFiltersBottomSheetListener()
        onFilterEmptyResultReloadClickListener()
    }

    override fun onResume() {
        super.onResume()
        configureNumberOfAppliedFilters(appliedFiltersNumber)
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_TREATED)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initViewSetup() {
        binding?.apply {
            rvChargebackTreated.adapter = adapter
       }
        reloadChargebackOnError()
    }

    private fun observeUiStateSetup() {
        viewModel.treatedChargebackListLiveData.observe(viewLifecycleOwner) { state ->
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

    private fun checkPaginationAndGetChargebackTreated() {
        binding?.nsChargebackTreated?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            val viewNested = (v.getChildAt(ZERO).measuredHeight) - v.measuredHeight
            if (scrollY == viewNested && viewModel.isMoreLoadingTreated.not()) {
                viewModel.pageTreated += ONE
                getChargeback()
            }
        })
    }

    private fun onLoadNextPage() {
        viewModel.isMoreLoadingTreated = true
        binding?.clContainerPaginationLoad.visible()
    }

    private fun onFilterEmpty() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            rvChargebackTreated.gone()
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
        viewModel.isMoreLoadingTreated = false
        binding?.clContainerPaginationLoad.gone()
    }

    private fun getChargeback() = viewModel.getChargebackTreated()

    private fun reloadChargebackOnError() {
        binding?.includeErrorTreatedChargeback?.btReloadChargeback?.setOnClickListener {
            getChargeback()
        }
    }

    private fun onItemClicked(chargeback: Chargeback) {
        findNavController().navigate(
            ChargebackHomeFragmentDirections
                .actionChargebackInitFragmentToChargebackDoneDetailsFragment(chargeback)
        )
    }

    private fun onEmpty() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeEmptyTreatedChargeback.root.visible()
            rvChargebackTreated.gone()
        }
    }

    private fun onLoading() {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeErrorTreatedChargeback.root.gone()
            includeEmptyTreatedChargeback.root.gone()
            includeFilterEmptyResult.root.gone()
            rvChargebackTreated.gone()
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
            includeEmptyTreatedChargeback.root.gone()
            rvChargebackTreated.gone()
            includeFilterEmptyResult.root.gone()
            includeErrorTreatedChargeback.root.visible()
        }
        ga4.logException(SCREEN_VIEW_CHARGEBACK_TREATED, error)
    }

    private fun onSuccess(chargebacks: Chargebacks) {
        binding?.apply {
            showOrHideFilterContainerLayout(true)
            includeErrorTreatedChargeback.root.gone()
            includeEmptyTreatedChargeback.root.gone()
            if(viewModel.isTreatedChargebacksAlreadyLoaded.not()){
                chargebacksList.addAll(chargebacks.content)

            }
            adapter.clear()
            setupRecyclerView()
            checkPaginationAndGetChargebackTreated()
            viewModel.isTreatedChargebacksAlreadyLoaded = true
        }
    }

    private fun setupRecyclerView(){
        binding?.apply {
            chargebacksList.let {
                rvChargebackTreated.visible()
                adapter.update(it)
            }
        }
    }


    private fun showOrHideFilterContainerLayout(toShow: Boolean) {
        binding?.apply {
            this.filterAreaContainerLayout.root.visible(toShow)
        }
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
        viewModel.clearFilters(ChargebackConstants.DONE)
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

            }, ChargebackConstants.DONE )
    }


    private fun openFiltersBottomSheetListener() {
        binding?.apply {
            this.filterAreaContainerLayout.openFilterScreenButton.setOnClickListener {
                if(filterDialog == null)
                    initializeFilterDialog()

                filterDialog?.show(childFragmentManager, ChargebackUIConstants.FILTERS_BOTTOM_SHEET)
            }
        }
    }



}