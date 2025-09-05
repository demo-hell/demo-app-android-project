package br.com.mobicare.cielo.mfa.commons

import br.com.mobicare.cielo.R

enum class MerchantStatusMFA(val color: Int?) {

    WAITING_ACTIVATION(R.color.color_f98f25),
    BLOCKED(R.color.red_EE2737),
    NOT_ACTIVE(null),
    ACTIVE(null),
    EXPIRED(null),
    PENDING(null),
    NOT_ELIGIBLE(null),
    ERROR_PENNY_DROP(null),
    PENNY_DROP_TEMPORARILY_BLOCKED(null)
}