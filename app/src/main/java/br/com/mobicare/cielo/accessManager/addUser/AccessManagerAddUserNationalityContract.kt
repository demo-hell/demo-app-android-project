package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerCountries
import br.com.mobicare.cielo.accessManager.addUser.model.Country
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAddUserNationalityContract {
    interface View: BaseView {
        fun showSuccess(result: MutableList<Country>)
        fun showError()
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun retry()
        fun getCountries()
    }
}