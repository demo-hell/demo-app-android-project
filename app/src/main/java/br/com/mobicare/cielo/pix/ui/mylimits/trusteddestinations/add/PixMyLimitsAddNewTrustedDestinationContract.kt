package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.add

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.ManualPayee
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse

interface PixMyLimitsAddNewTrustedDestinationContract {

    interface View : BaseView {
        fun onSetTrustedDestinationInformation(
            name: String?,
            document: String?,
            documentType: String?,
            bank: String?,
            branch: String?,
            account: String?
        )

        fun onSuccessAddNewTrustedDestination()
        fun onErrorAddNewTrustedDestination(onGenericError: () -> Unit, onOTPError: () -> Unit)
        fun onErrorAddNewTrustedDestinationOTP()
    }

    interface Presenter {
        fun onGetTrustedDestinationInformation(
            isKey: Boolean,
            keyInformation: ValidateKeyResponse?,
            manualTransferPayee: ManualPayee?
        )

        fun onAddNewTrustedDestination(
            otp: String?, limit: Double?, fingerprint: String
        )

        fun getUsername(): String

        fun onResume()
        fun onPause()
    }
}