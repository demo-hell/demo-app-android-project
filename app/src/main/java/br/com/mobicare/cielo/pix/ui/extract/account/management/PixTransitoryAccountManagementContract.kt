package br.com.mobicare.cielo.pix.ui.extract.account.management

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.PixMerchantResponse

interface PixTransitoryAccountManagementContract {

    interface View : BaseView {
        fun onShowLoadingMerchant()
        fun onHideMerchant()
        fun onSuccessMerchant(merchant: PixMerchantResponse)
        fun onErrorMerchant(errorMessage: ErrorMessage?)

        fun onShowIDOnboarding()
        fun onNotAdmin()
        fun onValidateMFA()

        fun onErrorChangePixAccount(onFirstAction: () -> Unit)
        fun onSuccessChangePixAccount()
    }

    interface Presenter {
        fun getUsername(): String
        fun getMerchant()
        fun getUserInformation(isShowLoading: Boolean = true)
        fun changePixAccount(otp: String)
        fun onResume()
        fun onPause()
    }
}