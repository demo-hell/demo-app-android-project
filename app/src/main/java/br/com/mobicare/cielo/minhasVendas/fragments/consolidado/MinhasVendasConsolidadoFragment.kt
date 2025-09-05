package br.com.mobicare.cielo.minhasVendas.fragments.consolidado

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.adapter.InfiniteScrollOnDefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasConsolidadoFragmentBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MinhasVendasTransacoesActivity
import kotlinx.android.synthetic.main.item_extrato_list.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

//TODO [MYSALES] - Remover posteiormente a migracao para MVVM
class MinhasVendasConsolidadoFragment : BaseFragment(), MinhasVendasConsolidadoContract.View {

    val presenter: MinhasVendasConsolidadoPresenter by inject {
        parametersOf(this)
    }

    private var adapter: InfiniteScrollOnDefaultViewListAdapter<SaleHistory>? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var binding: LayoutMinhasVendasConsolidadoFragmentBinding? = null

    private val quickfilter: QuickFilter?
        get() {
            return arguments?.getSerializable(ARG_PARAM_QUICK_FILTER) as QuickFilter?
        }


    companion object {
        const val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
        fun create(quickFilter: QuickFilter) : MinhasVendasConsolidadoFragment {
            return MinhasVendasConsolidadoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM_QUICK_FILTER, quickFilter)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        LayoutMinhasVendasConsolidadoFragmentBinding.inflate(
            inflater,
            container,
            false
        ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quickfilter?.run {
            presenter.loadInitial(this)
        }
        configureRecyclerView()
        configureSwipeRefreshLayout()
        configureListeners()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        this.presenter.onClearRequests()
    }

    override fun showLoading() {
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

    override fun hideLoading() {
        if (isAttached()) {
            binding?.apply {
                waitingLayout.gone()
                salesSummaryFooter.root.gone()
                waitingAddMoreItensLayout.gone()
            }
        }
    }

    override fun hideRefreshLoading() {
        if (isAttached()) {
            binding?.swipeRefreshLayout?.isRefreshing = false
        }
    }

    override fun showLoadingMoreItens() {
        if (isAttached()) {
            binding?.apply {
                waitingAddMoreItensLayout.visible()
                salesSummaryFooter.root.gone()
            }
        }
    }

    override fun logout(msg: ErrorMessage) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this.context, getString(R.string.ga_extrato_day))
                    .setTitle(R.string.ga_extrato)
                    .setMessage("Sua sessão expirou.")
                    .setCancelable(false)
                    .setBtnRight(getString(R.string.ok))
                    .setOnclickListenerRight {
                        if (isAttached()) {
                            Utils.logout(requireActivity())
                        }
                    }
                    .show()
        }
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
            binding?.apply {
                recyclerView.gone()
                waitingLayout.gone()
                errorLayout.root.visible()
            }
        }
    }

    override fun showEmptyResult() {
        if (isAttached()) {
            binding?.errorLayout?.root.visible()
        }
    }

    override fun showSales(sales: ArrayList<SaleHistory>) {

        if (isAttached()) {
            binding?.recyclerView?.itemAnimator = null

            if (adapter == null) {
                adapter = InfiniteScrollOnDefaultViewListAdapter(sales, R.layout.item_extrato_list)
                binding?.swipeRefreshLayout?.let { adapter?.attachSwipeLayout(it) }
                adapter?.setOnLoadNextPageListener(object :
                    InfiniteScrollOnDefaultViewListAdapter.OnLoadNextPageListener {
                    override fun onLoadNextPage() {
                        //this@MinhasVendasConsolidadoFragment.presenter.loadMore()
                    }
                })
                adapter?.setBindViewHolderCallback(object :
                    DefaultViewListAdapter.OnBindViewHolder<SaleHistory> {
                    override fun onBind(item: SaleHistory, holder: DefaultViewHolderKotlin) {
                        if (item.date != null) {
                            val localeBrazil = Locale("pt", "BR")
                            val sdf = SimpleDateFormat("yyyy-MM-dd")
                            val sdfPtBR = SimpleDateFormat("MMM", localeBrazil)
                            val calendar = Calendar.getInstance()
                            calendar.time = sdf.parse(item.date)
                            holder.mView.textview_item_extrato_list_date?.text =
                                "${calendar.get(Calendar.DAY_OF_MONTH)} ${
                                    sdfPtBR.format(calendar.time)
                                }"
                        }
                        item.quantity?.let {
                            holder.mView.textview_item_extrato_list_quantity?.text =
                                this@MinhasVendasConsolidadoFragment.resources.getQuantityString(
                                    R.plurals.extrato_quantity,
                                    it,
                                    String.format("%02d", item.quantity)
                                )
                        }
                        if (item.amount != null) {
                            holder.mView.textview_item_extrato_list_value?.text =
                                item.amount.toPtBrRealString()
                        }
                        holder.mView.setOnClickListener {
                            this@MinhasVendasConsolidadoFragment.presenter.onItemClicked(item)
                        }
                    }
                })

                binding?.recyclerView?.adapter = adapter
            } else {
                adapter?.setNewDataSet(sales)
                Handler().postDelayed({
                    scrollControlledLinearManager?.setIsCanScroll(true)
                }, 500)
            }
        }
    }

    override fun showMoreSales(sales: List<SaleHistory>) {
        if (isAttached()) {
            binding?.apply {
                salesSummaryFooter.root.visible()
                waitingAddMoreItensLayout.gone()
                if (sales.isEmpty()) {
                    adapter?.setEndOfTheList(true)
                } else {
                    adapter?.addMoreInList(sales)
                }
            }
        }
    }

    override fun showSummary(summary: Summary) {
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

    private fun configureRecyclerView() {
        if (isAttached()) {
            scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
            binding?.recyclerView?.layoutManager = this.scrollControlledLinearManager
        }
    }

    private fun configureSwipeRefreshLayout() {
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            scrollControlledLinearManager?.setIsCanScroll(false)
            presenter.refresh()
        }
    }

    private fun configureListeners() {
        binding?.errorLayout?.buttonRefreshTransactions?.setOnClickListener {
            presenter.retry()
        }
    }

    override fun showSalesMoviment(filter: QuickFilter) {
        MinhasVendasTransacoesActivity.create(requireContext(), filter)
    }

}