package br.com.mobicare.cielo.pix.ui.transfer.message

import br.com.mobicare.cielo.pix.constants.EMPTY

interface PixEnterTransferMessageContract {
    fun onMessage(message: String = EMPTY)
}