package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.ComponentFilterAdapterNew
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import kotlinx.android.synthetic.main.component_filter_fragment.*

const val DEFAULT_DATE_RANGE_DAYS = "360"
const val RANGE_18MONTHS_IN_DAYS = 548

class ComponentFilterFragmentNew : BaseFragment(), ComponentFilterListener,
        ComponentFilterListener.VisibilityOnShowAnotherDates,
        ComponentFilterListener.UpdateDateFromGraph {

    private var adapter: ComponentFilterAdapterNew? = null
    private lateinit var presenter: FilterPresenterNew
    private var initialDate = DataCustomNew()
    private var finalDate = DataCustomNew()

    private val daysReceivablesLast: Int = configureCalendarDialogCustomDateRange(
        ConfigurationDef.DAYS_RECEIVABLES_LAST, ONE)

    private val daysReceivablesFuture: Int = configureCalendarDialogCustomDateRange(
        ConfigurationDef.DAYS_RECEIVABLES_FUTURE, THREE)


    /**
     * Configura os limites de datas que serao utilizados nos componentes de CalendarDialogCustom.
     * A configuracao e feita com base na flag use18MonthsInCalendar, se ela for true, entao
     * sera adotado um range de 18 meses para mais ou menos (dia atual - 18 meses para data
     * posterior e retroativa).
     * Caso a flag seja false, entao sera adotado o range vindo via API.
     *
     * @param lastOrFuture String
     * @param increment Int
     * @return Int
     * */
    private fun configureCalendarDialogCustomDateRange(lastOrFuture: String, increment: Int): Int{
        return if(use18MonthsInCalendar)
            RANGE_18MONTHS_IN_DAYS
        else{
            ConfigurationPreference.instance
                .getConfigurationValue(
                   lastOrFuture, DEFAULT_DATE_RANGE_DAYS
                ).toInt() + increment
        }
    }

    companion object {
        lateinit var componentFilterListener: ComponentFilterListener
        var use18MonthsInCalendar: Boolean = false
        var showDailyCalendar: Boolean = true

        fun newInstance(componentFilterListener: ComponentFilterListener)
                : ComponentFilterFragmentNew {
            this.componentFilterListener = componentFilterListener
            return ComponentFilterFragmentNew()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.component_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.context?.let { itContext ->
            view.setBackgroundColor(ContextCompat
                    .getColor(itContext, R.color.colorPrimary))
        }
        init()
    }

    fun init() {
        presenter = FilterPresenterNewImpl(this)
        val listDays = resources.getStringArray(R.array.array_filter_date)
                .mapIndexed { index, day -> DayType(day, index) }

        val horizontalLayoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false)
        rv_options.layoutManager = horizontalLayoutManager
        adapter = ComponentFilterAdapterNew(listDays, this, this)
        rv_options.adapter = adapter

        moreFilterButton.gone()
    }

    private fun onClickDataFinal() {
        val cal = finalDate.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(-daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
                getString(R.string.extrato_filtro_data_fim), context as Context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    finalDate
                            .setDate(year, monthOfYear, dayOfMonth)
                    tv_date_end.text = finalDate.formatBRDate()
                    presenter.callCalculationVision(initialDate, finalDate)
                }, R.style.DialogThemeMeusRecebimentos).show()
    }

    private fun onClickDataInicio() {
        val cal = initialDate.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(-daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
                getString(R.string.extrato_filtro_data_inicio), context as Context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    initialDate
                            .setDate(year, monthOfYear, dayOfMonth)
                    tv_date_init.text = initialDate.formatBRDate()
                    presenter.callCalculationVision(initialDate, finalDate)
                }, R.style.DialogThemeMeusRecebimentos).show()
    }

    override fun onClickDate(initialDate: String, finalDate: String, isGraphSelection: Boolean, selectedDateType: DayType.Type?) {
        componentFilterListener.onClickDate(initialDate, finalDate, isGraphSelection,selectedDateType)
    }

    override fun showGraph(mainDate: DataCustomNew) {
        componentFilterListener.showGraph(mainDate)
    }

    override fun hideGraph() {
        componentFilterListener.hideGraph()
    }

    override fun onShowDailyDate(date: DataCustomNew) {
        if(showDailyCalendar) {
            layout_choose_dates.visible()
            et_date_end.gone()
            tv_date_init.text = date.toCalendar()
                .format(FULL_DAY_DESCRIPITION)
                .capitalizePTBR()
            et_date_init.setOnClickListener { onClickDaily() }
        }
    }

    private fun onClickDaily() {
        val cal = initialDate.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(-daysReceivablesLast, daysReceivablesFuture, -1, dia, mes, ano,
                getString(R.string.text_select_date), context as Context,
                { view, year, monthOfYear, dayOfMonth ->
                    initialDate
                            .setDate(year, monthOfYear, dayOfMonth)
                    tv_date_init.text = initialDate.toCalendar()
                            .format(FULL_DAY_DESCRIPITION)
                            .capitalizePTBR()
                    presenter.callCalculationVisionDaily(initialDate)
                }, R.style.DialogThemeMeusRecebimentos).show()
    }

    override fun onShowAnotherDates(initialDate: DataCustomNew, date: DataCustomNew) {
        layout_choose_dates.visible()
        tv_date_init.text = initialDate.formatBRDate()
        tv_date_end.text = date.formatBRDate()
        et_date_end.visible()
        this.initialDate = initialDate
        this.finalDate = date
        presenter.callCalculationVision(initialDate, date)
        et_date_init.setOnClickListener { onClickDataInicio() }
        et_date_end.setOnClickListener { onClickDataFinal() }
    }

    override fun onHideAnotherDates() {
        hideGraph()
        layout_choose_dates.gone()
    }

    override fun showFilterErroAlert() {
        AlertDialogCustom.Builder(this.context, getString(R.string.menu_meus_recebimentos))
                .setTitle(R.string.extrato_filtro_atencao_data_invalida_title)
                .setMessage(getString(R.string.extrato_filtro_atencao_data_invalida))
                .setBtnRight(getString(R.string.ok))
                .show()
    }

    override fun updateDateFromGraph(initialDate: String, finalDate: String) {
        with(DataCustomNew()) {
            setDateFromAPI(initialDate)
            this@ComponentFilterFragmentNew.initialDate = this
            this@ComponentFilterFragmentNew.finalDate = this
            tv_date_init?.text = this.toCalendar()
                    .format(FULL_DAY_DESCRIPITION)
                    .capitalizePTBR()
        }
    }
}