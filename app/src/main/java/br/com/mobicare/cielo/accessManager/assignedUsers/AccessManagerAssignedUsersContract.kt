package br.com.mobicare.cielo.accessManager.assignedUsers

import br.com.mobicare.cielo.accessManager.UnlinkUserReason
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAssignedUsersContract {
    interface Listener {
        fun showErrorProfile() {}
        fun showErrorEmptyProfiles(userSelected: AccessManagerUser) {}
        fun openDetailsBottomSheet(
            userSelected: AccessManagerUser,
            profileIdSelected: String,
            profileNameSelected: String) {}
        fun openSelectCustomProfile(item: AccessManagerUser) {}
        fun onRemoveClicked(item: AccessManagerUser) {}
        fun onUserProfileTypeUpdated(userId: String) {}
    }

    interface Presenter {
        fun unlinkUser(userId: String, reason: UnlinkUserReason, otpCode: String)
        fun canUserBeRemoved(userId: String): Boolean
        fun getUsername(): String
        fun onResume()
        fun onPause()
    }
    interface View : BaseView {
        fun onRemoveConfirmed(userId: String, reason: UnlinkUserReason)
        fun unlinkUser(isAnimation: Boolean)
        fun onUserUnlinked(userId: String)
        fun setUserList(userList: List<AccessManagerUser>)
    }
}
