package br.com.mobicare.cielo.extrato.presentation.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.presentation.ChargebackNavigationFlowActivity
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.SelectItemBottomSheet
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.model.RowSelectItemModel
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.fragment.components.filters.date.ComponentDateFilterFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extrato.presentation.presenter.ExtratoPresenter
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoContract
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasCanceladasActivity
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.minhasVendas.fragments.CancelTutorialBottomSheetFragment
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.presentation.ui.MySalesFiltersBottomSheetFragment
import br.com.mobicare.cielo.solesp.ui.SolespNavigationFlowActivity
import kotlinx.android.synthetic.main.extrato_fragment.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class ExtratoFragment : BaseFragment(), ExtratoContract.View,
    MySalesFiltersBottomSheetFragment.OnDismissListener
{

    private val ga4: MySalesGA4 by inject()
    private lateinit var filterCompFragment: ComponentDateFilterFragment

    private val presenter: ExtratoPresenter by inject {
        parametersOf(this)
    }

    private var diasPassados = -120

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.extrato_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createFilterComponent()
        presenter.loadExtratoByDailyDate(DataCustom(Calendar.getInstance().time))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isConvivenciaUser = UserPreferences.getInstance().isConvivenciaUser
        if (isConvivenciaUser) {
            diasPassados = -365
        }

        configureToolbarActionListener?.changeTo(title = getString(R.string.home_minhas_vendas_label))
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        initButtons()
    }

    private fun createFilterComponent() {
        this.context?.let { itContext ->
            ComponentDateFilterFragment.use18MonthsInCalendar = true
            this.filterCompFragment = ComponentDateFilterFragment.newInstance(
                clickFilterMoreOptions = {
                    this.presenter.showMoreFilters()
                },
                clickFilterByDaily = {
                    this.presenter.resetFilter()
                    this.presenter.loadExtratoByDailyDate(it)
                },
                clickFilterDateInterval = { startDate: DataCustom, endDate: DataCustom, isResetFilter: Boolean ->
                    if (isResetFilter) {
                        this.presenter.resetFilter()
                    }
                    this.presenter.loadExtratoByInterval(startDate, endDate)
                },
                clickFilterByPeriod = {
                    this.presenter.resetFilter()
                    this.presenter.loadExtratoByPeriod(it)
                },
                filterColor = R.color.blue_017CEB,
                filterTheme = R.style.DialogThemeMinhasVendas,
            )

            this.childFragmentManager
                .beginTransaction()
                .replace(R.id.content_filter, this.filterCompFragment)
                .commit()
        }

    }

    override fun showErrorData() {
        if (isAttached()) {
            AlertDialogCustom.Builder(this.context, getString(R.string.ga_extrato))
                .setMessage(getString(R.string.extrato_filtro_atencao_datas_nullas))
                .setBtnRight(getString(R.string.ok))
                .show()
        }
    }

    override fun validaOutrosPeriodos(): Boolean {
        return true
    }

    private fun initButtons() {
        view_transparent.setOnClickListener {
            hideFiltro()
        }
    }

    override fun showFiltro() {
    }

    override fun hideFiltro() {
    }

    override fun onPause() {
        super.onPause()
        setHasOptionsMenu(false)
    }

    override fun attachedFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout_extrato, fragment)
        transaction.commit()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.actionMoreOptions).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_extrato, menu)  // Use filter.xml from step 1
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_extrato_help -> {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Category.TAPICON),
                    action = listOf(Action.HOME_MINHAS_VENDAS),
                    label = listOf(
                        String.format(
                            Label.BT_MINHAS_VENDAS_INFO,
                            getString(R.string.home_minhas_vendas_label)
                        )
                    )
                )

                showAlert(getString(R.string.ga_extrato), getString(R.string.extrato_help_text))
                true
            }
            R.id.action_extrato_shared -> {
                true
            }
            R.id.actionMoreOptions -> {
                presenter.openBottomSheetMoreOptions()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun openBottomSheetMoreOptions(enabledOptionSolesp: Boolean, enabledOptionChargeback: Boolean ) {
        val selectItems = ArrayList<RowSelectItemModel>()
        if (enabledOptionSolesp) {
            selectItems.add(
                RowSelectItemModel(
                    label = R.string.option_bottom_sheet_solesp,
                    onClick = {
                        ga4.selectContent(REQUEST_SPECIAL_EXTRACT)
                        requireActivity().startActivity<SolespNavigationFlowActivity>()
                    },
                    iconStart = R.drawable.ic_payment_receipt_brand_400
                )
            )
        }
        selectItems.add(
            RowSelectItemModel(
                label = R.string.option_bottom_sheet_canceled_sales,
                onClick = {
                    ga4.selectContent(CANCELED_SALES)
                    requireActivity().startActivity<MinhasVendasCanceladasActivity>()
                },
                iconStart = R.drawable.ic_payment_credit_card_x_brand_400
            )
        )
        if (enabledOptionSolesp) {
            selectItems.add(
                RowSelectItemModel(
                    label = R.string.option_bottom_sheet_chargeback,
                    onClick = {
                        ga4.selectContent(SALES_DISPUTE)
                        requireActivity().startActivity<ChargebackNavigationFlowActivity>()
                    },
                    iconStart = R.drawable.ic_left_icon_blue
                )
            )
        }
        SelectItemBottomSheet.onCreate(selectItems).show(childFragmentManager, tag)
    }

    fun showAlert(title: String, message: String) {
        if (isAttached()) {
            AlertDialogCustom.Builder(context, getString(R.string.ga_extrato))
                .setTitle(title)
                .setMessage(message)
                .setBtnRight(getString(android.R.string.ok))
                .show()
        }
    }

    override fun showMoreFilters(quickFilter: QuickFilter, isShowPaymentTypes: Boolean) {

        MySalesFiltersBottomSheetFragment.newInstance(quickFilter,
            object : MySalesFiltersBottomSheetFragment.OnResultListener {
                override fun onResult(quickFilter: QuickFilter) {
                    this@ExtratoFragment.presenter.refresh(quickFilter)
                }
            }, isShowPaymentTypes).apply {
                this.onDismissListener = this@ExtratoFragment
        }.show(childFragmentManager,"MinhasVendasFilterBottomSheetFragment")
    }

    override fun changeColorFilter(isNotApplied: Boolean) {
        this.filterCompFragment.changeColorMoreFilter(isNotApplied)
    }

    override fun showCancelTutorial() {
        CancelTutorialBottomSheetFragment().show(
            childFragmentManager,
            CancelTutorialBottomSheetFragment::class.java.simpleName
        )
    }

    override fun onDismiss() {
        this.filterCompFragment.isMoreFiltersOpened = false
    }

    companion object{
        const val REQUEST_SPECIAL_EXTRACT = "solicitar_extrato_especial"
        const val CANCELED_SALES = "vendas_canceladas"
        const val SALES_DISPUTE = "contestacao_de_vendas"
    }
}