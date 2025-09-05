package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum

interface PixInsertKeyToRegisterContract {

    interface View : BaseView {
        fun onSuccessSendCode()
    }

    interface Presenter {
        fun onSendValidationCode(key: String?, type: PixKeyTypeEnum?)
        fun onResume()
        fun onPause()
    }
}