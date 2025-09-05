package br.com.mobicare.cielo.mfa.resume

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.MfaAccount

interface ResumeBankAndCNPJContract {

    interface View {
        fun showLoading(isVisible: Boolean)
        fun showSuccessful()
        fun showError(error: ErrorMessage)
        fun showTemporarilyBlockError(error: ErrorMessage)
        fun showBankChallengeActive()
        fun showBankChallengePending()
        fun showBlocked()
    }

    interface Presenter {
        fun sendEnrollment(mfaAccount: MfaAccount)
        fun sendChallenge(mfaAccount: MfaAccount)
    }
}