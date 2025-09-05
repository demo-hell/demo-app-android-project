package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAddUserCpfContract {
    interface View: BaseView {
        override fun showSuccess(result: Any)
        fun showErrorLabel(error: ErrorMessage?, retryCallback: (() -> Unit)?)
        fun showErrorGeneric(error: ErrorMessage?)
        fun onErrorCpfDuplicated()
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun retry()
        fun validateCpf(cpf: String?)
    }
}