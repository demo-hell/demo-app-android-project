package br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler

enum class ActionErrorTypeEnum {
    HTTP_ERROR,
    MFA_TOKEN_ERROR_ACTION,
    WITHOUT_ACCESS_UPDATE_INFO,
    WITHOUT_ACCESS_CHECK_MAIN_ROLE,
    LOGOUT_NEEDED_ERROR,
    NETWORK_ERROR,
    ELIGIBLE_ERROR,
}