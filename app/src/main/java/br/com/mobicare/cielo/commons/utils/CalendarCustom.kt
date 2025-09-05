package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.ONE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by silvia.miranda on 08/06/2017.
 */

class CalendarCustom {
    init {
        throw UnsupportedOperationException()
    }

    companion object {

        val now: Calendar
            get() = Calendar.getInstance(TimeZone.getDefault())

        fun getYear(calendar: Calendar): Int {
            return calendar.get(Calendar.YEAR)
        }

        fun getMonth(calendar: Calendar): Int {
            return calendar.get(Calendar.MONTH)
        }

        fun getDay(calendar: Calendar): Int {
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

        fun getCalendarFrom(date: Date): Calendar {
            val cal = Calendar.getInstance()
            cal.time = date
            return cal
        }
    }
}
