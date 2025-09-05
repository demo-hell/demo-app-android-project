package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import java.util.Calendar

data class PixTransferStore(
    val amount: Double = ZERO_DOUBLE,
    val message: String? = null,
    val schedulingDate: Calendar? = null,
    val recurrenceData: PixRecurrenceData = PixRecurrenceData(),
) {
    fun validateAmount(balance: Double? = null) = amount > ZERO_DOUBLE && balance?.let { amount <= it } ?: true
}
