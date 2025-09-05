package br.com.mobicare.cielo.openFinance.utils

object OpenFinanceConstants {
    const val PIX_URL = "https://www.cielo.com.br/pix/"
    const val INTENT_ID = "intent_id"
    const val REDIRECT_URI = "redirect_uri"
    const val APPROVED_PAYMENT = "AUTHORISED"
    const val REJECTED_PAYMENT = "REJECTED"
    const val EXPIRED_CONSENT = "EXPIRED_CONSENT"
    const val V2 = "v2"
    const val V3 = "v3"
    const val REJECTED_USER = "REJEITADO_USUARIO"
    const val REJECTED_USER_DETAIL = "O usuário rejeitou a autorização do consentimento"
    const val INSUFFICIENT_FUNDS = "SALDO_INSUFICIENTE"
    const val INSUFFICIENT_FUNDS_DETAIL =
        "O usuário não tem saldo suficiente para realizar essa operação"
    const val RECEIVED_TAB = 0
    const val SENT_TAB = 1
    const val RECEIVING_JOURNEY = "RECEIVING"
    const val TRANSMITTING_JOURNEY = "TRANSMITTING"
    const val CITIZEN_PORTAL = "https://openfinancebrasil.org.br/conheca-o-open-finance/"
    const val SVG_EXT = ".svg"
    const val BRAND_SELECTED = "brandSelected"
    const val CITIZEN_PORTAL_BRANDS = "https://openfinancebrasil.org.br/quem-participa/"
    const val ACCOUNT_TYPE = "ACCOUNT"
    const val CUSTOMER_TYPE = "CUSTOMER"
    const val TYPE_DAYS = "DAYS"
    const val TYPE_MONTHS = "MONTHS"
    const val TYPE_YEARS = "YEARS"
    const val CODE = "code"
    const val STATE = "state"
    const val ID_TOKEN = "id_token"
    const val ERROR_DESCRIPTION = "error_description"
    var isSharedDataFragmentActive = false
    const val FLOW_RECEIVER = "RECEIVING"
    const val CHANGE_SHARE = "ALTERACAO"
    const val RENEW_SHARE = "RENOVACAO"
    const val TYPE_SHARE = "typeShare"
}