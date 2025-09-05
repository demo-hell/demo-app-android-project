package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base

import br.com.mobicare.cielo.accessManager.invite.receive.domain.InviteDetails
import br.com.mobicare.cielo.accessManager.model.PendingInviteItem
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface InviteReceiveContract {
    interface View: BaseView {
        fun onUserCreatedSuccess()
        fun showConnectionError(retryCallback: () -> Unit)
        fun onPasswordError(error: ErrorMessage)
        fun onInviteExpiredError(error: ErrorMessage)
        fun onGenericError(error: ErrorMessage)
        fun onInviteDetails(inviteDetails: InviteDetails) {}
        fun onInvalidCpf(error: ErrorMessage)
        fun onCpfValidateMaxTriesExceeded(error: ErrorMessage)
        fun onInviteAcceptSuccess() {}
        fun onAcceptInviteTokenSuccess() {}
        fun onDeclineInviteTokenSuccess() {}
        fun onShowGenericError() {}
        fun showPendingInvite(invite: PendingInviteItem) {}
        fun onErrorNotBooting() {}
    }
    interface Presenter {
        fun retry()
        fun onResume()
        fun onPause()
    }
}