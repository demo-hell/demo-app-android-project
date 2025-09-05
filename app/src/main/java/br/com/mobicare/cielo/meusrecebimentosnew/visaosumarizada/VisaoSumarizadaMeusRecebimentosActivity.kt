package br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.fragment.FilterBottomSheetFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.analytics.formatTextForGA4
import br.com.mobicare.cielo.databinding.VisaoSumarizadaMeusRecebimentosActivityBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers.Codes
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.helpers.MeusRecebimentosHelper
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.SummaryViewMyReceiptsAdapter
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder.SummaryViewMyReceiptsViewHolder
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4.Companion.SCREEN_NAME_RECEIVABLES
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4.Companion.SCREEN_NAME_RECEIVABLES_DETAIL_LIST
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.common.ScrollControlledLinearManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


val PARAM_OBJECT: String = "EXTRA_OBJECT"
val PARAM_QUICKFILTER: String = "PARAM_QUICKFILTER"

class VisaoSumarizadaMeusRecebimentosActivity : BaseLoggedActivity(),
        VisaoSumarizadaMeusRecebimentosContract.View,
        FilterBottomSheetFragment.OnFilterActionListener {

    private val presenter: VisaoSumarizadaMeusRecebimentosPresenter by inject {
        parametersOf(this)
    }

    private var newAdapter: SummaryViewMyReceiptsAdapter? = null
    private var title: String? = null
    private var scrollControlledLinearManager: ScrollControlledLinearManager? = null

    private var actionsMenu: Menu? = null

    private var selectedQuickFilter: QuickFilter? by serialState()
    private var filterOpened: Boolean = false
    private val ga4: MyReceivablesGA4 by inject()
    private var _binding: VisaoSumarizadaMeusRecebimentosActivityBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = VisaoSumarizadaMeusRecebimentosActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        configureRecyclerView()
        configureSwipeRefreshLayout()
        this.presenter.onStart()
        unfreezeInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        freezeInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        selectedQuickFilter?.let {
            this.presenter.loadSummaryView(1, customQuickFilter = it)
        } ?: this.presenter.load(this.intent.extras)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.presenter.onDestroy()
        _binding = null
    }

    override fun configureToolbar(name: String) {
        this.title = name
        setupToolbar(binding?.toolbarMeusRecebimentosVisaoSumarizada?.root as Toolbar, toolbarTitle = name)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
    }

    override fun onResume() {
        super.onResume()
       flowFormated(title)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        actionsMenu = menu
        menuInflater.inflate(R.menu.menu_extrato, menu)

        val helpItem = menu.findItem(R.id.action_extrato_help)
        helpItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_screen_info_flui)
        helpItem.isVisible = false

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionFilterBrandsAndPaymentMethods -> {
                if (!filterOpened) {
                    filterOpened = true
                    presenter.filterReceivables(selectedQuickFilter)
                }
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun showHelpPopup(code: Int, title: String, message: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TAPICON),
            action = listOf("${MeusRecebimentosHelper.formatedScreenName()}/${MeusRecebimentosHelper.getScreenName(code)}"),
            label = listOf("Dúvida ${MeusRecebimentosHelper.getScreenNameWithSpaces(code)}")
        )
        AlertDialogCustom.Builder(this, getString(R.string.meus_recebimentos_detalhe))
                .setTitle(title)
                .setMessage(message)
                .setBtnRight(getString(android.R.string.ok))
                .show()
    }

    override fun showSubHeader(startDate: String, finalDate: String) {
        var text = "$startDate"
        if (startDate != finalDate) {
            text += " - $finalDate"
        }
        binding?.rangeDateView?.text = text
    }

    override fun hideLoading() {
        binding?.progressView?.visibility = View.GONE
    }

    override fun showLoading() {
        binding?.errorView?.visibility = View.GONE
        binding?.headerLayout?.visibility = View.GONE
        binding?.bottomLayout?.visibility = View.GONE
        binding?.progressView?.visibility = View.VISIBLE
        binding?.swipeRefreshLayout?.visibility = View.GONE
        binding?.moreProgresView?.root.gone()
    }

    override fun showError(errorMessage: ErrorMessage) {
        binding?.errorView?.visibility = View.VISIBLE
        binding?.headerLayout?.visibility = View.GONE
        binding?.bottomLayout?.visibility = View.GONE
        binding?.progressView?.visibility = View.GONE
        binding?.swipeRefreshLayout?.visibility = View.GONE
        exceptionFormated(SCREEN_NAME_RECEIVABLES, EMPTY, errorMessage)
    }

    override fun showItems(
        code: Int,
        itens: ArrayList<Item>,
        isAnimationEnabled: Boolean,
        quickFilter: QuickFilter?
    ) {
        binding?.errorView?.visibility = View.GONE
        binding?.headerLayout?.visibility = View.VISIBLE
        binding?.bottomLayout?.visibility = View.VISIBLE
        binding?.progressView?.visibility = View.GONE
        binding?.swipeRefreshLayout?.visibility = View.VISIBLE
        binding?.moreProgresView?.root?.visibility = View.GONE

        if (this.newAdapter == null && itens.isNotEmpty()) {
            binding?.tvErrorFilter.gone()
            binding?.recyclerView?.visible()
            this.newAdapter = SummaryViewMyReceiptsAdapter(itens)
            Handler().post {
                binding?.bottomLayout?.measuredHeight?.let {
                    this.newAdapter?.setDistanceToHide(it)
                }
            }
            this.newAdapter?.setBindViewHolderCallback(object  : SummaryViewMyReceiptsAdapter.OnBindViewHolder<Item> {
                override fun bind(items: Item, holder: SummaryViewMyReceiptsViewHolder) {
                    holder.bind(code, items, title, quickFilter, this@VisaoSumarizadaMeusRecebimentosActivity )
                }
            })
            binding?.recyclerView?.adapter = this.newAdapter
        }else if (itens.size == ZERO) {
            binding?.tvErrorFilter?.visible()
            binding?.recyclerView?.gone()
        } else {
            try {
                binding?.tvErrorFilter?.gone()
                binding?.recyclerView?.visible()
                newAdapter?.setNewDataList(itens)
                binding?.recyclerView?.post {
                    scrollControlledLinearManager?.setIsCanScroll(true)
                    binding?.recyclerView?.adapter?.notifyDataSetChanged()
                }
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().recordException(ex)
            }
        }
    }

    override fun showMoreItems(code: Int, itens: ArrayList<Item>, isFinish: Boolean) {
        binding?.moreProgresView?.root?.visibility = View.GONE
        binding?.recyclerView?.recycledViewPool?.clear()
        binding?.recyclerView?.stopScroll()
        this.newAdapter?.setEndOfTheList(isFinish)
        this.newAdapter?.addMoreInList(itens)
    }

    override fun enableFilterOnMenu(code: Int) {
        actionsMenu?.let {
            val filterItem = it.findItem(R.id.actionFilterBrandsAndPaymentMethods)
            when (code) {
                Codes.VENDA_CREDITO, Codes.DEBITO, Codes.PARCELADO,
                Codes.CANCELAMENTO_VENDA, Codes.REVERSAO_CANCELAMENTO,
                Codes.CHARGEBACK, Codes.REVERSAO_CHARGEBACK -> filterItem.isVisible = true
                else -> filterItem.isVisible = false
            }
        }
    }

    override fun showFilters(quickFilter: QuickFilter) {
        FilterBottomSheetFragment.create(
            getString(R.string.text_receivables_filter_label),
            getString(R.string.text_receivables_filter_description), quickFilter
        )
            .apply {
                onFilterActionListener = this@VisaoSumarizadaMeusRecebimentosActivity
            }
            .show(supportFragmentManager, FilterBottomSheetFragment::class.java.simpleName)
    }

    override fun disableFilterOnMenu() {
        actionsMenu?.let {
            val filterItem = it.findItem(R.id.actionFilterBrandsAndPaymentMethods)
            filterItem.isVisible = false
        }
    }

    override fun showBottom(value: String, color: Int) {
        binding?.totalNetValueView?.text = value
        binding?.totalNetValueView?.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun hideRefreshLoading() {
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    override fun hideMoreLoading() {
        this@VisaoSumarizadaMeusRecebimentosActivity.binding?.moreProgresView?.root?.visibility = View.GONE
    }

    private fun configureRecyclerView() {
        this.scrollControlledLinearManager = ScrollControlledLinearManager(this)
        binding?.recyclerView?.layoutManager = this.scrollControlledLinearManager
        binding?.recyclerView?.itemAnimator = null
        binding?.recyclerView?.setHasFixedSize(false)
    }

    private fun configureSwipeRefreshLayout() {
        if (isAttached()) {
            binding?.swipeRefreshLayout?.setOnRefreshListener {
                this.scrollControlledLinearManager?.setIsCanScroll(false)
                selectedQuickFilter?.let {
                    this.presenter.loadSummaryView(1, customQuickFilter = it)
                } ?: this.presenter.loadSummaryView(1)
            }
        }
    }

    override fun onCleanFilter() {
        filterOpened = false
        selectedQuickFilter = null
        onStart()
    }

    override fun onFilterSelected(quickFilter: QuickFilter) {
        filterOpened = false
        actionsMenu?.findItem(R.id.actionFilterBrandsAndPaymentMethods)
            ?.icon = ContextCompat.getDrawable(
            this,
            R.drawable.ic_filter_filled
        )

        selectedQuickFilter = quickFilter
        presenter.loadSummaryView(1, customQuickFilter = selectedQuickFilter)
    }

    override fun onDismiss() {
        filterOpened = false
    }

    fun flowFormated(flow: String?){
        flow?.let { flow -> ga4.logScreenView(SCREEN_NAME_RECEIVABLES_DETAIL_LIST, formatTextForGA4(flow) ) }
    }

    private fun exceptionFormated(screenName: String,  flow: String?, error: ErrorMessage?){
        flow?.let { flow -> ga4.logException(screenName, formatTextForGA4(flow), error) }
    }
}