package br.com.mobicare.cielo.pagamentoLink.orders.orderdetail

import br.com.mobicare.cielo.R

enum class StatusPay(val color: Int = R.color.colorStatusPayPaid) {

    PENDING(R.color.colorStatusPayPending),
    PAID(R.color.colorStatusPayPaid),
    DENIED(R.color.colorStatusPayDenied),
    EXPIRED(R.color.colorStatusPayExpired),
    VOIDED(R.color.colorStatusPayVoided),
    NOT_FINALIZED(R.color.colorStatusPayNotFinalized),
    CHARGEBACK(R.color.colorStatusPayChargeback),
    AUTHORIZED(R.color.colorStatusPayAuthorized),
    UNDEFINED(R.color.colorStatusPayUndefined)
}