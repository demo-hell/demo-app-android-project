package br.com.mobicare.cielo.pix.ui.home.account

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.PixMerchantResponse

interface PixFreeMovementAccountManagementContract {

    interface View : BaseView {
        fun onErrorChangePixAccount(onFirstAction: () -> Unit)
        fun onSuccessChangePixAccount()

        fun onShowMerchantLoading()
        fun onHideMerchantLoading()
        fun onSuccessMerchant(merchant: PixMerchantResponse)
        fun onErrorMerchant(errorMessage: ErrorMessage?)
    }

    interface Presenter {
        fun getUsername(): String
        fun getMerchant()
        fun changePixAccount(otp: String)
        fun onResume()
        fun onPause()
    }
}