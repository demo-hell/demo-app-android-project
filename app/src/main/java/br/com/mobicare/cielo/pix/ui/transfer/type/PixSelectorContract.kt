package br.com.mobicare.cielo.pix.ui.transfer.type

import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum

interface PixSelectorContract {

    interface View {
        fun onSelectedKeyType(keyType: PixKeyTypeEnum)
    }

    interface Result {
        fun onShowKeyTypeSelected(keyType: PixKeyTypeEnum)
    }
}