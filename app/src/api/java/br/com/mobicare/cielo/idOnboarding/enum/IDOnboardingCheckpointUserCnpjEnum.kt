package br.com.mobicare.cielo.idOnboarding.enum

enum class IDOCheckpointUserCnpj(val code: Int) {
    NONE(0),
    P1_VALIDATED(100),
    USER_CNPJ_CHECKED(200),
    P2_VALIDATED(300);

    companion object {
        fun fromCode(code: Int?) = values().firstOrNull{ it.code == code } ?: NONE
    }
}