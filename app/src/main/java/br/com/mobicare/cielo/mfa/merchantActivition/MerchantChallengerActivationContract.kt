package br.com.mobicare.cielo.mfa.merchantActivition

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse

interface MerchantChallengerActivationContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun onValueSuccess()
        fun onInvalidRequestError(error: ErrorMessage)
        fun onBusinessError(error: ErrorMessage)
        fun onValueError(error: ErrorMessage)
        fun incorrectValues()
        fun incorrectValuesThirdAttempt()
        fun configureActiveBank(enrollmentBankResponse: EnrollmentBankResponse?)
        fun hideEnrollmentActiveBank()
    }

    interface Presenter {
        fun activationCode(value1: String, value2: String)
        fun onResume()
        fun onPause()
    }
}