package br.com.mobicare.cielo.pix.ui.keys.myKeys.home

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.Key
import br.com.mobicare.cielo.pix.domain.MyKey

interface PixMyKeysContract {

    interface View : BaseView {
        fun onShowMyKeys(keys: Key?, isShowCNPJ: Boolean)
        fun onGetMyKeys()
        fun onShowVerificationKeys(keys: List<MyKey>)
        fun onHideVerificationKeys()
        fun onHideMyKeys()
        fun onShowKeyDetails(key: MyKey)
        fun onShowClaimKeysDetails(key: MyKey)
        fun onShowReceiveClaimKeysDetails(key: MyKey)
        fun onNoKeyRegistered()

        fun onShowAllErrors(onFirstAction: () -> Unit)
        fun onErrorDelete(error: ErrorMessage? = null)
        fun onErrorDefault(error: ErrorMessage? = null)
        fun onErrorConfirmClaim(error: ErrorMessage? = null)
        fun onErrorCreateClaimOwnership(error: ErrorMessage?)
        fun onErrorCreateClaimPortability(error: ErrorMessage?)

        fun onSuccess(onAction: () -> Unit)
        fun onShowSuccessToKeepKey()
        fun onShowSuccessToReleaseKey()
    }

    interface Presenter {
        fun getMyKeys()
        fun deleteKey(otp: String, key: String, isStartAnimation: Boolean)
        fun getUsername(): String
        fun cancelClaim(
            otp: String,
            key: MyKey?,
            isPortabilityOrClaimKey: Boolean = false,
            isClaimer: Boolean = true
        )

        fun confirmClaim(otp: String, key: MyKey?)
        fun onResume()
        fun onPause()
    }
}