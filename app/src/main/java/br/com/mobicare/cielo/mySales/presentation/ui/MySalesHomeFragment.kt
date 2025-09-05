package br.com.mobicare.cielo.mySales.presentation.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasOnlineFragmentBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.minhasVendas.activities.CanceledDetailsActivity
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_MINHAS_VENDAS
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.presentation.utils.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.canceledSales.CanceledSalesAdapter
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.sales.SalesAdapter
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.recyclerViewOnScrollListener
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MySalesHomeFragment: BaseFragment() {

    private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
    private val REFRESH_GA_ANALYTICS_NAME = "Atualizar"

    private var _binding: LayoutMinhasVendasOnlineFragmentBinding? = null
    private val binding get() = _binding

    val ga4: MySalesGA4 by inject()
    private val viewModel: HomeSalesViewModel by viewModel()
    private lateinit var quickFilter: QuickFilter

    private lateinit var salesAdapter: SalesAdapter
    private var canceledSalesAdapter: CanceledSalesAdapter? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutMinhasVendasOnlineFragmentBinding.inflate(inflater, container,false)

        Analytics.trackScreenView(
            screenName = SCREENVIEW_MINHAS_VENDAS,
            screenClass = this.javaClass
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.getSerializable(ARG_PARAM_QUICK_FILTER)?.let {
            quickFilter = arguments?.getSerializable(ARG_PARAM_QUICK_FILTER) as QuickFilter
        }
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupErrorViewRefreshListener()
        configureSwipeRefreshLayout()
    }

    override fun onResume() {
        super.onResume()
        handleMySalesAPIService()
        handleCanceledSalesAPIService()
        viewModel.getSales(quickFilter)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance(quickFilter: QuickFilter): MySalesHomeFragment {
            return MySalesHomeFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM_QUICK_FILTER,quickFilter)
                }
            }
        }
    }

    // region - metodos de observacao dos resultados da api
    private fun handleMySalesAPIService() {
        viewModel.getSalesDataViewState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> configureSuccessView(state.data)
                is MySalesViewState.SUCCESS_PAGINATION -> showMoreSales(state.data)
                is MySalesViewState.EMPTY -> showEmptySales()
                is MySalesViewState.ERROR_FULL_SCREEN -> showFullScreenError(state.newErrorMessage)
                is MySalesViewState.LOADING -> showLoading()
                is MySalesViewState.LOADING_MORE -> showLoadingMoreItems()
                is MySalesViewState.ERROR -> showError(state.newErrorMessage)
            }
        }
    }


    private fun handleCanceledSalesAPIService() {
        viewModel.getCanceledSalesDataViewState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> configureCanceledSalesSuccessView(state.data)
                is MySalesViewState.SUCCESS_PAGINATION -> showMoreCanceledSales(state.data)
                is MySalesViewState.LOADING -> showLoading()
                is MySalesViewState.EMPTY -> showEmptyCanceledSales()
                is MySalesViewState.ERROR_FULL_SCREEN -> showFullScreenError(state.newErrorMessage)
                is MySalesViewState.LOADING_MORE -> showLoadingMoreItems()
            }
        }
    }

    //endregion

    //region - Metodos de atualizacao da view e navegacao

    private fun configureSuccessView(data: SummarySalesBO?) {
        data?.let {
            if(viewModel.isRefreshing)
                hideRefreshLoading()
            else
                hideLoading()

            scrollControlledLinearManager?.setIsCanScroll(true)
            showSummary(it.summary)
            salesAdapter.updateAdapter(it.items)
        }
    }

    private fun configureCanceledSalesSuccessView(data: CanceledSummarySalesBO?) {
        data?.let {
            if(viewModel.isRefreshing)
                hideRefreshLoading()
            else
                hideLoading()

            scrollControlledLinearManager?.setIsCanScroll(true)
            showCanceledSales(it)
        }
    }

    private fun showCanceledSales(canceledSummarySalesBO: CanceledSummarySalesBO) {
        val sales = canceledSummarySalesBO.items
        val summary = canceledSummarySalesBO.summary
        sales?.let { itSales ->
            binding?.apply {
                footerLayout.gone()
                containerSummaryCanceled.root.visible()
                containerSummaryCanceled.apply {
                    groupingQuantityCanceled.text = summary?.totalQuantity?.toString()
                    groupingValueCanceled.text = summary?.totalAmount?.toPtBrRealString()
                }
                canceledSalesAdapter = CanceledSalesAdapter {
                    goToCanceledSaleDetail(it)
                }

                recyclerView.adapter = canceledSalesAdapter
                canceledSalesAdapter?.setCanceledSales(itSales)
                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (recyclerView.canScrollVertically(ONE)
                                .not() && newState == RecyclerView.SCROLL_STATE_IDLE
                        ) {
                            viewModel.loadMoreCanceledSalesData()
                        }
                    }
                })
                recyclerView.post {
                    scrollControlledLinearManager?.setIsCanScroll(true)
                }
            }

        }
    }

    private fun goToCanceledSaleDetail(canceledSale: CanceledSale) {
        val startSaleDetailsIntent = Intent(
            requireContext(),
            CanceledDetailsActivity::class.java
        )
        startSaleDetailsIntent.putExtra(CanceledDetailsActivity.CANCELED_SALE_ARGS, canceledSale)
        startActivity(startSaleDetailsIntent)
    }


    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter(quickFilter = quickFilter, clickListener = { sale ->
            gotoSaleDetails(sale)
        })

        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        binding?.apply {
            recyclerView.layoutManager = scrollControlledLinearManager
            recyclerView.itemAnimator = null
            recyclerView.adapter = salesAdapter

            recyclerViewOnScrollListener(
                recyclerView = recyclerView,
                linearLayoutManager = scrollControlledLinearManager,
                isLastPageListener = { viewModel.isLastPagingData },
                isLoadingListener = { viewModel.isLoadingMorePagingData },
                loadMoreItems = { viewModel.loadMoreSalesData() }
            )

        }
    }

    private fun configureSwipeRefreshLayout() {
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener {
                scrollControlledLinearManager?.setIsCanScroll(false)
                viewModel.refresh()
            }
        }
    }

    private fun showMoreSales(summarySalesBO: SummarySalesBO?) {
        summarySalesBO?.let {
            val sales = it.items
            binding?.apply {
                salesSummaryFooter.root.visible()
                waitingAddMoreItensLayout.gone()
                if(sales.isEmpty().not()){
                    recyclerView.recycledViewPool.clear()
                    recyclerView.stopScroll()
                    salesAdapter.addMoreData(sales)
                    scrollControlledLinearManager?.setIsCanScroll(true)
                }
            }
        }
    }

    private fun showMoreCanceledSales(canceledSummarySalesBO: CanceledSummarySalesBO?) {
        val sales = canceledSummarySalesBO?.items
        sales?.let {
            binding?.waitingAddMoreItensLayout?.gone()
            canceledSalesAdapter?.addCanceledSales(it)
            scrollControlledLinearManager?.setIsCanScroll(true)
        }
    }


    private fun setupErrorViewRefreshListener() {
       binding?.apply {
           errorLayout.apply {
               buttonRefreshTransactions.setOnClickListener {
                   viewModel.retry()
                   sendGARefreshCanceledSalesButton()

               }
           }
       }
    }

    private fun hideLoading() {
        binding?.apply {
            waitingLayout.gone()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
        }
    }

    private fun hideRefreshLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
        viewModel.isRefreshing = false
    }


    private fun showLoading() {
        binding?.apply {
            waitingLayout.visible()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            errorLayout.root.gone()
            emptyCanceledSales.root.gone()
        }
    }

    private fun showLoadingMoreItems() {
        binding?.apply {
            waitingAddMoreItensLayout.visible()
            salesSummaryFooter.root.gone()
        }
    }

    private fun showEmptySales(){
        binding?.apply {
            hideLoading()
            errorLayout.root.visible()
            containerSummaryCanceled.root.gone()
        }
    }


    private fun showEmptyCanceledSales() {
        binding?.apply {
            hideLoading()
            emptyCanceledSales.root.visible()
            containerSummaryCanceled.root.gone()
        }
    }


    private fun gotoSaleDetails(sale: Sale) { //analogo ao showSaleDetail

        val intent = MySaleDetailsActivity.newInstance(requireContext(), sale)
        intent.putExtra(IS_SALE_TODAY_ARGS, true)
        startActivity(intent)
    }


    private fun showSummary(summary: Summary){
        val totalQuantity = summary.totalQuantity ?: ZERO
        val totalAmount = summary.totalAmount ?: ZERO_DOUBLE
        val totalNetAmount = summary.totalNetAmount ?: ZERO_DOUBLE
        val isShowNetAmount = totalNetAmount > ZERO_DOUBLE

        if (isAttached()) {
            binding?.salesSummaryFooter?.apply {
                root.visible()
                    txtValueApprovedSales.text = totalQuantity.toString()
                    txtValueGrossValue.text = totalAmount.toPtBrRealString()
                    txtValueGrossValue.contentDescription =
                        AccessibilityUtils.convertAmount(totalAmount, requireContext())
                    txtValueNetValue.text = totalNetAmount.toPtBrRealString()
                    txtValueNetValue.contentDescription =
                        AccessibilityUtils.convertAmount(totalNetAmount, requireContext())
                    rowNetValue.visible(isShowNetAmount)
            }
        }
    }

    private fun showFullScreenError(error: NewErrorMessage?) {
        hideLoading()
        binding?.errorLayout?.root.visible()
        exceptionGA4(error)
    }

    private fun showError(error: NewErrorMessage?) {
        showFullScreenError(error)
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_generic_error_image,
            getString(R.string.id_onboarding_bs_title_error_generic),
            getString(R.string.id_onboarding_bs_description_error_generic),
            nameBtn1Bottom = EMPTY,
            nameBtn2Bottom = getString(R.string.entendi),
            statusNameTopBar = false,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusView2Line = false,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dialog.dismiss()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    //endregion

    private fun sendGARefreshCanceledSalesButton(){
        if(isAttached()){
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
                action = listOf(Action.CARTOES),
                label = listOf(Label.BOTAO, REFRESH_GA_ANALYTICS_NAME)
            )
        }
    }

    private fun exceptionGA4(error: NewErrorMessage?){
        ga4.logException(SCREEN_NAME_SALES_MADE, newErrorMessage = error)
    }
}