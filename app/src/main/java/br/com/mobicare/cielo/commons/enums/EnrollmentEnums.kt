package br.com.mobicare.cielo.commons.enums

enum class EnrollmentType(val type: String) {
    ENROLLMENT("ENROLLMENT"),
    CHALLENGE("CHALLENGE"),
    ERROR_PENNY_DROP("ERROR_PENNY_DROP");

    companion object {
        fun fromString(value: String) = values().find {
            it.type == value
        }
    }

}