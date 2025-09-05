package br.com.mobicare.cielo.pix.ui.transfer.key

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum

interface PixInsertKeyContract {

    interface View : BaseView {
        fun onValidKey(response: ValidateKeyResponse)
        fun onErrorInput(error: ErrorMessage)
    }

    interface Presenter {
        fun onValidateKey(key: String, type: PixKeyTypeEnum?)
        fun onResume()
        fun onPause()
    }
}