package br.com.mobicare.cielo.home.presentation.main

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached

interface HomeContract {

    interface View : IAttached {
        fun showLoading() {}
        fun hideLoading() {}
        fun showError(error: ErrorMessage) {}
        fun logout(msg: ErrorMessage) {}

        fun hideContent() {}
        fun showContent() {}
        fun hideError() {}
    }

    interface Presenter {
        fun callAPI()

    }
}