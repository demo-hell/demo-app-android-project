package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity.Companion.NEGOTIATIONS_ITEMS_ARGS
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasItems
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasUseCase
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.adapter.ExtratoRecebiveisVendasUnitariasAdapter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter.ExtratoRecebiveisVendasUnitariasPresenterImpl
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EndlessScrollListener
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.constraintLayoutContent
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.empityFilter
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.errorView
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.progressBarPaging
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.progressBarStart
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.recyclerView
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.textViewNegotiatesValue
import kotlinx.android.synthetic.main.fragment_extrato_recebiveis_vendas_unitarias.textViewTotalNegotiated
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ExtratoRecebiveisVendasUnitariasFragment : BaseFragment(), CieloNavigationListener,
    ExtratoRecebiveisVendasUnitariasView {

    private val negotiationItem: Item? by lazy { arguments?.getParcelable(NEGOTIATIONS_ITEMS_ARGS) }
    private var actionsMenu: Menu? = null
    private var quickFilter: QuickFilter? = null
    private val presenter: ExtratoRecebiveisVendasUnitariasPresenterImpl by inject {
        parametersOf(this)
    }
    private val analytics: ReceivablesAnalyticsGA4 by inject()
    private lateinit var adapter: ExtratoRecebiveisVendasUnitariasAdapter
    private lateinit var scrollListener: EndlessScrollListener
    private val extractUnitList: MutableList<ExtratoRecebiveisVendasUnitariasItems> = ArrayList()
    private val layoutManager: LinearLayoutManager?
            by lazy { LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) }
    private var cieloNavigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_extrato_recebiveis_vendas_unitarias, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        configureNavigation()
        init()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume(negotiationItem?.operationSourceCode)
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    fun init() {
        negotiationItem?.let { item ->
            presenter.onCreate(item, null)
            textViewTotalNegotiated.setOnClickListener {
                RecivableDetailBottomSheetFragment
                    .newInstance(requireActivity().supportFragmentManager, item)
            }
        }

        scrollListener = object : EndlessScrollListener(layoutManager!!) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                presenter.getUnitReceivable(page + 1, true)
            }
        }
        adapter = ExtratoRecebiveisVendasUnitariasAdapter(extractUnitList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(scrollListener)
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.title_balcao_unit_sales))
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_common_filter_faq, menu)
        actionsMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_help_extrato -> {
                Router.navigateTo(requireContext(), br.com.mobicare.cielo.main.domain.Menu(
                    Router.APP_ANDROID_HELP_CENTER, "", listOf(),
                    getString(R.string.text_body_dirf_02), false, "",
                    listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                        false,
                        type = "", mail = "", url = ""
                    )
                ), object : Router.OnRouterActionListener {

                    override fun actionNotFound(action: br.com.mobicare.cielo.main.domain.Menu) {
                        FirebaseCrashlytics
                            .getInstance()
                            .recordException(Throwable("Acao não encontrada || action_help_extrato"))
                    }

                })
                return true
            }

            R.id.action_filter -> {

                actionsMenu?.findItem(R.id.action_filter)
                    ?.isEnabled = false
                Handler().postDelayed({
                    actionsMenu?.findItem(R.id.action_filter)
                        ?.isEnabled = true
                }, 1000)

                negotiationItem?.let {
                    ExtratoRecebiveisVendasUnitariasFilterBottomSheet.newInstance(it, quickFilter)
                        .apply {
                            this.onClick = object :
                                ExtratoRecebiveisVendasUnitariasFilterBottomSheet.OnClickButtons {
                                override fun onBtnAddFilter(
                                    dialog: Dialog,
                                    quickFilter: QuickFilter?
                                ) {
                                    dialog.dismiss()
                                    this@ExtratoRecebiveisVendasUnitariasFragment.quickFilter =
                                        quickFilter
                                    resetState()
                                    presenter.onCreate(
                                        it,
                                        this@ExtratoRecebiveisVendasUnitariasFragment.quickFilter
                                    )
                                    actionsMenu?.findItem(R.id.action_filter)
                                        ?.icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_filter_filled
                                    )
                                }

                                override fun onBtnCleanFilter(
                                    dialog: Dialog,
                                    quickFilter: QuickFilter?
                                ) {
                                    this@ExtratoRecebiveisVendasUnitariasFragment.quickFilter = null
                                    resetState()
                                    presenter.onCreate(it, null)
                                    actionsMenu?.findItem(R.id.action_filter)
                                        ?.icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_filter
                                    )
                                    dialog.dismiss()
                                }
                            }
                        }.show(
                            requireActivity().supportFragmentManager,
                            "ExtratoRecebiveisVendasUnitariasFilterBottomSheet"
                        )
                }
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSuccess(response: ExtratoRecebiveisVendasUnitariasUseCase) {
        constraintLayoutContent.visible()

        if (response.items.isNullOrEmpty().not()) {
            empityFilter.gone()
            textViewNegotiatesValue.text = response.summary.totalNetAmount.toPtBrRealString()
            extractUnitList.addAll(response.items)
            adapter.notifyDataSetChanged()
        } else if (response.summary.totalAmount == 0.0) {
            empityFilter.visible()
            textViewNegotiatesValue.text = response.summary.totalAmount.toPtBrRealString()
        }
    }

    override fun onError(error: ErrorMessage) {
        empityFilter.gone()
        errorView?.visible()
        errorView?.configureButtonLabel(getString(R.string.text_try_again_label))
        errorView?.cieloErrorTitle = getString(R.string.text_title_service_unavailable)
        errorView?.errorHandlerCieloViewImageDrawable = R.drawable.img_ineligible_user
        errorView?.configureActionClickListener {
            negotiationItem?.let {
                presenter.onCreate(it, quickFilter)
            }
        }
        presenter.trackException(
            negotiationType = negotiationItem?.operationSourceCode,
            error = error
        )
        resetState()
    }

    private fun resetState() {
        extractUnitList.clear()
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager?.scrollToPosition(0)
        scrollListener.resetState()
    }

    override fun showLoading() {
        constraintLayoutContent?.gone()
        errorView?.gone()
        progressBarStart?.visible()
    }

    override fun hideLoading() {
        progressBarStart?.gone()
    }

    override fun showLoadingMore() {
        constraintLayoutContent?.gone()
        errorView?.gone()
        progressBarPaging?.visible()
    }

    override fun hideLoadingMore() {
        progressBarPaging?.gone()
    }

    override fun logException(screenName: String, error: NewErrorMessage) {
        analytics.logException(screenName, error)
    }

    override fun logScreenView(screen: String) {
        analytics.logScreenView(
            screenName = screen,
        )
    }
}