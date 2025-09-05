package br.com.mobicare.cielo.biometricToken.presentation.home

import br.com.mobicare.cielo.commons.presentation.BaseView

class BiometricTokenHomeContract {
    interface View : BaseView {
        fun setupView(name: String)
    }

    interface Presenter {
        fun getUserName()
    }
}