package br.com.mobicare.cielo.suporteTecnico.data


data class ScheduleDataResponse(
    val availabilityList: List<Availability>
)

data class Availability(
    val code: String,
    val initialWeekDay: String,
    val finalWeekDay: String,
    val initialHour: Int,
    val finalHour: Int
)

