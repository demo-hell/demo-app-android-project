package br.com.mobicare.cielo.accessManager.home

import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerHomeContract {
    interface View : BaseView {
        fun showNoRoleUsers(noRoleUsers: List<AccessManagerUser>?)
        fun showAdminUsers(adminUsers: List<AccessManagerUser>?)
        fun showReaderUsers(readerUsers: List<AccessManagerUser>?)
        fun showAnalystUsers(readerUsers: List<AccessManagerUser>?)
        fun showForeignUsers(foreignUsers: List<ForeignUsersItem>?)
        fun showExpiredInvitation(numberExpiredInvitations: Int)
        fun showTechnicalUsers(technicalUsers: List<AccessManagerUser>?)
        fun hideTechnicalUsers()
        fun showCustomUsers(customUsers: List<AccessManagerUser>?)
        fun hideExpiredInvitation()
        fun hideForeignUsers()
        fun showCardCustomProfile()
        fun getCustomUsers(customProfileEnabled: Boolean)
    }

    interface Presenter {
        fun retry()
        fun getExpiredInvites()
        fun getForeignUsers()
        fun getCustomerSettings()
        fun getForeignFlowAllowed(): Boolean?
        fun getCustomProfileEnabled(): Boolean?
        fun onPause()
        fun onResume()
    }
}