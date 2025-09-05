package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAddUserEmailContract {
    interface View: BaseView {
        override fun showSuccess(result: Any)
        override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?)
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun retry()
        fun validateEmail(cpf: String?, email: String?, foreign: Boolean)
    }
}