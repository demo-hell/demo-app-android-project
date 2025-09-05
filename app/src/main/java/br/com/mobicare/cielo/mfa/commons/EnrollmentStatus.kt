package br.com.mobicare.cielo.mfa.commons

enum class EnrollmentStatus(val status: String) {
    NOT_ACTIVE("NOT_ACTIVE"),
    ACTIVE("ACTIVE"),
    WAITING_ACTIVATION("WAITING_ACTIVATION"),
    NOT_ELIGIBLE("NOT_ELIGIBLE"),
    PENDING("PENDING"),
    BLOCKED("BLOCKED"),
    EXPIRED("EXPIRED"),
    UNKNOWN("UNKNOWN"),
    ERROR_PENNY_DROP("ERROR_PENNY_DROP"),
    PENNY_DROP_TEMPORARILY_BLOCKED("PENNY_DROP_TEMPORARILY_BLOCKED"),
    NOT_MIGRATED("NOT_MIGRATED");

    companion object {
        fun fromString(value: String) = values().find {
            it.status == value
        }
    }

}