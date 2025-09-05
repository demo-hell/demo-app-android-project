package br.com.mobicare.cielo.mfa.resume

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.BankEnrollmentResponse
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA

const val ERROR_401 = 401

class ResumeBankAndCNPJPresenter(private val view: ResumeBankAndCNPJContract.View,
                                 private val repository: MfaRepository) : ResumeBankAndCNPJContract.Presenter {

    override fun sendEnrollment(mfaAccount: MfaAccount) {
        view.showLoading(true)
        repository.postBankEnrollment(mfaAccount, object : APICallbackDefault<BankEnrollmentResponse, String> {
            override fun onError(error: ErrorMessage) {
                if (error.httpStatus != ERROR_401) {
                    setupError(error.errorCode,
                            EnrollmentStatus.PENNY_DROP_TEMPORARILY_BLOCKED.name,
                            error)
                }
            }

            override fun onSuccess(response: BankEnrollmentResponse) {
                view.showLoading(false)
                view.showSuccessful()
            }
        })
    }

    override fun sendChallenge(mfaAccount: MfaAccount) {
        view.showLoading(true)
        repository.sendMFABankChallenge(mfaAccount,
                object : APICallbackDefault<EnrollmentResponse, String> {
                    override fun onError(error: ErrorMessage) {
                        setupError(error.errorCode,
                                MerchantStatusMFA.PENNY_DROP_TEMPORARILY_BLOCKED.name,
                                error)
                    }

                    override fun onSuccess(response: EnrollmentResponse) {
                        view.showLoading(false)
                        val status = response.status ?: ""

                        when (EnrollmentStatus.fromString(status)) {
                            EnrollmentStatus.ACTIVE -> view.showBankChallengeActive()
                            EnrollmentStatus.WAITING_ACTIVATION -> view.showBankChallengePending()
                            EnrollmentStatus.BLOCKED -> view.showBlocked()
                            else -> view.showError(ErrorMessage())
                        }
                    }
                }
        )
    }

    private fun setupError(errorCode: String, typeError: String, error: ErrorMessage) {
        view.showLoading(false)

        when (errorCode) {
            typeError -> view.showTemporarilyBlockError(error)
            else -> view.showError(error)
        }
    }
}