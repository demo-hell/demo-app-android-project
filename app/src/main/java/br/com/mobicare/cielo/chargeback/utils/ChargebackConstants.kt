package br.com.mobicare.cielo.chargeback.utils

object ChargebackConstants {
    const val PENDING_TAB = 0
    const val TREATED_TAB = 1
    const val REFUSE = "RECUSADO"
    const val ACCEPT = "ACEITO"
    const val OUT_OF_TIME = "FORA DO PRAZO"
    const val DEFAULT_EMPTY_VALUE = "-"
    const val PENDING = "PENDING"
    const val DONE = "DONE"
    const val TREATED = "TREATED"
    const val RECEPTION_DATE = "receptionDate"
    const val TREATMENT_DEADLINE = "treatmentDeadline"
    const val DESC = "desc"
    const val ASC = "asc"
    const val BROKE = "\\s+"
    const val SPACE = " "

    const val IS_SHOW_TREATED_ARGS = "IS_SHOW_TREATED_ARGS"

    object ChargebackListParams {
        const val DEFAULT_ORDER_BY = "treatmentDeadline"
        const val DEFAULT_ORDER = "desc"
    }
}
