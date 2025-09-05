package br.com.mobicare.cielo.esqueciSenha.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword

interface EsqueciSenhaDigitalContract {

    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showError(error: ErrorMessage)
        fun changeActivity()
        fun showErrorTapume(error: ErrorMessage)
        fun hideError()
        fun showSuccess(response: String)
        fun onErrorNotBooting()
    }

    interface Presenter {
        fun resetPassword(data: RecoveryPassword)
    }
}
