package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.received

import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum

interface PixKeyReceivedClaimContract {

    interface View {
        fun keepKey(key: MyKey)
        fun releaseKey(key: MyKey)
        fun ownershipValidation(key: MyKey, keyType: PixKeyTypeEnum)
    }
}