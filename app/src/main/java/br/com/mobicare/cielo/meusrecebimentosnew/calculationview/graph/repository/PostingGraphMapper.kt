package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository

import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.IncomingObj
import java.util.*
import kotlin.collections.ArrayList

object PostingGraphMapper {

    fun mapper(response: PostingsResponse, mainDate: DataCustomNew): ArrayList<IncomingObj> {
        val list = ArrayList<IncomingObj>()
        val summaryList = response.summary.reversed()
        val today = DataCustomNew()

        getRangeDates(mainDate).forEach { rangeDate ->
            val incomingObj = IncomingObj()
            incomingObj.dayOfMonth = rangeDate.toCalendar().get(Calendar.DAY_OF_MONTH).toString()
            incomingObj.dayOfWeek = rangeDate.toCalendar().format(SIMPLE_DAY_OF_WEEK_MASK).capitalizePTBR()
            incomingObj.date = rangeDate.toCalendar().format()
            incomingObj.cieloDate = rangeDate.toCalendar().format(SIMPLE_DATE_INTERNATIONAL)
            incomingObj.totalDeposited = 0.0

            val simpleDayDescription = rangeDate.toCalendar()
                    .format(SIMPLE_DAY_DESCRIPITION)
                    .capitalizePTBR()
            if (today.formatDateToAPI().equals(rangeDate.formatDateToAPI()))
                incomingObj.dayDescription = "Hoje ($simpleDayDescription)"
            else incomingObj.dayDescription = simpleDayDescription

            if (mainDate.formatDateToAPI().equals(rangeDate.formatDateToAPI()))
                incomingObj.mainDay = true

            summaryList.forEach { summary ->
                val date = DataCustomNew()
                date.setDateFromAPI(summary.date!!)

                if (rangeDate.formatDateToAPI().contains(date.formatDateToAPI())) {
                    incomingObj.totalDeposited = summary.totalAmount
                    if (summary.pendingAmount != null) incomingObj.totalPending = summary.pendingAmount
                    else incomingObj.totalPending = 0.0
                }
            }
            list.add(incomingObj)
        }
        return list
    }

    private fun getRangeDates(mainDate: DataCustomNew): MutableList<DataCustomNew> {
        val previousDate: MutableList<DataCustomNew> = ArrayList()
        val nextDate: MutableList<DataCustomNew> = ArrayList()
        val rangeDates: MutableList<DataCustomNew> = ArrayList()

        for (i in -7..0) {
            val previous = DataCustomNew()
            previous.setDate(mainDate.toDate())
            previous.setDateByDate(i)
            previousDate.add(previous)
        }

        for (i in 1..7) {
            val next = DataCustomNew()
            next.setDate(mainDate.toDate())
            next.setDateByDate(i)
            nextDate.add(next)
        }
        rangeDates.addAll(previousDate)
        rangeDates.addAll(nextDate)
        previousDate.clear()
        nextDate.clear()
        return rangeDates
    }
}