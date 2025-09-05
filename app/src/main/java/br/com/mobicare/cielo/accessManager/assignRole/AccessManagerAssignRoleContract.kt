package br.com.mobicare.cielo.accessManager.assignRole

import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAssignRoleContract {
    interface View: BaseView {
        fun roleAssigned(userCount: Int, role: String)
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun retry()
    }
}