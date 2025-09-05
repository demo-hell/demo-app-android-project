package br.com.mobicare.cielo.pix.enums

enum class PixTimeManagementEnum(val time: String, val hour: Int, val displayHour: String) {
    EIGHT(time = "20:00:00", 20, "20h"),
    TEN(time = "22:00:00", 22, "22h");

    companion object {
        fun findByTime(time: String?) = values().find { it.time == time } ?: TEN
    }
}