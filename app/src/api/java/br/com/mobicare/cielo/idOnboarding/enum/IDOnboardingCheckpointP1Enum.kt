package br.com.mobicare.cielo.idOnboarding.enum

enum class IDOCheckpointP1(val code: Int) {
    NONE(0),
    CPF_NAME_VALIDATED(100),
    EMAIL_VALIDATION_STARTED(200),
    EMAIL_VALIDATION_CONFIRM(300),
    CELLPHONE_VALIDATION_STARTED(400),
    CELLPHONE_VALIDATION_CONFIRM(500),
    POLICY_1_REQUESTED(600),
    POLICY_1_RESPONSE(700),;

    companion object {
        fun fromCode(code: Int?) = values().firstOrNull{ it.code == code } ?: NONE
    }
}