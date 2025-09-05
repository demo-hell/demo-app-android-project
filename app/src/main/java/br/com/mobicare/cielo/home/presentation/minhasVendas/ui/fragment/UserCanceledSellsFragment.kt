package br.com.mobicare.cielo.home.presentation.minhasVendas.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.fragment.components.filters.date.ComponentDateFilterFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.presentation.presenter.ExtratoPresenter
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoContract
import br.com.mobicare.cielo.minhasVendas.activities.SCREENVIEW_LISTA_VENDAS_CANCELADAS
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class UserCanceledSellsFragment : BaseFragment(), ExtratoContract.View,
    MinhasVendasFilterBottomSheetFragment.OnDismissListener {


    private lateinit var filterCompFragment: ComponentDateFilterFragment

    var isCheck = false

    private val statementCancelPresenter: ExtratoPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_user_canceled_sells,
            container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.createFilterComponent()
        statementCancelPresenter.isFilterByCanceledStatus = true
        setHasOptionsMenu(false)

        val yesterday = DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, ONE)

        gaFiltroMeusCancelamentos(getString(R.string.text_yesterday_statement_label))
        Analytics.trackScreenView(
            screenName = SCREENVIEW_LISTA_VENDAS_CANCELADAS,
            screenClass = this.javaClass
        )

        isCheck = false
        statementCancelPresenter.quickFilter = QuickFilter.Builder()
            .initialDate(yesterday)
            .finalDate(yesterday)
            .status(listOf(ExtratoStatusDef.CANCELADA))
            .build()

        statementCancelPresenter
            .loadExtratoByInterval(DataCustom(yesterday), DataCustom(yesterday))
    }

    private fun createFilterComponent() {
        this.childFragmentManager.let {
            ComponentDateFilterFragment.use18MonthsInCalendar = false
            this.filterCompFragment = ComponentDateFilterFragment.newInstance(
                clickFilterMoreOptions = {
                    this.statementCancelPresenter.showMoreFilters()
                },
                clickFilterByDaily = {
                    setupYesterdayPeriod()
                },
                clickFilterDateInterval = { startDate: DataCustom, endDate: DataCustom, isResetFilter: Boolean ->
                    setupOtherPeriods(isResetFilter, startDate, endDate)
                },
                clickFilterByPeriod = { itPeriod ->
                    setupSevenFifteenOrThirtyDaysPeriod(itPeriod)
                },
                filterColor = R.color.brand_400,
                isCanceledSells = true,
                filterTheme = R.style.DialogThemeMinhasVendas
            )

            this.filterCompFragment.addInFrame(childFragmentManager,
                    R.id.content_filter)
        }
    }

    private fun setupYesterdayPeriod() {
        gaFiltroMeusCancelamentos(getString(R.string.text_yesterday_statement_label))
        Analytics.trackScreenView(
            screenName = SCREENVIEW_LISTA_VENDAS_CANCELADAS,
            screenClass = javaClass
        )
        isCheck = false
        statementCancelPresenter.resetFilter()

        val yesterday = DataCustom(
            DateTimeHelper.decreaseDateByNumberDays(
                Calendar.getInstance().time,
                ONE
            )
        )
        this.statementCancelPresenter.loadExtratoByInterval(yesterday, yesterday)
    }

    private fun setupOtherPeriods(
        isResetFilter: Boolean,
        startDate: DataCustom,
        endDate: DataCustom
    ) {
        if (isCheck.not()) {
            gaFiltroMeusCancelamentos(getString(R.string.filter_outros_periodos))
            Analytics.trackScreenView(
                screenName = SCREENVIEW_LISTA_VENDAS_CANCELADAS,
                screenClass = this.javaClass
            )

            isCheck = true
        }

        if (isResetFilter) {
            this.statementCancelPresenter.resetFilter()
        }
        this.statementCancelPresenter.loadExtratoByInterval(startDate, endDate)
    }

    private fun setupSevenFifteenOrThirtyDaysPeriod(days: String) {
        gaFiltroMeusCancelamentos(getString(R.string.filter_last_days, days))
        Analytics.trackScreenView(
            screenName = SCREENVIEW_LISTA_VENDAS_CANCELADAS,
            screenClass = javaClass
        )

        isCheck = false
        statementCancelPresenter.resetFilter()
        statementCancelPresenter.loadExtratoByPeriod(days)
    }

    override fun showMoreFilters(quickFilter: QuickFilter, isShowPaymentTypes: Boolean) {
        val minhasVendasFilterBottomSheetFrag =
            MinhasVendasFilterBottomSheetFragment.create(quickFilter,
                object : MinhasVendasFilterBottomSheetFragment.OnResultListener {
                    override fun onResult(quickFilter: QuickFilter) {
                        this@UserCanceledSellsFragment.statementCancelPresenter.refresh(quickFilter)
                    }
                }, isShowPaymentTypes
            )
        minhasVendasFilterBottomSheetFrag.onDismissListener = this

        minhasVendasFilterBottomSheetFrag
            .show(this.childFragmentManager, "MinhasVendasFilterBottomSheetFragment")
    }

    override fun attachedFragment(fragment: Fragment) {
        fragment.addInFrame(childFragmentManager, R.id.frameUserCanceledSellsContent)
    }

    override fun changeColorFilter(isNotApplied: Boolean) {
        this.filterCompFragment.changeColorMoreFilter(isNotApplied)
    }

    override fun onDismiss() {
        this.filterCompFragment.isMoreFiltersOpened = false
    }

    private fun gaFiltroMeusCancelamentos(periodo: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
                action = listOf(Action.FILTRO),
                label = listOf(Label.BOTAO, periodo)
            )
        }
    }

}