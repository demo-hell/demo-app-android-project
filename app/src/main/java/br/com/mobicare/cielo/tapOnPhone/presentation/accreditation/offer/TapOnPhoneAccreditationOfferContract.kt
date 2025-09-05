package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer

import android.app.Activity
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount

interface TapOnPhoneAccreditationOfferContract {

    interface View {
        fun showLoading()
        fun hideLoading()

        fun onGetSessionIdSuccess(sessionId: String)
        fun onShowCallCenter(error: ErrorMessage)
        fun onShowOffer(offer: OfferResponse)
        fun onShowAccounts(accounts: List<TapOnPhoneAccount>)

        fun showLoadingOffers()
        fun hideLoadingOffers()
        fun showLoadingAccounts()
        fun hideLoadingAccounts()
        fun showOfferError()

        fun showError(error: ErrorMessage? = null)
        fun onLoadDataSuccess(
            sessionId: String,
            offer: OfferResponse,
            accounts: List<TapOnPhoneAccount>
        )
    }

    interface Presenter {
        fun onDestroy()
        fun onResume()
        fun getOfferData()
        fun reloadOffer(includeRR: Boolean)
        fun isShowBSAlertDeviceIncompatibility(activity: Activity): Boolean
        fun isEnabledAutomaticReceiptOptional(): Boolean
    }
}
