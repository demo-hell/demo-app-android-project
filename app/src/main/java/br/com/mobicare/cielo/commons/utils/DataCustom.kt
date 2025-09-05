package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.FORMAT_MINIMUM_2_DIGITS
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SEPARATOR
import br.com.mobicare.cielo.commons.constants.ZERO
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by silvia.miranda on 08/06/2017.
 */

class DataCustom {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var initialDate: String? = null
    var finalDate: String? = null

    constructor() {
        loadData(ZERO, ZERO, ZERO)
    }

    constructor(year: Int, month: Int, day: Int) {
        loadData(year, month, day)
    }

    constructor(date: Date) {
        val cal = CalendarCustom.getCalendarFrom(date)
        loadData(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + ONE,
                cal.get(Calendar.DAY_OF_MONTH))
    }

    constructor(date: String){
        val formatter = SimpleDateFormat(SIMPLE_DT_BR_SINGLE_YEAR)
        val date = formatter.parse(date) as Date
        val cal = CalendarCustom.getCalendarFrom(date)
        loadData(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + ONE,
                cal.get(Calendar.DAY_OF_MONTH))

    }

    constructor(cal: Calendar) {
        loadData(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + ONE,
                cal.get(Calendar.DAY_OF_MONTH))
    }

    fun loadData(year: Int, month: Int, day: Int) {
        val now = CalendarCustom.now
        if (year == ZERO) {
            this.year = CalendarCustom.getYear(now)
        } else {
            this.year = year
        }
        if (month == ZERO) {
            this.month = CalendarCustom.getMonth(now)
        } else {
            this.month = month
        }
        if (day == ZERO) {
            this.day = CalendarCustom.getDay(now)
        } else {
            this.day = day
        }
    }

    fun formatDateNew(quantDay: Int) {
        var now = CalendarCustom.now
        val sdf = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
        finalDate = sdf.format(now.time)
        now.add(Calendar.DAY_OF_MONTH, -quantDay)
        initialDate = sdf.format(now.time)
        this.year = CalendarCustom.getYear(now)
        this.month = CalendarCustom.getMonth(now) + ONE
        this.day = CalendarCustom.getDay(now)
    }

    fun formatBRDate(): String {
        val now = CalendarCustom.now
        val sdf = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK)
        return sdf.format(now.time)
    }

    fun formatDate(): String {
        if (day > ZERO && month > ZERO && year > ZERO) {
            return "${this.year}-${String.format(FORMAT_MINIMUM_2_DIGITS, this.month)}-${String.format(FORMAT_MINIMUM_2_DIGITS, this.day)}"
        }
        return EMPTY_VALUE
    }

    fun formatBrDateNowOrFuture(): String {
        if (day > ZERO && month > ZERO && year > ZERO) {
            return "${String.format(FORMAT_MINIMUM_2_DIGITS, this.day)}/${String.format(FORMAT_MINIMUM_2_DIGITS, this.month)}/${this.year}"
        }
        return EMPTY_VALUE
    }

    fun formatDate(daily: String?,
                   dailyDate: DataCustom?,
                   initialDate: String?,
                   finalDate: String?,
                   startDate: DataCustom?,
                   endDate: DataCustom?,
                   period: String?) {
        if (daily != null) {
            this.initialDate = dailyDate?.formatDate()
            this.finalDate = dailyDate?.formatDate()
            return
        }
        if (initialDate != null && finalDate != null) {
            this.initialDate = startDate?.formatDate()
            this.finalDate = endDate?.formatDate()
            return
        }
        if (period != null) {
            this.finalDate = formatDate()
            loadData(ZERO, ZERO, ZERO)
            this.initialDate = formatDate()
            return
        }
    }

    fun toCalendar(): Calendar {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(year, month - ONE, day)
        return calendar
    }

    fun toDate(): Date {
        val calendar = toCalendar()
        return calendar.time
    }

    fun toDateOnly(): Date {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.set(year, month - ONE, day)
        calendar.set(Calendar.HOUR_OF_DAY, ZERO);
        calendar.set(Calendar.MINUTE, ZERO);
        calendar.set(Calendar.SECOND, ZERO);
        calendar.set(Calendar.MILLISECOND, ZERO);
        return calendar.time
    }

    override fun toString(): String {
        if (day > ZERO && month > ZERO && year > ZERO) {
                return String.format(FORMAT_MINIMUM_2_DIGITS, day) + SEPARATOR + String.format(FORMAT_MINIMUM_2_DIGITS, month) + SEPARATOR + year
        }
        return EMPTY_VALUE
    }

    fun isGreaterThen(other: DataCustom) : Boolean {
        val currentRawTime = this.toDateOnly().time
        val otherRawTime = other.toDateOnly().time
        return currentRawTime > otherRawTime
    }

    fun isBelowThen(other: DataCustom) : Boolean {
        val currentRawTime = this.toDateOnly().time
        val otherRawTime = other.toDateOnly().time
        return currentRawTime < otherRawTime
    }

}
