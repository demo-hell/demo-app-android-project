package br.com.mobicare.cielo.pix.ui.qrCode.charge.key

import br.com.mobicare.cielo.pix.domain.MyKey

interface PixQRCodeMyKeysAdapterListener {
    fun handleClick(key: MyKey)
}