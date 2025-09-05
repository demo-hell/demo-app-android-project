package br.com.mobicare.cielo.mfa.merchantstatus.challenge

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.api.BankChallengeResponse
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA

class MerchantValidateChallengePresenterImpl(
        private val view: MerchantValidateChallengeView,
        private val mfaRepository: MfaRepository
) : MerchantValidateChallengePresenter {

    private var accountSelected: MfaAccount? = null

    override fun getMfaBanks() {
        view.showLoading()
        this.mfaRepository.getMfaBanks(object : APICallbackDefault<ArrayList<MfaAccount>, String> {
            override fun onError(error: ErrorMessage) {
                when (error.httpStatus) {
                    420 -> view.showNotEligibleUser()
                    404 -> view.onBusinessError(error)
                    else -> view.showError(error)
                }
            }

            override fun onSuccess(response: ArrayList<MfaAccount>) {
                if (response.isNotEmpty()) {
                    if (response.isNotEmpty()) {
                        view.show(response)
                    }
                }
            }
        })
    }

    override fun selectedItem(account: MfaAccount) {
        this.accountSelected = account
    }

    override fun sendMFABankChallenge() {
        this.accountSelected?.let { itAccount ->
            view.showLoading()
            this.mfaRepository.sendMFABankChallenge(
                    itAccount,
                    object : APICallbackDefault<EnrollmentResponse, String> {
                        override fun onError(error: ErrorMessage) {
                            when (error.errorCode) {
                                MerchantStatusMFA.PENNY_DROP_TEMPORARILY_BLOCKED.name -> view.showTemporarilyBlockError(error)
                                else -> view.showError(error)
                            }
                        }

                        override fun onSuccess(response: EnrollmentResponse) {
                            val status = response.status ?: ""
                            when (EnrollmentStatus.fromString(status)) {
                                EnrollmentStatus.ACTIVE -> view.showBankChallengeActive()
                                EnrollmentStatus.WAITING_ACTIVATION -> view.showBankChallengePending()
                                EnrollmentStatus.BLOCKED -> view.showBlocked()
                                else -> view.showError(ErrorMessage())
                            }
                        }
                    })
        }
    }
}