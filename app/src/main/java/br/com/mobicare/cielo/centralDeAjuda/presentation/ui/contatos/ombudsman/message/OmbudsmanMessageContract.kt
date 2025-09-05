package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanResponse
import br.com.mobicare.cielo.commons.presentation.BaseView

interface OmbudsmanMessageContract {
    interface View : BaseView {
        fun onSuccess(response: OmbudsmanResponse)
    }

    interface Presenter {
        fun onSendProtocol(ombudsmanRequest: OmbudsmanRequest?,
            subject: String?,
            protocol: String?,
            message: String?)
        fun onResume()
        fun onPause()
    }
}