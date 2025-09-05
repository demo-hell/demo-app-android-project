package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import br.com.mobicare.cielo.commons.presentation.BaseView

class IDOnboardingUpdateForeignPhoneContract {
    interface View : BaseView {
        fun successSendForeignCellphone()
    }

    interface Presenter {
        fun sendForeignCellphone(phone: String)
        fun onPause()
        fun onResume()
    }
}