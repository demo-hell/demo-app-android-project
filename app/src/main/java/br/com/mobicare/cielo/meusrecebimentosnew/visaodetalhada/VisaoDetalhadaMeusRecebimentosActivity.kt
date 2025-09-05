package br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_CODE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUMMARY_ITEM
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_TITLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.analytics.formatTextForGA4
import br.com.mobicare.cielo.databinding.ActivityVisaoDetalhadaMeusRecebimentosBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.DetailViewOfMyReceiptsAdapter
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder.DetailViewOfMyReceiptsViewHolder
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4.Companion.SCREEN_NAME_RECEIVABLES_DETAIL_LIST
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4.Companion.SCREEN_NAME_RECEIVABLES_DETAIL_MORE_INFOS
import br.com.mobicare.cielo.meusrecebimentosnew.enums.MeusRecebimentosCodigosEnum
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.FilterVisaoDetalhadaContact
import br.com.mobicare.cielo.meusrecebimentosnew.fragments.FilterVisaoDetalhadaMeusRecebiveisBottomSheetFragment
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_QUICKFILTER
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val MEUS_RECEBIMENTOS_FILTER = "VisaoDetalhadaMeusRecebimentosActivity"

class VisaoDetalhadaMeusRecebimentosActivity : BaseLoggedActivity(), VisaoDetalhadaMeusRecebimentosContract.View {

    private var _binding: ActivityVisaoDetalhadaMeusRecebimentosBinding? = null
    private val binding get() = _binding

    private val presenter: VisaoDetalhadaMeusRecebimentosPresenter by inject {
        parametersOf(this)
    }

    private var adapter: DetailViewOfMyReceiptsAdapter? = null
    private val ga4: MyReceivablesGA4 by inject()
    private var isShowHelpButton = true
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null
    private var filter: QuickFilter? = null
    private var menu: Menu? = null
    private var code: Int? = null
    private var items: Item? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVisaoDetalhadaMeusRecebimentosBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        loadFilterIntent()
        configureErrorEmpty()
        configureRecyclerView()
        configureSwipeRefreshLayout()
        load()

    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onDestroy() {
        this.presenter.onDestroy()
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.menu_extrato, menu)
        menu.findItem(R.id.actionFilterBrandsAndPaymentMethods).isVisible = true
        menu.findItem(R.id.action_extrato_help).isVisible = isShowHelpButton

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_extrato_help -> {
                this.presenter.helpButtonClicked(items)
                return true
            }
            R.id.actionFilterBrandsAndPaymentMethods -> {
                showFilter()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun configureErrorEmpty() {
        binding?.emptyResultHandler?.configureButtonVisible(false)

    }

    private fun loadFilterIntent() {
        intent.extras?.let {
            it.getSerializable(PARAM_QUICKFILTER)?.let { itParam ->
                filter = itParam as QuickFilter
            }

            it.getParcelable<Item>(ARG_PARAM_SUMMARY_ITEM)?.let { itItem ->
                items = itItem
                it.getString(ARG_PARAM_TITLE)?.let { itTitle ->
                    configureToolbar(itTitle)
                } ?: configureToolbar(items?.transactionType)
            }

            it.getInt(ARG_PARAM_CODE).let { itCode ->
                code = itCode
                if (MeusRecebimentosCodigosEnum.valueOf(itCode) != MeusRecebimentosCodigosEnum.VALORES_PENDENTES) {
                    hideHelpButton()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        flowFormated(items?.transactionType)
    }

    private fun load(){
        presenter.load(customQuickFilter = filter, item = items, code = code)
    }

    private fun configureRecyclerView() {
        scrollControlledLinearManager = ScrollControlledLinearManager(this)
        _binding?.recyclerView?.layoutManager = this.scrollControlledLinearManager
        _binding?.recyclerView?.itemAnimator = null
    }

    private fun configureSwipeRefreshLayout() {
        if (isAttached()) {
            binding?.swipeRefreshLayoutView?.setOnRefreshListener {
                this.scrollControlledLinearManager?.setIsCanScroll(false)
                load()
            }
        }
    }

    private fun showFilter() {
        FilterVisaoDetalhadaMeusRecebiveisBottomSheetFragment.create(filter, object : FilterVisaoDetalhadaContact {
            override fun onCleanFilter(quickFilter: QuickFilter) {
                setIconFilter(false)
                requestFilter(quickFilter)
            }

            override fun onFilterSelected(quickFilter: QuickFilter) {
                setIconFilter(true)
                requestFilter(quickFilter)
            }
        }).show(supportFragmentManager, MEUS_RECEBIMENTOS_FILTER)
    }

    private fun requestFilter(quickFilter: QuickFilter) {
        filter = quickFilter
        this@VisaoDetalhadaMeusRecebimentosActivity.presenter.load(customQuickFilter = quickFilter, item = items, code = code)
    }

    private fun setIconFilter(isFilter: Boolean) {
        if (isFilter)
            menu?.findItem(R.id.actionFilterBrandsAndPaymentMethods)
                    ?.icon = ContextCompat.getDrawable(this,
                    R.drawable.ic_filter_filled)
        else
            menu?.findItem(R.id.actionFilterBrandsAndPaymentMethods)
                    ?.icon = ContextCompat.getDrawable(this,
                    R.drawable.ic_filter)
    }

    override fun showLoading() {
        binding?.errorView?.gone()
        binding?.progressView?.visible()
        binding?.moreProgresView?.root.gone()
        binding?.contentLayout?.gone()
    }

    override fun hideLoading() {
        binding?.progressView?.gone()
    }

    override fun hideRefreshLoading() {
        binding?.swipeRefreshLayoutView?.isRefreshing = false
    }

    override fun hideMoreLoading() {
        binding?.moreProgresView?.root.gone()
    }

    override fun hideHelpButton() {
        this.isShowHelpButton = false
    }

    private fun configureToolbar(name: String?) {
        name?.let {
            setupToolbar(binding?.toolbar?.root as Toolbar, toolbarTitle = name)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
        )
    }

    override fun showError(errorMessage: ErrorMessage) {
        binding?.errorView?.visible()
        exceptionFormated(
            SCREEN_NAME_RECEIVABLES_DETAIL_LIST,
            items?.transactionType,
            errorMessage
        )
        binding?.errorView?.configureActionClickListener {
            load()
        }
        binding?.progressView?.gone()
        binding?.moreProgresView?.root.gone()
        binding?.contentLayout?.gone()
    }

    override fun showBottom(valor: String, idColorRes: Int) {
        val resumoOperacoesBinding = binding?.bottomLayout
        resumoOperacoesBinding?.resumoOperacaoTotalLancamentos?.text = "Valor líquido total:"
        resumoOperacoesBinding?.meusRecebimentosDetalheDetalheValor?.text = valor
        resumoOperacoesBinding?.meusRecebimentosDetalheDetalheValor?.setTextColor(
            ContextCompat.getColor(
                this,
                idColorRes
            )
        )
    }

    override fun showHeader(brand: String, product: String) {
        binding?.headerLayout.visible()
        binding?.resumoOperacoesDetalheBandeira?.text = brand
        binding?.resumoOperacoesDetalheProduto?.text = product
    }

    override fun showItens(code: Int, itens: ArrayList<Receivable>) {
        binding?.errorView?.gone()
        binding?.progressView?.gone()
        binding?.moreProgresView?.root.gone()
        binding?.emptyResultHandler?.gone()
        binding?.contentLayout?.visible()

        if (this.adapter == null) {
            adapter = DetailViewOfMyReceiptsAdapter(itens)
            this.adapter?.setBindViewHolderCallback(object :
                DetailViewOfMyReceiptsAdapter.OnBindViewHolder<Receivable> {
                override fun bind(item: Receivable, holder: DetailViewOfMyReceiptsViewHolder) {
                    holder.bind(code, item, this@VisaoDetalhadaMeusRecebimentosActivity)
                }
            })
            binding?.recyclerView?.adapter = this.adapter
        } else {
            this.adapter?.updateDataSet(itens)
            Handler().postDelayed({
                this.scrollControlledLinearManager?.setIsCanScroll(true)
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
            }, 500)
        }
    }

    override fun showMoreItems(code: Int, itens: ArrayList<Receivable>, isFinish: Boolean) {
        binding?.moreProgresView?.root.gone()
        binding?.recyclerView?.recycledViewPool?.clear()
        binding?.recyclerView?.stopScroll()
        this.adapter?.setEndOfTheList(isFinish)
        this.adapter?.addMoreInList(itens)
    }

    override fun showHelpPopup(code: Int, title: String, message: String) {
        AlertDialogCustom.Builder(this, getString(R.string.meus_recebimentos_detalhe))
            .setTitle(title)
            .setMessage(message)
            .setBtnRight(getString(android.R.string.ok))
            .show()
    }

    override fun showErrorEmptyResult() {
        binding?.contentLayout.gone()
        binding?.emptyResultHandler.visible()
    }

    private fun flowFormated(flow: String?) {
        flow?.let { flow ->
            ga4.logScreenView(
                SCREEN_NAME_RECEIVABLES_DETAIL_MORE_INFOS,
                formatTextForGA4(flow)
            )
        }
    }

    private fun exceptionFormated(screenName: String, flow: String?, error: ErrorMessage?) {
        flow?.let { flow -> ga4.logException(screenName, formatTextForGA4(flow), error) }
    }
}