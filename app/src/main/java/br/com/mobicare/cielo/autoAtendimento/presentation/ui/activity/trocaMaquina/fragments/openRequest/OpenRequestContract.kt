package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest

import br.com.mobicare.cielo.autoAtendimento.domain.model.EstablishmentSelectedObj
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.LoginObj

class OpenRequestContract {
    interface View : BaseView{
        fun showEstablishment(establishment: EstabelecimentoObj)
        fun onNextStep(establishment: EstablishmentSelectedObj)
    }

    interface Presenter {
        fun loadEstablishment(loginObj: LoginObj?)
        fun onNextButtonClicked()
    }
}