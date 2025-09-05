package br.com.mobicare.cielo.accessManager.expired

import br.com.mobicare.cielo.accessManager.model.AccessManagerExpiredInviteResponse
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerExpiredInvitationContract {

    interface View : BaseView {
        fun onSuccessResendInvite(invitations: Int)
        fun onSuccessDeleteInvite(invitations: Int)
        fun onUserSelected(user: Item)
        fun onShowExpiredInvites(
            expiredInviteResponse: AccessManagerExpiredInviteResponse,
            isUpdate: Boolean
        )

        fun onErrorGetExpiredInvites(errorMessage: ErrorMessage?)
    }

    interface Presenter {
        fun getExpiredInvites(isLoading: Boolean, pageNumber: Int)
        fun onResendInvite(users: List<Item>, otpCode: String)
        fun onDeleteInvite(users: List<Item>, otpCode: String)
        fun resetPagination()

        fun onPause()
        fun onResume()
    }
}