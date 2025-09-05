package br.com.mobicare.cielo.commons.ui.fragment.components.filters.date

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.DataCustom
import java.util.*

class ComponentDateFilterPresenter(private val view: ComponentDateFilterContract.View, private val isCanChangeDailyDate: Boolean = false) : ComponentDateFilterContract.Presenter {

    override val DATE_FUTURE: Int
        get() = 1
    override val DATE_PAST: Int
        get() = 2
    override val DATE_NOTHING: Int
        get() = 3

    var startDateFilter: DataCustom = DataCustom(Calendar.getInstance(TimeZone.getDefault()))
    var endDataFilter: DataCustom = DataCustom(Calendar.getInstance(TimeZone.getDefault()))
    var dailyDate: DataCustom = DataCustom(Calendar.getInstance(TimeZone.getDefault()))
    var isResetFilter: Boolean = false

    init {
        this.view.tvDailyUpdate(this.dailyDate.toString())
        this.view.tvDateInitUpdate("")
        this.view.tvDateEndUpdate("")
    }

    override fun onStart() {
        this.isResetFilter = true
        var thirtyDays = DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, 30)
        var yesterday = DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, 1)
        this.changeStartDate(DataCustom(thirtyDays))
        this.changeEndDate(DataCustom(yesterday))
    }

    override fun onClickFilterDaily() {
        if (isCanChangeDailyDate) {
            this.view.changeVisibilityForChangeDailyDate(true)
        }
        else {
            this.view.changeVisibilityForChangeDailyDate(false)
            this.view.onFilterByDailyDate(this.dailyDate)
        }
    }

    override fun onCLickToChangeDailyDate() {
        this.view.onSelectDateForDailyDate(this.dailyDate)
    }

    override fun changeDailyDate(date: DataCustom) {
        this.dailyDate = date
        this.view.tvDailyUpdate(dailyDate.toString())
        this.view.onFilterByDailyDate(date)
    }

    override fun changeStartDate(date: DataCustom) {
        this.startDateFilter = date
        view.tvDateInitUpdate(this.startDateFilter.toString())
        fireFilter()
    }

    override fun changeEndDate(date: DataCustom) {
        if (date.toDate() < this.startDateFilter?.toDate()) {
            view.showFilterErroAlert(R.string.extrato_filtro_atencao_data_invalida_title, R.string.extrato_filtro_atencao_data_invalida)
        } else {
            this.endDataFilter = date
            this.view.tvDateEndUpdate(this.endDataFilter.toString())
            fireFilter()
        }
    }

    private fun fireFilter() {
        this.view.onFilterByIntervalDate(this.startDateFilter, this.endDataFilter, this.isResetFilter)
        this.isResetFilter = false
    }

    override fun changeDate(date: DataCustom) {
        dailyDate = date
    }

    override fun changeDateInit(date: DataCustom) {
        dailyDate = date
    }

    override fun onCLickStartDate() {
        //this.view.tvDateInitUpdate(this.startDateFilter.toString())
        this.view.onClickDataInicio(this.startDateFilter)
    }

    override fun onClickEndDate() {
        //this.view.tvDateEndUpdate(this.endDataFilter.toString())
        this.view.onClickDataFinal(this.endDataFilter)
    }

}