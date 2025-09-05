package br.com.mobicare.cielo.commons.ui.fragment.components.filters.date

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.commons.utils.DataCustom

interface ComponentDateFilterContract {

    interface View : IAttached {
        fun tvDailyUpdate(date: String)
        fun tvDateInitUpdate(date: String)
        fun tvDateEndUpdate(date: String)
        fun daily() : DataCustom
        fun showErrorData()
        fun showFilterErroAlert(titleId: Int, msgId: Int)
        fun onSelectDateForDailyDate(date: DataCustom)
        fun onClickDataInicio(date: DataCustom)
        fun onClickDataFinal(date: DataCustom)
        fun onFilterByIntervalDate(startDate: DataCustom, endData: DataCustom, isResetFilter: Boolean=false)
        fun onFilterByDailyDate(date: DataCustom)
        fun changeVisibilityForChangeDailyDate(isShow: Boolean)
    }

    interface Presenter {
        val DATE_FUTURE: Int
        val DATE_PAST: Int
        val DATE_NOTHING: Int

        fun onStart()
        fun onClickFilterDaily()
        fun onCLickToChangeDailyDate()
        fun onCLickStartDate()
        fun onClickEndDate()
        fun changeDailyDate(date: DataCustom)
        fun changeStartDate(date: DataCustom)
        fun changeEndDate(date: DataCustom)
        fun changeDate(date: DataCustom)
        fun changeDateInit(date: DataCustom)
    }

}