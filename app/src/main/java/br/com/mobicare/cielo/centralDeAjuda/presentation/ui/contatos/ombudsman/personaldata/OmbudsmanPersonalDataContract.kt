package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.commons.presentation.BaseView

interface OmbudsmanPersonalDataContract {
    interface View : BaseView {
        fun onShowPersonalData(ombudsman: OmbudsmanRequest?)
    }

    interface Presenter {
        fun onLoadPersonalData()
        fun onCreateObject(userName: String?,
                           ec: String?,
                           email: String?,
                           phone: String?): OmbudsmanRequest
    }
}