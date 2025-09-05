package br.com.mobicare.cielo.commons.utils

import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.ONE
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class DataCustomNew : Parcelable {

    private var now: Calendar
    private val PATTERN_BR = "dd/MM/yyyy"
    private val PATTERN_API = "yyyy-MM-dd"
    private val locale = Locale("pt", "BR")

    init {
        now = CalendarCustom.now
    }

    fun formatBRDate() = SimpleDateFormat(PATTERN_BR, locale).format(now.time)
    fun formatDateToAPI() = SimpleDateFormat(PATTERN_API, locale).format(now.time)
    fun toDate() = now.time
    fun toCalendar() = now


    /**
     *  Set new date
     */
    fun setDate(year: Int, month: Int, day: Int) {
        now.set(year, month - 1, day)
    }

    /**
     *  Set new date from add or subtract day by CURRENT Date
     */
    fun setDate(quantDays: Int = 0) {
        now = CalendarCustom.now
        now.add(Calendar.DAY_OF_MONTH, quantDays)
    }

    /**
     *  Set new Date
     */
    fun setDate(date: Date) {
        now.time = date
    }

    /**
     *  Set new date from add or subtract day by SELECTED Date
     */
    fun setDateByDate(quantDays: Int = 0) {
        now.add(Calendar.DAY_OF_MONTH, quantDays)
    }

    /**
     *  Set new date by api
     */
    fun setDateFromAPI(date: String? = "0000/00/00") : DataCustomNew {
        val sdf = SimpleDateFormat(PATTERN_API, locale)
        now.time = sdf.parse(date)
        return this
    }

    fun setDateFromBR(date: String) {
        val sdf = SimpleDateFormat(PATTERN_BR, locale)
        now.time = sdf.parse(date)
    }

    fun getYear(): Int{
        return CalendarCustom.getYear(now)
    }

    fun getMonth(): Int {
        return CalendarCustom.getMonth(now) + ONE
    }

    fun getDay(): Int {
        return CalendarCustom.getDay(now)
    }

}