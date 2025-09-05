package br.com.mobicare.cielo.pix.ui.qrCode.identifier

import br.com.mobicare.cielo.pix.constants.EMPTY

interface PixIdentifierContract {
    fun onIdentifier(identifier: String = EMPTY)
}