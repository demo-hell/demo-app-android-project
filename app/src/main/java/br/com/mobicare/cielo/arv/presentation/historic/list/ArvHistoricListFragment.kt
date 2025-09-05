package br.com.mobicare.cielo.arv.presentation.historic.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_HISTORIC
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.presentation.historic.list.adapter.ArvHistoricCardAdapter
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentArvHistoricListBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvHistoricListFragment : BaseFragment(), CieloNavigationListener {

    private val arvHistoricListViewModel: ArvHistoricListViewModel by viewModel()
    private var binding: FragmentArvHistoricListBinding? = null
    private var navigation: CieloNavigation? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private val historicCardAdapter = ArvHistoricCardAdapter()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ) = FragmentArvHistoricListBinding.inflate(inflater, container, false)
            .also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupObservers()
        setupListeners()
        configureRecyclerView()
        getHistoric(isMore = false, isStart = true)
    }

    override fun onResume() {
        super.onResume()
        arvAnalytics.logScreenView(screenName = SCREEN_VIEW_ARV_HISTORIC)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.setupToolbar(
                    title = getString(R.string.txt_arv_historic_title),
                    isCollapsed = false
            )
        }
    }

    private fun setupObservers() {
        arvHistoricListViewModel.arvHistoricUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvHistoricState.ShowLoadingHistoric -> onShowLoading()
                is UiArvHistoricState.HideLoadingHistoric -> onHideLoading()
                is UiArvHistoricState.ShowLoadingMoreHistoric -> onShowLoadingMore()
                is UiArvHistoricState.HideLoadingMoreHistoric -> onHideLoadingMore()
                is UiArvHistoricState.Success-> onSuccessHistoric()
                is UiArvHistoricState.ErrorHistoric -> onEmptyOrErrorHistoric(uiState.error)
                else -> onEmptyOrErrorHistoric()
            }
        }

        arvHistoricListViewModel.arvHistoricLisNegotiations.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()){
                onEmptyOrErrorHistoric()
            }else{
                historicCardAdapter.setNegotiations(list)
            }
        }
    }

    private fun setupListeners() {
        binding?.layoutError?.btnReload?.setOnClickListener {
            getHistoric(isMore = false, isStart = false)
        }
    }

    private fun getHistoric(isMore: Boolean, isStart: Boolean) {
        arvHistoricListViewModel.getHistoric(isMore, isStart)
    }

    private fun navigateToDetailsNegotiations(negotiation: Item) {
        findNavController().safeNavigate(
                ArvHistoricListFragmentDirections
                        .actionArvHistoricListFragmentToArvHistoricDetailsFragment(negotiation)
        )
    }

    private fun configureRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        binding?.apply {
            root.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, y, _, _ ->
                val viewNested = (v.getChildAt(ZERO).measuredHeight) - v.measuredHeight
                if (y == viewNested) {
                    getHistoric(isMore = true, isStart = false)
                }
            })
            historicCardAdapter.setOnTap(::navigateToDetailsNegotiations)
            rvListHistoric.itemAnimator = null
            rvListHistoric.adapter = historicCardAdapter
            rvListHistoric.layoutManager = scrollControlledLinearManager
        }
    }

    private fun onShowLoading() {
        binding?.apply {
            shimmerLoading.startShimmer()
            shimmerLoading.visible()
            rvListHistoric.gone()
            layoutError.root.gone()
        }
    }

    private fun onHideLoading() {
        binding?.apply {
            shimmerLoading.stopShimmer()
            shimmerLoading.gone()
        }
    }

    private fun onShowLoadingMore() {
        binding?.pbLoading.visible()
    }

    private fun onHideLoadingMore() {
        binding?.pbLoading.gone()
    }

    private fun onSuccessHistoric() {
        binding?.apply{
            layoutError.root.gone()
            rvListHistoric.visible()
        }
    }

    private fun onEmptyOrErrorHistoric(error: NewErrorMessage? = null) {
        arvAnalytics.logException(
            screenName = SCREEN_VIEW_ARV_HISTORIC,
            error = error
        )

        binding?.apply {
            layoutError.root.visible()
            layoutError.tvSorryMessage.text = getString(R.string.txt_arv_historic_error_message)
            rvListHistoric.gone()
        }
    }

}


