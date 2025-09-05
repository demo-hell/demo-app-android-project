package br.com.mobicare.cielo.accessManager.invite.receive.domain

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

enum class InviteErrors(vararg val errorCodes: String) {
    PASSWORD(
        "TOO_MANY_OCCURRENCES", "PALINDROME_NUMBER", "REPEAT_OF_TENS",
        "INVALID_PASSWORD", "ILLEGAL_NUMERICAL_SEQUENCE"
    ),
    INVITE_EXPIRED("INVITE_EXPIRED"),
    INVALID_CPF("INVALID_CPF"),
    ERROR_CODE_INVALID_CPF("ERROR_CODE_INVALID_CPF"),
    CPF_NAME_MAX_TRIES_EXCEEDED("CPF_NAME_MAX_TRIES_EXCEEDED"),
    ERROR_NOT_BOOTING("ERROR_NOT_BOOTING"),
    GENERIC;


    companion object {
        fun valueOf(errorMessage: ErrorMessage): InviteErrors = values().find { inviteErrors ->
            inviteErrors.errorCodes.any { errorCode ->
                errorCode in errorMessage.errorCode + errorMessage.listErrorServer.map { it.errorCode }
            }
        } ?: GENERIC
    }
}