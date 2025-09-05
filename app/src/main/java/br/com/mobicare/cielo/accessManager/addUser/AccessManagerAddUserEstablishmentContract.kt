package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.changeEc.domain.Hierarchy
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerAddUserEstablishmentContract {
    interface View : BaseView {
        fun showMerchants(merchants: Array<Hierarchy>?) {}
        fun onErrorOTP()
        fun addUserForeignFlowAllowed(foreignFlowAllowed: Boolean)
    }

    interface Presenter {
        fun onPause()
        fun onResume()
        fun retry()
        fun loadItens()
        fun sendInvitation(
            cpfInviteRequest: String?,
            emailInviteRequest: String,
            roleInviteRequest: String,
            foreignInviteRequest: Boolean,
            countryCodeInviteRequest: String,
            otp: String
        )

        fun setView(view: View)
        fun getUsername(): String
        fun getRootCNPJ(): String
        fun getCustomerSettings()
    }
}