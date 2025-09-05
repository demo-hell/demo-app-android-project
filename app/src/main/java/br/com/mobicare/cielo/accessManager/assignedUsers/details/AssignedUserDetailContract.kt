package br.com.mobicare.cielo.accessManager.assignedUsers.details

import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AssignedUserDetailContract {
    interface Presenter {
        fun assignRole(userId: String, role: String, otpCode: String)
        fun getUsername(): String?
        fun getDocument(): String?
        fun isCnpj(): Boolean
        fun getCustomActiveProfiles(customProfileEnabled: Boolean)
        fun checkTechnicalToogle()
        fun onResume()
        fun onPause()
    }
    interface View : BaseView {
        fun onRoleAssigned()
        fun showCustomProfiles(customProfiles: List<AccessManagerCustomProfileResponse>)
        fun showErrorProfile()
        fun hideTechnicalUser()
    }
}