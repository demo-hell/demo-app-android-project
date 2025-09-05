package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.FragmentPixExtractPageBaseBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixExtractPageListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixHomeExtractListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.adapter.PixExtractTransactionAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.controller.PixExtractFilterController
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel.PixExtractPageViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState

abstract class PixExtractPageBaseFragment :
    BaseFragment(),
    CieloNavigationListener,
    PixExtractPageListener {
    protected abstract val viewModel: PixExtractPageViewModel
    protected abstract val pageType: PixReceiptsTab
    protected abstract val pixHomeExtractListener: PixHomeExtractListener

    protected abstract fun onItemClick(receipt: Any)

    private var binding: FragmentPixExtractPageBaseBinding? = null

    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var transactionsAdapter: PixExtractTransactionAdapter? = null
    private lateinit var filterController: PixExtractFilterController

    private val isCurrentTab
        get(): Boolean {
            return pixHomeExtractListener.getCurrentTab() == pageType.ordinal ||
                (pixHomeExtractListener.getCurrentTab() == TWO && pageType == PixReceiptsTab.NEW_SCHEDULES) // TODO: REMOVER ESSA CONDIÇÃO QUANDO EXCLUIR O FRAGMENT DE AGENDAMENTOS ANTIGO
        }

    private val extractStateIsEmptyOrError
        get(): Boolean {
            val state = viewModel.extractState.value
            return state is UIPixExtractPageState.EmptyTransactions || state is UIPixExtractPageState.Error
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixExtractPageBaseBinding
        .inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setMinimumHeight()
        setupFilterController()
        setupObservers()
        setupRecyclerView()
        setupListeners()

        viewModel.setTab(pageType)
    }

    override fun onResume() {
        super.onResume()

        if (isCurrentTab) {
            if (extractStateIsEmptyOrError) pixHomeExtractListener.resetLayoutToolbar()
            viewModel.loadTransactions(isOnResume = true, isSwipe = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun reloadExtract() {
        viewModel.loadTransactions(isOnResume = false, isSwipe = false)
    }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun setupObservers() {
        setupObserverExtractState()
        setupObserverFilterData()
    }

    private fun setupObserverExtractState() {
        viewModel.extractState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIPixExtractPageState.ShowLoading -> {
                    showLoading()
                    showEmptyTransactions(false)
                    showErrorLoadTransactions(false)
                }
                is UIPixExtractPageState.ShowLoadingMoreTransactions -> {
                    showLoadingMoreTransactions(true)
                }
                is UIPixExtractPageState.HideLoading -> {
                    hideLoading()
                }
                is UIPixExtractPageState.HideLoadingSwipe -> {
                    hideSwipe()
                }
                is UIPixExtractPageState.HideLoadingMoreTransactions -> {
                    showLoadingMoreTransactions(false)
                }
                is UIPixExtractPageState.Success -> {
                    onSuccessLoadExtract(state.transactions, state.isMoreTransactions)
                }
                is UIPixExtractPageState.Error -> {
                    showErrorLoadTransactions(true)
                }
                is UIPixExtractPageState.EmptyTransactions -> {
                    showEmptyTransactions(true)
                }
                is UIPixExtractPageState.EmptyTransactionsWithActiveFilter -> {
                    showEmptyTransactionsWithActiveFilter()
                }
            }
        }
    }

    private fun setupObserverFilterData() {
        viewModel.filterData.observe(viewLifecycleOwner) {
            pixHomeExtractListener.onLoadAccountBalance()
            transactionsAdapter?.setFilterData(it)
        }
    }

    private fun setupRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        transactionsAdapter = PixExtractTransactionAdapter(pageType, filterController, ::onItemClick)

        viewModel.filterData.value?.let { transactionsAdapter?.setFilterData(it) }

        transactionsAdapter?.clearTransactions()
        transactionsAdapter?.addTransactions(viewModel.transactions)

        binding?.apply {
            swipeRefreshLayout.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.transparent,
                ),
            )
            swipeRefreshLayout.setColorSchemeResources(R.color.brand_400)

            rvExtract.adapter = transactionsAdapter
            rvExtract.layoutManager = scrollControlledLinearManager
            rvExtract.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int,
                    ) {
                        super.onScrolled(recyclerView, dx, dy)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastItem = layoutManager.findLastVisibleItemPosition()
                        val totalItems = layoutManager.itemCount
                        if (lastItem == totalItems - ONE) {
                            viewModel.loadMoreTransactions()
                        }
                    }
                },
            )
        }
    }

    private fun setupListeners() {
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadTransactions(isOnResume = false, isSwipe = true)
                pixHomeExtractListener.onLoadAccountBalance()
            }

            includeErrorLoadTransactions.btnReload.setOnClickListener {
                viewModel.loadTransactions(isOnResume = false, isSwipe = false)
                pixHomeExtractListener.onLoadAccountBalance()
            }
        }
    }

    private fun setupFilterController() {
        filterController =
            PixExtractFilterController(
                viewModel,
                requireContext(),
                requireActivity().supportFragmentManager,
            )
    }

    private fun onSuccessLoadExtract(
        transactions: ArrayList<Any>,
        isMoreTransactions: Boolean,
    ) {
        if (isMoreTransactions.not()) transactionsAdapter?.clearTransactions()
        transactionsAdapter?.addTransactions(transactions)
    }

    private fun showLoading() {
        pixHomeExtractListener.onLoadAccountBalance()
        resetLayoutToolbar()

        binding?.apply {
            includeShimmerLoadTransactions.root.visible()
            includeShimmerLoadTransactions.shimmerLoading.apply {
                visible()
                sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
            }
            swipeRefreshLayout.gone()
        }
    }

    private fun hideLoading() {
        binding?.apply {
            includeShimmerLoadTransactions.root.gone()
            includeShimmerLoadTransactions.shimmerLoading.gone()
            swipeRefreshLayout.visible()
        }
    }

    private fun hideSwipe() {
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    private fun showLoadingMoreTransactions(isShow: Boolean) {
        transactionsAdapter?.showLoading(isShow)
    }

    private fun showErrorLoadTransactions(isShow: Boolean) {
        resetLayoutToolbar()

        binding?.apply {
            includeErrorLoadTransactions.root.visible(isShow)
            if (isShow) swipeRefreshLayout.gone()
        }
    }

    private fun showEmptyTransactions(isShow: Boolean) {
        resetLayoutToolbar()

        binding?.apply {
            includeEmptyTransactions.root.visible(isShow)
            if (isShow) swipeRefreshLayout.gone()
        }
    }

    private fun resetLayoutToolbar() {
        if (isCurrentTab) pixHomeExtractListener.resetLayoutToolbar()
    }

    private fun showEmptyTransactionsWithActiveFilter() {
        transactionsAdapter?.showClearFilter()
    }
}
