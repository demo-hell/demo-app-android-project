package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixScheduleFrequencyTime {
    ONE,
    DIARY,
    WEEKLY,
    MONTHLY,
    ;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}
