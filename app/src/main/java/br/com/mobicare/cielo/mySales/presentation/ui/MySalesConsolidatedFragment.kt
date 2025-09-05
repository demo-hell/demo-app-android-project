package br.com.mobicare.cielo.mySales.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasConsolidadoFragmentBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.minhasVendas.fragments.consolidado.MinhasVendasConsolidadoFragment
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.presentation.ui.adapter.consolidatedSales.ConsolidatedSalesAdapter
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HistorySalesViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import org.koin.androidx.viewmodel.ext.android.viewModel

class MySalesConsolidatedFragment: BaseFragment() {

    private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"

    private var _binding: LayoutMinhasVendasConsolidadoFragmentBinding? = null
    private val binding get() = _binding

    private lateinit var adapter: ConsolidatedSalesAdapter
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null

    private val viewModel: HistorySalesViewModel by viewModel()


    companion  object {
        fun newInstance(quickFilter: QuickFilter): MySalesConsolidatedFragment {
            return MySalesConsolidatedFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(MinhasVendasConsolidadoFragment.ARG_PARAM_QUICK_FILTER, quickFilter)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutMinhasVendasConsolidadoFragmentBinding.inflate(
            inflater, container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.getSerializable(ARG_PARAM_QUICK_FILTER)?.let {
            val filterArgument = arguments?.getSerializable(ARG_PARAM_QUICK_FILTER) as QuickFilter
            viewModel.quickFilter = filterArgument
            viewModel.getSalesHistory(filterArgument)
        }
        configureRecyclerView()
        configureSwipeRefreshLayout()
        configureRetryListener()
    }


    override fun onResume() {
        super.onResume()
        handleSalesHistoryAPIService()
    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    private fun handleSalesHistoryAPIService() {
        viewModel.getSalesHistoryDataViewState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is MySalesViewState.SUCCESS -> showSuccessSalesHistory(state.data)
                is MySalesViewState.EMPTY -> showEmptyResult()
                is MySalesViewState.LOADING -> showLoading()
                is MySalesViewState.ERROR -> showError()
            }
        }
    }

    private  fun showLoading() {
        if (isAttached()) {
            binding?.apply {
                salesSummaryFooter.root.gone()
                salesSummaryFooter.root.gone()
                waitingAddMoreItensLayout.gone()
                errorLayout.root.gone()
                waitingLayout.visible()
            }
        }
    }

    private fun hideLoading() {
        if (isAttached()) {
            binding?.apply {
                waitingLayout.gone()
                salesSummaryFooter.root.gone()
                waitingAddMoreItensLayout.gone()
            }
        }
    }


    private fun showError() {
        if (isAttached()) {
            hideLoading()
            binding?.apply {
                recyclerView.gone()
                waitingLayout.gone()
                errorLayout.root.visible()
            }
        }
    }

    private fun showEmptyResult() {
        if (isAttached()) {
            hideLoading()
            binding?.errorLayout?.root.visible()
        }
    }

    private fun configureRecyclerView() {
        if (isAttached()) {
            adapter = ConsolidatedSalesAdapter { dateStr ->
                goToSalesHistoryMoviment(dateStr)
            }
            scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
            binding?.recyclerView?.layoutManager = this.scrollControlledLinearManager
            binding?.recyclerView?.adapter = adapter
        }
    }

    private fun configureSwipeRefreshLayout() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            scrollControlledLinearManager?.setIsCanScroll(false)
            viewModel.refresh()
        }
    }

    private fun configureRetryListener() {
        binding?.errorLayout?.buttonRefreshTransactions?.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun hideRefreshLoading() {
        if (isAttached()) {
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    private fun showSuccessSalesHistory(data: ResultSummarySalesHistoryBO?) {
        data?.let { salesHistory ->
            if(viewModel.isRefreshing){
                hideRefreshLoading()
                viewModel.isRefreshing = false
            }else
                hideLoading()

            scrollControlledLinearManager?.setIsCanScroll(true)
            showSummary(salesHistory.summary)
            binding?.recyclerView.visible()
            salesHistory.items?.let { adapter.updateAdapter(it) }
        }
    }

    private fun showSummary(summary: Summary) {
        val totalQuantity = summary.totalQuantity ?: ZERO
        val totalAmount = summary.totalAmount ?: ZERO_DOUBLE
        val totalNetAmount = summary.totalNetAmount ?: ZERO_DOUBLE
        val isShowFooter = totalAmount > ZERO_DOUBLE
        val isShowNetAmount = totalNetAmount > ZERO_DOUBLE

        if (isAttached()) {
            binding?.salesSummaryFooter?.apply {
                root.visible(isShowFooter)
                if (isShowFooter) {
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
    }


    private fun goToSalesHistoryMoviment(date: String?) {
        date?.let{ itDate ->
            val newQuickFilter = QuickFilter.Builder().apply {
                from(viewModel.quickFilter)
                initialDate(itDate)
                finalDate(itDate)
            }.build()
            val intent = Intent(requireContext(), MySalesTransactionsActivity::class.java)
            intent.putExtra(ARG_PARAM_QUICK_FILTER,newQuickFilter)
            startActivity(intent)
        }
    }

}