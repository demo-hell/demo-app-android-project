package br.com.mobicare.cielo.commons.helpers

import br.com.mobicare.cielo.machine.domain.Availability

class AvailabilityHelper {
    companion object {

        fun formatDays(availability: Availability): String {
            return "${getShortDay(availability.initialWeekDay)} a ${getShortDay(availability.finalWeekDay)}"
        }

        fun getShortDay(day: String): String {
            return DateTimeHelper.convertWeekDayToPortuguese(day).substring(0, 3)
        }

        fun formatTime(availability: Availability) : String {
            return "${getFormatedTime(availability.initialHour)} às ${getFormatedTime(availability.finalHour)}"
        }

        fun getFormatedTime(time: Int) : String {
            return "${DateTimeHelper.formatTime(time)}:00"
        }

    }
}