package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType

interface ComponentFilterListener {

    fun onClickDate(initialDate: String, finalDate: String, isGraphSelection: Boolean = false, selectedDateType: DayType.Type? = null)
    fun showGraph(mainDate: DataCustomNew) {}
    fun hideGraph() {}
    fun showFilterErroAlert(){}

    interface VisibilityOnShowAnotherDates {
        fun onShowDailyDate(date: DataCustomNew)
        fun onShowAnotherDates(initialDate: DataCustomNew, date: DataCustomNew)
        fun onHideAnotherDates()
    }

    interface UpdateDateFromGraph{
        fun updateDateFromGraph(initialDate: String, finalDate: String)
    }
}