package br.com.mobicare.cielo.minhasVendas.fragments.online

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
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
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.adapter.InfiniteScrollOnDefaultViewListAdapter
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutMinhasVendasOnlineFragmentBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef.CANCELADA
import br.com.mobicare.cielo.minhasVendas.activities.CanceledDetailsActivity
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_MINHAS_VENDAS
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import br.com.mobicare.cielo.minhasVendas.constants.IS_SALE_TODAY_ARGS
import br.com.mobicare.cielo.minhasVendas.detalhe.MinhasVendasDetalhesActvity
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.Item
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesStatusHelper
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import br.com.mobicare.cielo.minhasVendas.fragments.online.adapter.CanceledSalesAdapter
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.layout_item_minhas_vendas.view.*
import kotlinx.android.synthetic.main.layout_minhas_vendas_online_fragment.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//TODO [MYSALES] - REMOVER ESTE ARQUIVO POSTERIORMENTE
class MinhasVendasOnlineFragment : BaseFragment(),
    MinhasVendasOnlineContract.WithCanceledSellsView {

    val presenter: MinhasVendasOnlinePresenter by inject {
        parametersOf(this)
    }
    val ga4: MySalesGA4 by inject()

    private var adapter: InfiniteScrollOnDefaultViewListAdapter<Item>? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private val canceledSalesAdapter = CanceledSalesAdapter(this)
    private var binding: LayoutMinhasVendasOnlineFragmentBinding? = null

    companion object {
        private val ARG_PARAM_QUICK_FILTER = "ARG_PARAM_QUICK_FILTER"
        fun create(quickFilter: QuickFilter): MinhasVendasOnlineFragment {
            return MinhasVendasOnlineFragment().apply {
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
    ): View? {
        binding = LayoutMinhasVendasOnlineFragmentBinding.inflate(inflater, container, false)

        Analytics.trackScreenView(
            screenName = SCREENVIEW_MINHAS_VENDAS,
            screenClass = this.javaClass
        )

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.arguments?.getSerializable(ARG_PARAM_QUICK_FILTER)?.let {
            this.presenter.loadInitial(it as QuickFilter)
        }
        configureRecyclerView()
        configureSwipeRefreshLayout()
        configureListeners()
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        this.presenter.onClearRequests()
    }

    override fun onDestroy() {
        super.onDestroy()
        this.presenter.onDestroy()
    }

    override fun hideLoading() { 
        binding?.apply {
            waitingLayout.gone()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
        }
    }

    override fun showLoading() { 
        binding?.apply {
            waitingLayout.visible()
            salesSummaryFooter.root.gone()
            waitingAddMoreItensLayout.gone()
            errorLayout.root.gone()
            emptyCanceledSales.root.gone()
        }
    }


    override fun logout(msg: ErrorMessage?) {
        exceptionGA4(msg)
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(requireContext(), true)
        }
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            binding?.apply {
                recyclerView.gone()
                waitingLayout.gone()
                errorLayout.root.visible()
                exceptionGA4(error)
            }
        }
    }

    override fun hideRefreshLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    override fun showSales(
        sales: ArrayList<Sale>,
        summary: Summary,
        saleStatus: Int,
        isNewLoad: Boolean,
        isByRefreshing: Boolean
    ) {

        val list = ArrayList<Item>()
        if (saleStatus == CANCELADA) {
            list.add(Item(summary, 0))
        }
        sales.forEachIndexed { index, sale ->
            list.add(Item(sale, index))
        }

        if (isAttached()) {
            if (this.adapter == null) {
                this.adapter = InfiniteScrollOnDefaultViewListAdapter(
                    list,
                    R.layout.layout_item_minhas_vendas
                )
                this.adapter?.attachSwipeLayout(this.swipeRefreshLayout)
                this.adapter?.setOnLoadNextPageListener(object :
                    InfiniteScrollOnDefaultViewListAdapter.OnLoadNextPageListener {
                    override fun onLoadNextPage() {
                        this@MinhasVendasOnlineFragment.presenter.loadMore()
                    }
                })
                this.adapter?.setBindItemViewType(object :
                    DefaultViewListAdapter.OnBindItemViewType<Item> {
                    override fun onBind(position: Int, item: Item): Int {
                        return if (saleStatus == CANCELADA && position == 0)
                            R.layout.content_grouping_canceled_sales
                        else R.layout.layout_item_minhas_vendas
                    }
                })

                this.adapter?.setBindViewHolderCallback(object :
                    DefaultViewListAdapter.OnBindViewHolderPositon<Item> {
                    override fun onBind(
                        item: Item,
                        holder: DefaultViewHolderKotlin,
                        position: Int,
                        lastPositon: Int
                    ) {
                        configureItemByType(item, position, holder)
                    }

                    private fun configureItemByType(
                        item: Item,
                        position: Int,
                        holder: DefaultViewHolderKotlin
                    ) {
                        val sale = item.item as Sale
                        val saleTime: String?
                        val saleStatusCode: Int?
                        val cardBrandUrl: String?
                        val saleValue: String?

                        saleTime = if (isCanceledSells()) {
                            val inputDateFormat =
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputDtFormat =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val saleDate: String? = try {
                                outputDtFormat.format(inputDateFormat.parse(sale.date))
                            } catch (ex: ParseException) {
                                ""
                            }

                            saleStatusCode = sale.statusCode ?: CANCELADA
                            cardBrandUrl = sale.cardBrandCode.toString()
                            saleValue = sale.grossAmount?.toPtBrRealString()

                            saleDate!!
                        } else {
                            saleStatusCode = sale.statusCode ?: ExtratoStatusDef.APROVADA
                            cardBrandUrl = sale.cardBrand

                            saleValue = sale.amount?.toPtBrRealString()

                            sale.date?.substring(11, 16)
                        }

                        //TODO: Criar um parse com SimpleDateFormat("yyyy-MM-dd")
                        holder.mView.tvTime?.text = SpannableStringBuilder.valueOf(saleTime)
                        holder.mView.tvValue?.text = SpannableStringBuilder.valueOf(saleValue)
                        holder.mView.tvPaymentType?.text = sale.paymentType
                        holder.mView.tvStatus?.text =
                            sale.status?.toLowerCasePTBR()?.capitalizePTBR()

                        sale.cardBrand?.let { _ ->
                            BrandCardHelper.getUrlBrandImageByCode(cardBrandUrl!!)
                                ?.let { itUrl ->
                                    ImageUtils.loadImage(
                                        holder.mView.ivBrandType,
                                        itUrl,
                                        R.drawable.ic_generic_brand
                                    )
                                    holder.mView.ivBrandType.contentDescription = cardBrandUrl
                                }
                                ?: holder.mView.ivBrandType?.setImageResource(R.drawable.ic_generic_brand)
                        }


                        MySalesStatusHelper.setSalesStatus(
                            requireContext(),
                            saleStatusCode,
                            null,
                            holder.mView.tvStatus
                        )
                        holder.mView.setOnClickListener {

                            presenter.showSaleDetail(sale)
                        }
                    }

                })

                binding?.recyclerView?.adapter = this.adapter
            } else {
                try {
                    adapter?.setNewDataSet(list)
                    binding?.recyclerView?.post {
                        scrollControlledLinearManager?.setIsCanScroll(true)
                    }
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(ex)
                }
            }
        }
    }

    override fun showError(error: ErrorMessage?, isCanceledSale: Boolean) {
        error?.let {
            if (isCanceledSale) showEmptyCanceledSales()
            else showEmptyResult()

            binding?.apply {
                recyclerView.gone()
                waitingLayout.gone()
            }

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
            exceptionGA4(error)
        }
    }

    override fun showCanceledSales(
        sales: ArrayList<CanceledSale>,
        summary: Summary,
    ) {
        binding?.apply {
            footerLayout.gone()
            containerSummaryCanceled.root.visible()
            containerSummaryCanceled.apply {
                groupingQuantityCanceled.text = summary.totalQuantity?.toString()
                groupingValueCanceled.text = summary.totalAmount?.toPtBrRealString()
            }
            canceledSalesAdapter.setCanceledSales(sales)
            recyclerView.adapter = canceledSalesAdapter
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (recyclerView.canScrollVertically(ONE)
                            .not() && newState == RecyclerView.SCROLL_STATE_IDLE
                    ) {
                        presenter.loadMoreCanceledSales()
                    }
                }
            })
            recyclerView.post {
                scrollControlledLinearManager?.setIsCanScroll(true)
            }
        }
    }

    override fun showMoreSales(sales: List<Sale>) { 
        val items = sales.mapIndexed { index, sale -> Item(sale, index) }
        binding?.apply {
            salesSummaryFooter.root.visible()
            waitingAddMoreItensLayout.gone()
            if (sales.isEmpty()) {
                adapter?.setEndOfTheList(true)
            } else {
                recyclerView.recycledViewPool.clear()
                recyclerView.stopScroll()
                adapter?.addMoreInList(items)
            }
        }
    }

    override fun showMoreCanceledSales(sales: List<CanceledSale>) {
        binding?.waitingAddMoreItensLayout?.gone()
        canceledSalesAdapter.addCanceledSales(sales)
    }

    override fun showLoadingMoreItems() { 
        binding?.apply {
            waitingAddMoreItensLayout.visible()
            salesSummaryFooter.root.gone()
        }
    }

    override fun showSummary(summary: Summary, saleStatus: Int) { 
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

    override fun showEmptyResult(isByRefreshing: Boolean) { 
        binding?.apply {
            errorLayout.root.visible()
            containerSummaryCanceled.root.gone()
        }
    }

    override fun showEmptyCanceledSales() { 
        binding?.apply {
            emptyCanceledSales.root.visible()
            containerSummaryCanceled.root.gone()
        }
    }

    override fun showFullsecError() { 
        binding?.errorLayout?.root.visible()
    }

    override fun showSaleDetail(sale: Sale) { 
        val intent = MinhasVendasDetalhesActvity.newIntent(requireContext(), sale)
        intent.putExtra(IS_SALE_TODAY_ARGS, true)
        startActivity(intent)
    }

    override fun showCanceledSaleDetail(canceledSale: CanceledSale) {
        val startSaleDetailsIntent = Intent(
            requireContext(),
            CanceledDetailsActivity::class.java
        )
        startSaleDetailsIntent.putExtra(CanceledDetailsActivity.CANCELED_SALE_ARGS, canceledSale)
        startActivity(startSaleDetailsIntent)
    }

    private fun configureRecyclerView() {  
        scrollControlledLinearManager = ScrollControlledLinearManager(requireContext())
        binding?.apply {
            recyclerView.layoutManager = scrollControlledLinearManager
            recyclerView.itemAnimator = null
        }
    }

    private fun configureSwipeRefreshLayout() { 
        binding?.swipeRefreshLayout?.setOnRefreshListener {
            scrollControlledLinearManager?.setIsCanScroll(false)
            presenter.refresh()
        }
    }

    private fun configureListeners() {
        binding?.errorLayout?.apply {
            buttonRefreshTransactions.setOnClickListener {
                presenter.retry()
                gaSendButtonCancelAtualizar("Atualizar")
            }
        }
    }

    private fun isCanceledSells(): Boolean = (arguments?.getSerializable(ARG_PARAM_QUICK_FILTER) as
            QuickFilter?)?.status?.contains(CANCELADA) ?: false 

    private fun gaSendButtonCancelAtualizar(name: String) { 
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
                action = listOf(Action.CARTOES),
                label = listOf(Label.BOTAO, name)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_NAME_SALES_MADE)
    }

    private fun exceptionGA4(error: ErrorMessage?){
        ga4.logException(SCREEN_NAME_SALES_MADE, errorMessage = error)
    }
}