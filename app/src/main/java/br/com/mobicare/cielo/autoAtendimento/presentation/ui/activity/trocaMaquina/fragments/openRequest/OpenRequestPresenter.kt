package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest

import br.com.mobicare.cielo.autoAtendimento.domain.model.EstablishmentSelectedObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.LoginObj

class OpenRequestPresenter(
        private val mView: OpenRequestContract.View) : OpenRequestContract.Presenter {

    var establishment: EstabelecimentoObj? = null

    //region OpenRequestContract.Presenter

    override fun onNextButtonClicked() {
        establishment?.let {
            mView.onNextStep(EstablishmentSelectedObj(it.ec, it.tradeName, it.cnpj))
        }
    }

    override fun loadEstablishment(loginObj: LoginObj?) {
        loginObj?.establishment?.let {
            establishment = it
            mView.showEstablishment(it)
        } ?: kotlin.run {
            establishment = null
            mView.logout(ErrorMessage())
        }
    }

    //endregion

}