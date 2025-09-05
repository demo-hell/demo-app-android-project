package br.com.mobicare.cielo.mfa.merchantstatus.challenge

import br.com.mobicare.cielo.mfa.MfaAccount

interface MerchantValidateChallengePresenter {

    fun getMfaBanks()
    fun selectedItem(account: MfaAccount)
    fun sendMFABankChallenge()
}