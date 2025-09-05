package br.com.mobicare.cielo.commons.utils

import android.annotation.SuppressLint
import android.app.Activity
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZONE_ID_UTC
import br.com.cielo.libflue.util.dateUtils.getDayOfMonth
import br.com.cielo.libflue.util.dateUtils.getMonth
import br.com.cielo.libflue.util.dateUtils.getYear
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.commons.constants.BR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_THOUSAND
import br.com.mobicare.cielo.commons.constants.PT
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.COLON
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.meusrecebimentosnew.models.DayType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.absoluteValue

const val SIMPLE_DT_FORMAT_MASK = "dd/MM/yyyy"
const val SIMPLE_DT_BR_SINGLE_YEAR = "dd/MM/yy"
const val SIMPLE_DT_FORMAT_MASK_DAY_MONTH = "dd/MM"
const val SIMPLE_HOUR_MINUTE_24h = "HH:mm"
const val SIMPLE_HOUR_MINUTE_24h_CON = "HH:mm"
const val SIMPLE_HOUR_MINUTE_SECOND = "HH:mm:ss"
const val SIMPLE_DATE_INTERNATIONAL = "yyyy-MM-dd"
const val SIMPLE_HOUR_MINUTE = "hh:mm"
const val SIMPLE_DAY_OF_WEEK_MASK = "EEE"
const val SIMPLE_DAY_DESCRIPITION = "EEE, dd 'de' MMM 'de' yyyy"
const val FULL_DAY_DESCRIPITION = "EEE, dd 'de' MMMM 'de' yyyy"
const val SIMPLE_DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss"
const val SIMPLE_DATE_FORMAT_DATE_TIME = "dd/MM/yyyy - HH:mm"
const val SIMPLE_DATE_FORMAT_DATE_TIME_FULL = "dd/MM/yyyy HH:mm:ss"
const val SIMPLE_DATE_FORMAT_DATE_TIME_FULL_MINUS = "dd/MM/yyyy - HH:mm:ss"
const val DATE_FORMAT_CIELO_FACILITA = "yyyy-MM-dd"
const val SHORT_MONTH_DESCRIPTION = "MMM"
const val LONG_MONTH_DESCRIPTION = "MMMM"
const val LONG_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val LONG_TIME_NO_UTC = "yyyy-MM-dd'T'HH:mm:ss"
const val LONG_TIME_WITH_MILLIS_NO_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS"
const val COMPLETE_DATE_TIME = "EEE, dd MMM yyyy HH:mm:ss z"

const val FORMAT_TIME_CIELO_TAP = "MM/dd/yyyy HH:mm:ss"
const val SEPARATOR_SHORT_HOUR = "h"
const val DATE_FORMAT_PIX_TRANSACTION =
    "$SIMPLE_DT_FORMAT_MASK 'às' $SIMPLE_HOUR_MINUTE_SECOND"
const val EMPTY_VALUE = ""
const val DATE_TODAY_FORMAT_CALENDAR = "Hoje • %s"

const val ONE_MINUTE_MILLIS = 60000L
const val ONE_SECOND_MILLIS = 1000L
const val SECONDS_IN_MINUTE = 60L
const val MINUTES_IN_HOUR = 60L
const val HOURS_IN_DAY = 24L
const val ONE_LONG = 1L

enum class CompareDatesResults {
    INITIAL_DATE_HIGHER_THAN_FINAL_DATE,
    FINAL_DATE_HIGHER_THEN_INITIAL_DATE,
    EQUALS_DATES,
}

fun calculateNowMinusDays(minusDaysQuantity: Long): java.time.LocalDate {
    val today: java.time.LocalDate = java.time.LocalDate.now()
    val formatter =
        java.time.format.DateTimeFormatter
            .ofPattern(SIMPLE_DATE_INTERNATIONAL)
    today.format(formatter)
    return today.minusDays(minusDaysQuantity)
}

fun compareDates(
    initialDate: String,
    finalDate: String,
): CompareDatesResults {
    val formatter = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
    val d1: Date = formatter.parse(initialDate)
    val d2: Date = formatter.parse(finalDate)

    if (d1.after(d2)) {
        return CompareDatesResults.INITIAL_DATE_HIGHER_THAN_FINAL_DATE
    } else if (d1.before(d2)) {
        return CompareDatesResults.FINAL_DATE_HIGHER_THEN_INITIAL_DATE
    }

    return CompareDatesResults.EQUALS_DATES
}

fun CharSequence.validateDt(
    dtPattern: String = SIMPLE_DT_FORMAT_MASK,
    limitDate: Date? = null,
): Boolean {
    if (this.length < dtPattern.length) {
        return false
    }

    val dtFormat = SimpleDateFormat(dtPattern, Locale.getDefault())
    dtFormat.isLenient = false

    return try {
        val parsedDt = dtFormat.parse(this.toString())

        return if (limitDate != null) {
            parsedDt.after(Date())
        } else {
            true
        }
    } catch (e: Exception) {
        false
    }
}

val dtFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

/**
 * Dias a partir de this
 */
fun Date.daysFrom(daysCount: Int): String {
    val calInstance = Calendar.getInstance()
    calInstance.time = this
    calInstance.set(Calendar.DAY_OF_MONTH, calInstance.get(Calendar.DAY_OF_MONTH) - daysCount)
    return dtFormat.format(calInstance.time)
}

fun Date.daysFromToCalendar(daysCount: Int): Calendar {
    val calInstance = Calendar.getInstance()
    calInstance.time = this
    calInstance.set(Calendar.DAY_OF_MONTH, calInstance.get(Calendar.DAY_OF_MONTH) - daysCount)
    return calInstance
}

fun Date.format(): String = dtFormat.format(this)

fun Activity.currentDate(): String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

fun Date.currency(format: String = SIMPLE_DT_FORMAT_MASK): String {
    val formataData = SimpleDateFormat(format)
    return formataData.format(this)
}

fun Date.nextWeekday(day: Int): Calendar {
    var dayStop = true
    var daysCount = day.absoluteValue
    var dateTest: Calendar? = null

    while (dayStop) {
        dateTest = this.daysFromToCalendar(daysCount)

        if (!dateTest.isNotWeekend()) {
            daysCount--
        } else {
            dayStop = false
        }
    }

    return dateTest!!
}

fun String.parseToLocalDate(): LocalDate =
    LocalDate.parse(
        this,
        DateTimeFormatter.ofPattern(SIMPLE_DATE_INTERNATIONAL),
    )

fun LocalDate.parseToString(pattern: String = SIMPLE_DATE_INTERNATIONAL): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}

fun String.parseToLocalDateTime(pattern: String = LONG_TIME): LocalDateTime =
    LocalDateTime.parse(
        this,
        DateTimeFormatter.ofPattern(pattern),
    )

fun String?.parseToLocalDateTimeOrNull(pattern: String = LONG_TIME): LocalDateTime? =
    try {
        this?.parseToLocalDateTime(pattern)
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }

fun String.parseToLocalDatePT(): LocalDate =
    LocalDate.parse(
        this,
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
    )

fun String.formatDateToBrazilian(): String {
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    var outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
    var date: Date = inputFormat.parse(this)
    var outputDateStr: String = outputFormat.format(date)
    return outputDateStr
}

fun minusOneDayBasedOnDayType(
    date: String?,
    dayType: DayType.Type?,
): String? {
    val localDate = LocalDate.parse(date)
    var strDate = date
    if (dayType == DayType.Type.DAY) {
        val newDate = localDate.minusDays(ONE_LONG)
        strDate = newDate.toString()
    }
    return strDate
}

interface ClosedRange<T : Comparable<T>> {
    val start: T
    val endInclusive: T

    operator fun contains(value: T): Boolean = value in start..endInclusive

    fun isEmpty(): Boolean = start > endInclusive
}

class DateIterator(
    startDate: LocalDateTime,
    val endDateInclusive: LocalDateTime,
    val stepDays: Long,
) : Iterator<LocalDateTime> {
    private var currentDate = startDate

    override fun hasNext(): Boolean = currentDate <= endDateInclusive

    override fun next(): LocalDateTime {
        val next = currentDate
        currentDate = currentDate.plusDays(stepDays)
        return next
    }
}

class DateProgression(
    override val start: LocalDateTime,
    override val endInclusive: LocalDateTime,
    val stepDays: Long = 1,
) : Iterable<LocalDateTime>,
    ClosedRange<LocalDateTime> {
    override fun iterator(): Iterator<LocalDateTime> = DateIterator(start, endInclusive, stepDays)

    infix fun step(days: Long) = DateProgression(start, endInclusive, days)
}

operator fun LocalDateTime.rangeTo(other: LocalDateTime) = DateProgression(this, other)

fun String?.dateFormatToBr(): String {
    if (this.isNullOrEmpty()) return ""
    val date = this.parseToLocalDate()
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.monthValue.toString().padStart(2, '0')
    return "$day/$month/${date.year}"
}

fun String.isoDateWithMiliSecToBr(): String =
    try {
        val format = SimpleDateFormat(SIMPLE_DATE_FORMAT_ISO)
        val date = format.parse(this) as Date

        val simpleDateFormat = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK, Locale(PT, BR))
        simpleDateFormat.format(date)
    } catch (e: java.lang.Exception) {
        EMPTY
    }

fun String.isoDateToBrHourAndMinute(
    mask: String = SIMPLE_DATE_FORMAT_ISO,
    newFormat: String = SIMPLE_HOUR_MINUTE_24h,
): String =
    try {
        val format = SimpleDateFormat(mask)
        val date = format.parse(this) as Date

        val simpleDateFormat = SimpleDateFormat(newFormat, Locale(PT, BR))
        simpleDateFormat.format(date)
    } catch (e: java.lang.Exception) {
        EMPTY
    }

fun String.isoDateToBrHourAndMinuteCon(): String =
    try {
        val format = SimpleDateFormat(SIMPLE_HOUR_MINUTE_SECOND)
        val date = format.parse(this) as Date

        val simpleDateFormat = SimpleDateFormat(SIMPLE_HOUR_MINUTE_24h_CON, Locale(PT, BR))
        simpleDateFormat.format(date)
    } catch (e: java.lang.Exception) {
        EMPTY
    }

fun String.convertToCompleteDateTime(): Date? = SimpleDateFormat(COMPLETE_DATE_TIME, Locale.ENGLISH).parse(this)

fun getDateDiffInSeconds(
    firstDate: Date,
    secondDate: Date,
): Long {
    val diffInMillis = firstDate.time - secondDate.time
    return diffInMillis / ONE_THOUSAND
}

fun String.convertIsoDateToBr(): String =
    try {
        val dtStart = this
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date: Date = format.parse(dtStart)

        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        simpleDateFormat.format(date)
    } catch (e: java.lang.Exception) {
        ""
    }

fun String.dateYear(): String {
    if (this.isEmpty()) return ""
    val date = this.parseToLocalDate()
    return date.year.toString()
}

fun String.convertTimeStampToDate(): String {
    try {
        val sdfTimeStamp = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
        val sdfBrazilianDate = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK)
        val dateFromTimeStamp = sdfTimeStamp.parse(this)
        return sdfBrazilianDate.format(dateFromTimeStamp)
    } catch (e: Exception) {
        return ""
    }
}

fun getCurrentDateAndTime(): String {
    val currentDateTime = Date()
    val formatter = SimpleDateFormat(SIMPLE_DATE_FORMAT_DATE_TIME, Locale.getDefault())
    return formatter.format(currentDateTime)
}

fun getTimestampNow(): Long =
    LocalDateTime
        .now()
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

fun String.convertTimeStampToDateTime(): String = convertTimeStampToDateTime(false)

fun String.convertTimeStampToDateTime(full: Boolean): String {
    try {
        val sdfTimeStamp = SimpleDateFormat(SIMPLE_DATE_FORMAT_ISO)

        val sdfDateTimeBrazil =
            if (full) {
                SimpleDateFormat(SIMPLE_DATE_FORMAT_DATE_TIME_FULL)
            } else {
                SimpleDateFormat(SIMPLE_DATE_FORMAT_DATE_TIME)
            }

        val isoDateTime = sdfTimeStamp.parse(this)
        val dateTimeBrazil = sdfDateTimeBrazil.format(isoDateTime)
        return dateTimeBrazil
    } catch (e: Exception) {
        return ""
    }
}

fun LocalDateTime.toDate(): Date {
    val instant = this.atZone(ZoneId.systemDefault()).toInstant()
    return Date(instant.toEpochMilli())
}

fun Date.toLocalDate(): LocalDate {
    val currentCal = Calendar.getInstance()
    currentCal.time = this

    return LocalDate.of(
        currentCal.get(Calendar.YEAR) + 1900,
        currentCal.get(Calendar.MONTH) + 1,
        currentCal.get(Calendar.DAY_OF_MONTH),
    )
}

fun Date.justDate(): Date {
    val now = Calendar.getInstance().time
    val date = dtFormat.parse(dtFormat.format(now))
    return date
}

fun LocalDate.toDate(): Date =
    Date(
        this
            .atTime(0, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
    )

fun Date.toLocalDateTime(): LocalDateTime {
    var tempCal = Calendar.getInstance()
    tempCal.time = this
    val instant = Instant.ofEpochMilli(tempCal.timeInMillis)
    return instant
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun String.convertToBrDateFormat(inputFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME): String =
    try {
        val outputFormatter = DateTimeFormatter.ofPattern(SIMPLE_DT_FORMAT_MASK)
        val dateTime = inputFormatter.parse(this)

        outputFormatter.format(dateTime)
    } catch (e: Exception) {
        e.printStackTrace()
        EMPTY
    }

fun LocalDate.convertToBrDateFormat(pattern: String = SIMPLE_DT_FORMAT_MASK): String =
    try {
        val outputFormatter = DateTimeFormatter.ofPattern(pattern)
        format(outputFormatter)
    } catch (e: Exception) {
        e.printStackTrace()
        EMPTY
    }

fun LocalDateTime.convertToBrDateFormat(): String =
    try {
        val outputFormatter = DateTimeFormatter.ofPattern(SIMPLE_DT_FORMAT_MASK)
        format(outputFormatter)
    } catch (e: Exception) {
        e.printStackTrace()
        EMPTY
    }

fun String.covertInternationalDateToBRDateAndMinutes(): String =
    try {
        val inputFormat = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
        val sdfDateTimeBrazil = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK)
        val isoDateTime = inputFormat.parse(this)
        val dateTimeBrazil = sdfDateTimeBrazil.format(isoDateTime)
        dateTimeBrazil
    } catch (e: Exception) {
        EMPTY
    }

fun String.calendarDate(): Calendar =
    try {
        val date = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
        date.parse(this)
        date.calendar
    } catch (e: Exception) {
        Calendar.getInstance()
    }

fun String.dateInternationalFormat(): String? =
    try {
        val brazilianDate = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK)
        val internationalDate = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
        val dateFromTimeStamp = brazilianDate.parse(this)
        internationalDate.format(dateFromTimeStamp)
    } catch (e: Exception) {
        null
    }

fun String?.dateFormatToBrSubString(): String {
    if (this.isNullOrEmpty()) return ""

    val dateSub = this.substring(0, 10)
    val date = dateSub.parseToLocalDate()
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.monthValue.toString().padStart(2, '0')
    return "$day/$month/${date.year}"
}

fun String?.dateFormatToPutSubString(): String {
    if (this.isNullOrEmpty()) return ""

    val dateSub = this.substring(0, 10)
    val date = dateSub.parseToLocalDatePT()
    return date.toString()
}

@SuppressLint("SimpleDateFormat")
fun String.getBrazilianDayAndExtensiveMonth(): Pair<Int, String> {
    val localeBrazil = Locale("pt", "BR")
    val sdf = SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL)
    val sdfPtBR = SimpleDateFormat(SHORT_MONTH_DESCRIPTION, localeBrazil)
    val calendar = Calendar.getInstance()
    calendar.time = sdf.parse(this) as Date
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    val brMonth = sdfPtBR.format(calendar.time)
    return Pair(dayOfMonth, brMonth)
}

fun Calendar?.getDateInTheFuture(years: Int = ONE): String? {
    this?.time = Date()
    this?.add(Calendar.YEAR, years)
    return this?.time?.format()
}

fun Calendar?.getDateInThePast(years: Int = ONE): String? {
    this?.time = Date()
    this?.add(Calendar.YEAR, -years)
    return this?.time?.format()
}

fun Calendar?.getDateInThePastMonth(month: Int = ONE): String? {
    this?.time = Date()
    this?.add(Calendar.MONTH, -month)
    return this?.time?.format()
}

fun String?.hourMinuteToBrFormat() = this?.replace(COLON, SEPARATOR_SHORT_HOUR)

fun String?.hourMinuteToGenericFormat(): String? =
    this?.length?.let {
        if (it < TWO) {
            "0$this:00"
        } else {
            "$this:00"
        }
    }

class LocaleUtil {
    companion object {
        fun getMonthLongName(
            dateString: String?,
            format: String = SIMPLE_DATE_INTERNATIONAL,
            nameStyle: Int = Calendar.LONG,
        ): String? {
            val locale = Locale("pt", "BR")
            return SimpleDateFormat(format, locale).parse(dateString)?.let {
                val calendar = Calendar.getInstance(locale)
                calendar.time = it
                calendar
                    .getDisplayName(
                        Calendar.MONTH,
                        nameStyle,
                        locale,
                    )?.capitalize()
            }
        }

        fun currentDate(): String {
            return SimpleDateFormat(SIMPLE_DATE_INTERNATIONAL, Locale.getDefault()).format(Date())
        }
    }
}

fun Long?.toSeconds(): Int =
    this?.let {
        ((this / ONE_SECOND_MILLIS) % SECONDS_IN_MINUTE).toInt()
    } ?: 0

fun Long?.toMinutes(): Int =
    this?.let {
        ((this / ONE_MINUTE_MILLIS) % MINUTES_IN_HOUR).toInt()
    } ?: 0

fun Long?.toHours(): Int =
    this?.let {
        ((this / (ONE_MINUTE_MILLIS * MINUTES_IN_HOUR)) % HOURS_IN_DAY).toInt()
    } ?: 0

fun String.formatDateFromYYMMDD(): String {
    val inputFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = inputFormat.parse(this)
    return outputFormat.format(date)
}

fun String?.parseToLocalDateTimeNoUTC(): LocalDateTime? =
    try {
        this?.let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            )
        }
    } catch (e: Exception) {
        e.message?.logFirebaseCrashlytics()
        null
    }

fun LocalDateTime?.isToday(): Boolean {
    return this?.let {
        val currentDate = LocalDateTime.now()
        return it.toLocalDate() == currentDate.toLocalDate()
    } ?: false
}

fun LocalDateTime?.toStringDayAndMonthOrTimeOfDay(): String =
    this?.let {
        it.format(
            DateTimeFormatter.ofPattern(
                if (it.isToday()) {
                    SIMPLE_HOUR_MINUTE_24h
                } else {
                    SIMPLE_DT_FORMAT_MASK_DAY_MONTH
                },
            ),
        )
    } ?: EMPTY

fun Calendar.toLocalDate(): LocalDate =
    LocalDate.of(
        this.getYear(),
        this.getMonth() + ONE,
        this.getDayOfMonth(),
    )

fun LocalDate.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.set(
        this.year,
        this.monthValue - ONE,
        this.dayOfMonth,
    )
    return calendar
}

fun String?.toCalendar(pattern: String): Calendar? =
    this?.let {
        try {
            SimpleDateFormat(pattern, Locale.getDefault()).parse(it)?.let { date ->
                Calendar.getInstance(TimeZone.getTimeZone(ZONE_ID_UTC)).apply {
                    time = date
                }
            }
        } catch (e: Exception) {
            e.message?.logFirebaseCrashlytics()
            null
        }
    }

fun LocalDate?.isToday() = this == LocalDate.now()

fun Calendar?.isToday() = this?.toLocalDate()?.isToday() ?: false

fun getNumberOfUnitBetweenDates(
    startDate: LocalDate?,
    endDate: LocalDate?,
    unit: ChronoUnit,
): Long? =
    if (startDate != null && endDate != null && endDate.isBefore(startDate).not()) {
        unit.between(startDate, endDate)
    } else {
        null
    }

fun getNumberOfDaysBetweenDates(
    startDate: LocalDate?,
    endDate: LocalDate?,
): Long? = getNumberOfUnitBetweenDates(startDate, endDate, ChronoUnit.DAYS)

fun getNumberOfWeeksBetweenDates(
    startDate: LocalDate?,
    endDate: LocalDate?,
): Long? = getNumberOfUnitBetweenDates(startDate, endDate, ChronoUnit.WEEKS)

fun getNumberOfMonthsBetweenDates(
    startDate: LocalDate?,
    endDate: LocalDate?,
): Long? = getNumberOfUnitBetweenDates(startDate, endDate, ChronoUnit.MONTHS)

fun Calendar.toStringWithTodayCondition(pattern: String): String =
    if (this.isToday()) {
        String.format(DATE_TODAY_FORMAT_CALENDAR, this.toString(pattern))
    } else {
        this.toString(pattern)
    }
