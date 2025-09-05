package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.rules

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod

class ShouldEmptyBalanceRule(currentBalance: Double?) {

    private val hasBalanceOnAccount = currentBalance?.let { it > ZERO_DOUBLE } ?: false

    operator fun invoke(activeReceiptMethod: PixReceiptMethod) =
        activeReceiptMethod == PixReceiptMethod.CIELO_ACCOUNT && hasBalanceOnAccount

}