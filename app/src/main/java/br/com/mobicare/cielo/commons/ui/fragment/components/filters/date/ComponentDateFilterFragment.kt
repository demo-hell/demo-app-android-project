package br.com.mobicare.cielo.commons.ui.fragment.components.filters.date

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.LESS_540_DAYS
import br.com.mobicare.cielo.commons.constants.SELECTED_LESS_ONE_DAY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters.ComponentFilterAdapter
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.synthetic.main.component_filter_fragment.*
import java.util.*

const val RANGE_18MONTHS_IN_DAYS = 546

class ComponentDateFilterFragment : BaseFragment(), ComponentDateFilterContract.View {

    var listOptions: MutableList<String> = mutableListOf()
    var adapter: ComponentFilterAdapter? = null
    var presenter: ComponentDateFilterContract.Presenter? = null
    var periods = arrayListOf("7", "15", "30")
    var isMoreFiltersOpened = false

    private var clickFilterMoreOptions: () -> Unit = {}
    private var clickFilterByDaily: (date: DataCustom) -> Unit = {}
    private var clickFilterDateInterval: (startDate: DataCustom, endDate: DataCustom, isResetFilter: Boolean) -> Unit = { a, b, c -> }
    private var clickFilterByPeriod: (period: String) -> Unit = {}
    private var isCanChangeDailyDate: Boolean = false
    private var isCanceledSells: Boolean = false
    @ColorRes private var filterColor: Int = R.color.blue_017CEB
    @StyleRes private var filterTheme: Int = R.style.DialogThemeMinhasVendas

    companion object {

        var use18MonthsInCalendar: Boolean = false
        private var daysInFuture = ZERO
        private var daysInPast = ZERO

        fun newInstance(
            clickFilterMoreOptions: () -> Unit = {},
            clickFilterByDaily: (date: DataCustom) -> Unit = {},
            clickFilterDateInterval: (startDate: DataCustom, endDate: DataCustom, isResetFilter: Boolean) -> Unit = { a, b, c -> },
            clickFilterByPeriod: (period: String) -> Unit = {},
            isCanChangeDailyDate: Boolean = false,
            isCanceledSells: Boolean = false,
            @ColorRes filterColor: Int = R.color.blue_017CEB,
            @StyleRes filterTheme: Int = R.style.DialogThemeMinhasVendas
        ): ComponentDateFilterFragment = ComponentDateFilterFragment().apply {
            this.clickFilterMoreOptions = clickFilterMoreOptions
            this.clickFilterByDaily = clickFilterByDaily
            this.clickFilterDateInterval = clickFilterDateInterval
            this.clickFilterByPeriod = clickFilterByPeriod
            this.isCanChangeDailyDate = isCanChangeDailyDate
            this.isCanceledSells = isCanceledSells
            this.filterColor = filterColor
            this.filterTheme = filterTheme
        }
    }



    init {
        if(use18MonthsInCalendar){
            daysInPast = -RANGE_18MONTHS_IN_DAYS
            daysInFuture = SELECTED_LESS_ONE_DAY
        }else{
            daysInPast = LESS_540_DAYS
            daysInFuture = SELECTED_LESS_ONE_DAY
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.component_filter_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.presenter = ComponentDateFilterPresenter(this, isCanChangeDailyDate)
        this.context?.let { itContext ->
            view.setBackgroundColor(ContextCompat.getColor(itContext, this.filterColor))
        }

        //lista de itens
        if (!isCanceledSells) {
            listOptions.add(getString(R.string.filter_hoje))
        } else {
            listOptions.add(getString(R.string.text_yesterday_statement_label))
        }

        listOptions.add(getString(R.string.filter_ultimos_7_dias))
        listOptions.add(getString(R.string.filter_ultimos_15_dias))
        listOptions.add(getString(R.string.filter_ultimos_30_dias))
        listOptions.add(getString(R.string.filter_outros_periodos))

        val horizontalLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        rv_options.layoutManager = horizontalLayoutManager
        adapter = ComponentFilterAdapter(listOptions)
        rv_options.adapter = adapter

        this.moreFilterButton?.setOnClickListener {
            if (!isMoreFiltersOpened) {
                this.clickFilterMoreOptions()
                isMoreFiltersOpened = true
            }
        }
        et_daily.setOnClickListener {
            this.presenter?.onClickFilterDaily()
        }
        et_date_init.setOnClickListener {
            this.presenter?.onCLickStartDate()
        }
        et_date_end.setOnClickListener {
            this.presenter?.onClickEndDate()
        }

        adapter?.onDateFilterSelectedListener = object : ComponentFilterAdapter.OnItemClickListener {
            override fun onClicked(view: View, position: Int) {
                if (this@ComponentDateFilterFragment.isAttached()) {
                    if (position == 4) {
                        this@ComponentDateFilterFragment.presenter?.onStart()
                    }
                    this@ComponentDateFilterFragment.layout_choose_dates.visibility = if (position == 4) View.VISIBLE else View.GONE
                    if (position == 0) {
                        this@ComponentDateFilterFragment.presenter?.onClickFilterDaily()
                    } else if (position in 1..3) {
                        clickFilterByPeriod(periods[position - 1])
                    }
                }
            }
        }

    }

    override fun changeVisibilityForChangeDailyDate(isShow: Boolean) {
        this@ComponentDateFilterFragment.layout_choose_dates_dialy.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun daily(): DataCustom {
        return if (this.isAttached()) {
            if (tv_date_daily != null && tv_date_daily.text != "Selecione uma data") {
                DataCustom(tv_date_daily.text.toString())
            } else {
                DataCustom(Date().nextWeekday(0))
            }
        } else {
            DataCustom(Date().nextWeekday(0))
        }
    }

//    override fun clickPosition(position: Int) {
//        if (this.isAttached()) {
//            layout_choose_dates.visibility = if (position == 4) View.VISIBLE else View.GONE
//            layout_choose_dates_dialy.visibility = if (position == 0) View.VISIBLE else View.GONE
//        }
//    }

    override fun tvDailyUpdate(date: String) {
        if (this.isAttached()) {
            tv_date_daily?.let {
                it.text = date
                Utils.addFontMuseoSans700(context as Context, tv_date_daily)
            }
        }
    }

    override fun tvDateInitUpdate(date: String) {
        if (this.isAttached()) {
            tv_date_init.text = date
            Utils.addFontMuseoSans700(context as Context, tv_date_init)
        }
    }

    override fun tvDateEndUpdate(date: String) {
        if (this.isAttached()) {
            tv_date_end.text = date
            Utils.addFontMuseoSans700(context as Context, tv_date_end)
        }
    }

    override fun showFilterErroAlert(titleId: Int, msgId: Int) {
        AlertDialogCustom.Builder(this.context, getString(R.string.menu_meus_recebimentos))
            .setTitle(titleId)
            .setMessage(getString(msgId))
            .setBtnRight(getString(R.string.ok))
            .show()
    }

    override fun showErrorData() {
        if (isAttached()) {
            AlertDialogCustom.Builder(this.context, getString(R.string.menu_meus_recebimentos))
                .setTitle(R.string.extrato_filtro_atencao_data_invalida_title)
                .setMessage(getString(R.string.extrato_filtro_atencao_datas_nullas))
                .setBtnRight(getString(R.string.ok))
                .show()
        }
    }

    override fun onSelectDateForDailyDate(date: DataCustom) {
        if (this.isAttached()) {
            val cal = date.toCalendar()
            val dia = CalendarCustom.getDay(cal)
            val mes = CalendarCustom.getMonth(cal)
            val ano = CalendarCustom.getYear(cal)

            CalendarDialogCustom(LESS_540_DAYS, SELECTED_LESS_ONE_DAY, SELECTED_LESS_ONE_DAY, dia, mes, ano, EMPTY, context as Context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val dateFilter = DataCustom(year, monthOfYear, dayOfMonth)
                this.presenter?.changeDailyDate(dateFilter)
            }, filterTheme).show()
        }
    }

    override fun onClickDataInicio(date: DataCustom) {
        if (this.isAttached()) {
            val cal = date.toCalendar()
            val dia = CalendarCustom.getDay(cal)
            val mes = CalendarCustom.getMonth(cal)
            val ano = CalendarCustom.getYear(cal)

            CalendarDialogCustom(daysInPast, daysInFuture, SELECTED_LESS_ONE_DAY, dia, mes, ano, getString(R.string.extrato_filtro_data_inicio), context as Context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val startDateFilter = DataCustom(year, monthOfYear, dayOfMonth)
                this.presenter?.changeStartDate(startDateFilter)
            }, filterTheme).show()
        }
    }

    override fun onClickDataFinal(date: DataCustom) {
        val cal = date.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(daysInPast, daysInFuture, SELECTED_LESS_ONE_DAY, dia, mes, ano, getString(R.string.extrato_filtro_data_fim), context as Context, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val endDateFilter = DataCustom(year, monthOfYear, dayOfMonth)
            this.presenter?.changeEndDate(endDateFilter)
        }, filterTheme).show()
    }

    override fun onFilterByIntervalDate(startDate: DataCustom, endData: DataCustom, isResetFilter: Boolean) {
        this.clickFilterDateInterval(startDate, endData, isResetFilter)
    }

    override fun onFilterByDailyDate(date: DataCustom) {
        this.clickFilterByDaily(date)
    }

    fun changeColorMoreFilter(isFilterNotApplied: Boolean) {
        if (isFilterNotApplied) {
            this.moreFilterButton?.setImageResource(R.drawable.ic_filter)
        } else {
            this.moreFilterButton?.setImageResource(R.drawable.ic_filter_filled)
        }
    }

}