package br.com.mobicare.cielo.commons.utils

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.addDays(days: Int) {
    this.add(Calendar.DAY_OF_YEAR, days)
}

fun Calendar.addYears(years: Int) {
    this.add(Calendar.YEAR, years)
}

fun Calendar.addMonths(months: Int) {
    this.add(Calendar.MONTH, months)
}

fun Calendar.format( dtPattern: String = SIMPLE_DT_FORMAT_MASK) : String {
    return SimpleDateFormat(dtPattern, Locale("pt", "BR")).format(this.time)
}

fun Calendar.isNotWeekend(): Boolean {
    return this.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
            this.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
}

fun Calendar.dateWithoutTime(date: Date): Date {
    val cal = this
    cal.time = date
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}