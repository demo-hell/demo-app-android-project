package br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity.Companion.FINAL_DATE_ARGS
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity.Companion.INITIAL_DATE_ARGS
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity.Companion.TYPE_NEGOCIATION
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.CIELO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.MERCADO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoPresenter
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.fragment_balcao_recebiveis_extrato_details_negotiations.*
import kotlinx.android.synthetic.main.item_balcao_recebiveis_extrato_details_negotiations.view.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * create by Enzo Teles
 * Jan 22, 2021
 * */
class BalcaoRecebiveisExtratoDetailsNegotiationsFragment : BaseFragment(),
    BalcaoRecebiveisExtratoContract.View,
    CieloNavigationListener {

    companion object {
        const val QUICKFILTER = "QUICKFILTER"
    }

    private var cieloNavigation: CieloNavigation? = null
    private val dateInit: String? by lazy { arguments?.getString(INITIAL_DATE_ARGS) }
    private val dateEnd: String? by lazy { arguments?.getString(FINAL_DATE_ARGS) }
    private val typeNegociation: String? by lazy { arguments?.getString(TYPE_NEGOCIATION) }
    private var actionsMenu: Menu? = null
    private var quickFilter: QuickFilter? = null
    private lateinit var adapter: DefaultViewListAdapter<Item>
    private val negociationItems: MutableList<Item> = ArrayList()
    private lateinit var scrollListener: EndlessScrollListener
    private val presenter: BalcaoRecebiveisExtratoPresenter by inject {
        parametersOf(this)
    }
    private val analytics: ReceivablesAnalyticsGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.negociationItems.clear()
        return inflater.inflate(
            R.layout.fragment_balcao_recebiveis_extrato_details_negotiations,
            container,
            false
        )
    }

    override fun onResume() {
        super.onResume()
        presenter.trackScreenViewEvent(typeNegociation)
    }

    override fun logScreenView(screenName: String) {
        analytics.logScreenView(
            screenName = screenName,
        )
    }

    override fun logException(screenName: String, error: NewErrorMessage) {
        analytics.logException(
            screenName = screenName,
            error = error
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        rangeDateView.text =
            "${dateInit?.convertTimeStampToDate()}-${dateEnd?.convertTimeStampToDate()}"
        initView()
    }

    /**
     * method to init views.
     * */
    override fun initView() {
        configureNavigation()
        presenter.callExtratoDetail(1, dateInit, dateEnd, typeNegociation, quickFilter)
        errorHandlerUrl.configureActionClickListener(View.OnClickListener {
            requireActivity().onBackPressed()
        })

        val llm = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        scrollListener = object : EndlessScrollListener(llm) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (recyclerView.canScrollVertically(ONE))
                    presenter.callExtratoDetail(
                        page + 1, dateInit, dateEnd, typeNegociation,
                        quickFilter, true
                    )
            }
        }

        adapter = DefaultViewListAdapter(
            negociationItems,
            R.layout.item_balcao_recebiveis_extrato_details_negotiations
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = llm
        recyclerView.addOnScrollListener(scrollListener)

        adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<Item> {
            override fun onBind(item: Item, holder: DefaultViewHolderKotlin) {
                holder.mView.tv_details_date.text = item.date?.convertTimeStampToDate()
                holder.mView.tv_details_cod.text = item.operationNumber.toString()
                holder.mView.tv_details_value.text = Utils.formatValue(item.netAmount)

                if (item.identificationNumber?.length!! == 11) {
                    holder.mView.tv_details_cnpj.text = addMaskCPForCNPJ(
                        item.identificationNumber,
                        getString(R.string.mask_cpf_step4)
                    )
                } else if (item.identificationNumber?.length!! > 11) {
                    holder.mView.tv_details_cnpj.text = addMaskCPForCNPJ(
                        item.identificationNumber,
                        getString(R.string.mask_cnpj_step4)
                    )
                } else if (item.identificationNumber?.length!! < 11) {
                    holder.mView.tv_details_cnpj.text = item.identificationNumber
                }

                holder.mView.linearLayoutContent.setOnClickListener {
                    val direction = BalcaoRecebiveisExtratoDetailsNegotiationsFragmentDirections
                        .actionBalcaoRecebiveisExtratoFragmentToExtratoRecebiveisVendasUnitariasFragment(
                            item
                        )
                    dateInit?.let { direction.setINITIALDATEARGS(it) }
                    dateEnd?.let { direction.setFINALDATEARGS(it) }
                    findNavController().navigate(direction)
                }
            }
        })
    }

    /**
     * method to populate recyclerview
     * */
    private fun populationRecyclerView(negotiations: Negotiations) {
        negotiations.items?.toMutableList()?.let {
            negociationItems.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    override fun initProgress() {
        displayedChild(0, vf_extrato)
    }

    override fun finishedProgress() {
    }

    override fun showProgressMore() {
        displayedChild(1, vf_extrato)
        progressBarLoadingMore.visible()
    }

    override fun finishedProgressMore() {
        progressBarLoadingMore.gone()
    }

    override fun showSuccess(negotiations: Negotiations) {
        displayedChild(1, vf_extrato)
        totalExtratoDetails.text = Utils.formatValue(negotiations.summary?.totalNetAmount)
        populationRecyclerView(negotiations)
        tvAmountEmpty.visibility = if (negociationItems.size != 0) View.GONE else View.VISIBLE
        recyclerView.visibility = if (negociationItems.size != 0) View.VISIBLE else View.GONE
    }

    override fun serverError() {
        displayedChild(2, vf_extrato)
        resetList()
    }

    /**
     * método configurar o estilos do fragmento via navigation listener
     * */
    private fun configureNavigation() {

        var name = if (typeNegociation == CIELO) CIELO else MERCADO

        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(
                getString(
                    R.string.title_tela_details_negociations,
                    name
                )
            )
            this.cieloNavigation?.showButton(false)
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
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
                    Router.APP_ANDROID_HELP_CENTER, EMPTY, listOf(),
                    getString(R.string.text_body_dirf_02), false, EMPTY,
                    listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                        false,
                        type = EMPTY, mail = EMPTY, url = EMPTY
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

                val ftsucessBS =
                    BalcaoRecebiveisExtratoDetailsNegotiationsBottomSheet.newInstance(quickFilter)
                        .apply {
                            this.onClick = object :
                                BalcaoRecebiveisExtratoDetailsNegotiationsBottomSheet.OnClickButtons {
                                override fun onBtnAddFilter(
                                    dialog: Dialog?,
                                    quickFilter: QuickFilter?
                                ) {
                                    dialog?.dismiss()
                                    resetList()
                                    presenter.callExtratoDetail(
                                        1,
                                        dateInit,
                                        dateEnd,
                                        typeNegociation,
                                        quickFilter
                                    )
                                    this@BalcaoRecebiveisExtratoDetailsNegotiationsFragment.quickFilter =
                                        quickFilter
                                    if (quickFilter?.operationNumber != null || quickFilter?.initialAmount != null || quickFilter?.finalAmount != null || quickFilter?.merchantId != null) {
                                        actionsMenu?.findItem(R.id.action_filter)
                                            ?.icon = ContextCompat.getDrawable(
                                            requireContext(),
                                            R.drawable.ic_filter_filled
                                        )
                                    }
                                }

                                override fun onBtnCleanFilter(
                                    dialog: Dialog?,
                                    quickFilter: QuickFilter?
                                ) {
                                    dialog?.dismiss()
                                    this@BalcaoRecebiveisExtratoDetailsNegotiationsFragment.quickFilter =
                                        quickFilter
                                    resetList()
                                    presenter.callExtratoDetail(
                                        1,
                                        dateInit,
                                        dateEnd,
                                        typeNegociation
                                    )
                                    actionsMenu?.findItem(R.id.action_filter)
                                        ?.icon = ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.ic_filter
                                    )
                                }
                            }

                        }
                ftsucessBS.show(
                    requireActivity().supportFragmentManager,
                    "BalcaoRecebíveisExtratoDetailsNegotiationsBottomSheet"
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun resetList() {
        negociationItems.clear()
        recyclerView.layoutManager?.scrollToPosition(0)
        adapter.notifyDataSetChanged()
        scrollListener.resetState()
    }
}