package br.com.mobicare.cielo.mfa.merchantstatus.challenge

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.MfaAccount

interface MerchantValidateChallengeView {

    fun show(contas: List<MfaAccount>)
    fun showLoading()
    fun showError(errorMessage: ErrorMessage)
    fun showTemporarilyBlockError(error: ErrorMessage)
    fun showBankChallengeActive()
    fun showNotEligibleUser()
    fun showBankChallengePending()
    fun showBlocked()
    fun onBusinessError(error: ErrorMessage)
}