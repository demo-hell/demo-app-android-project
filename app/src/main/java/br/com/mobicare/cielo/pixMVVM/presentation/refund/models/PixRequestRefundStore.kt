package br.com.mobicare.cielo.pixMVVM.presentation.refund.models

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE

data class PixRequestRefundStore(
    val amount: Double = ZERO_DOUBLE,
    val message: String? = null
) {
    fun validateAmount(availableAmountToRefund: Double, balance: Double? = null) =
        amount > ZERO_DOUBLE
                && amount <= availableAmountToRefund
                && balance?.let { amount <= it } ?: true
}
