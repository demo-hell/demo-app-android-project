package br.com.mobicare.cielo.pix.ui.qrCode.receivable

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE

interface PixAmountReceivableContract {
    fun onAmount(amount: Double = ZERO_DOUBLE)
}