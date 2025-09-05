package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.commons.enums.DaysOfWeek
import br.com.mobicare.cielo.commons.enums.DiasDaSemana
import java.text.SimpleDateFormat
import java.util.*

class DateTimeHelper {
    companion object {
        fun convertWeekDayToPortuguese(day: String) =
                when(day) {
                    DaysOfWeek.SUNDAY.day -> DiasDaSemana.DOMINGO.dia
                    DaysOfWeek.MONDAY.day -> DiasDaSemana.SEGUNDA.dia
                    DaysOfWeek.TUESDAY.day -> DiasDaSemana.TERCA.dia
                    DaysOfWeek.WEDNESDAY.day -> DiasDaSemana.QUARTA.dia
                    DaysOfWeek.THURSDAY.day -> DiasDaSemana.QUINTA.dia
                    DaysOfWeek.FRIDAY.day -> DiasDaSemana.SEXTA.dia
                    else -> DiasDaSemana.SABADO.dia
                }

        fun convertWeekDayToEnglish(day: String) =
            when(DiasDaSemana.fromString(day)) {
                DiasDaSemana.DOMINGO -> DaysOfWeek.SUNDAY.day
                DiasDaSemana.SEGUNDA -> DaysOfWeek.MONDAY.day
                DiasDaSemana.TERCA -> DaysOfWeek.TUESDAY.day
                DiasDaSemana.QUARTA -> DaysOfWeek.WEDNESDAY.day
                DiasDaSemana.QUINTA -> DaysOfWeek.THURSDAY.day
               DiasDaSemana.SEXTA -> DaysOfWeek.FRIDAY.day
                else -> DaysOfWeek.SATURDAY.day
            }

        fun formatTime(time: Int) =
            String.format("%02d", time)

        fun decreaseDateByNumberDays(date: Date, days: Int) : Date {
            val endDate = Calendar.getInstance().time
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, days * -1)
            return cal.time
        }

        private fun formatDateByMask(date: Date, mask: String = "dd/MM/yyyy") : String {
            val sdf = SimpleDateFormat(mask)
            var dateFormatted = ""
            try {
                dateFormatted = sdf.format(date)
            }
            catch (error: Throwable) {

            }
            return dateFormatted
        }

        fun toDate(date: String, mask: String) : Date? {
            val sdf = SimpleDateFormat(mask)
            var dateConverted: Date? = null
            try {
                dateConverted = sdf.parse(date)
            }
            catch (error: Throwable) {

            }
            return dateConverted
        }

        fun convertToDate(date: String?, sourceMask: String, destMask: String) : String {
            date?.let { itDate ->
                toDate(itDate, sourceMask)?.let {
                    formatDateByMask(it, destMask)?.let { itFormattedDate ->
                        return itFormattedDate
                    }
                }
            }
            return ""
        }
    }
}