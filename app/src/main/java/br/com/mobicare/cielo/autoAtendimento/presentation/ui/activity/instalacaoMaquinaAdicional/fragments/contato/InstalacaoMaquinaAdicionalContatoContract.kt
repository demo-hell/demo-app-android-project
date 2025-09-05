package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato

import br.com.mobicare.cielo.commons.ui.IAttached

interface InstalacaoMaquinaAdicionalContatoContract {

    interface View : IAttached {
        fun onShowNameError(errorMessage: String?)
        fun onShowPhoneNumberError(errorMessage: String?)
        fun goToNextScreen(nome: String, numeroTelefone: String)
        fun setPersonData(nome: String, numeroTelefone: String)
    }

    interface Presenter {
        fun setData(nome: String, numeroTelefone: String)
        fun onNextButtonClicked(nome: String, numeroTelefone: String)
    }

}