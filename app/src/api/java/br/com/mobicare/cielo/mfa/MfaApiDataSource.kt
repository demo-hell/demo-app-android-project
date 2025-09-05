package br.com.mobicare.cielo.mfa

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest

class MfaApiDataSource(private var api: CieloAPIServices) {

    fun checkEnrollment() = api.checkEnrollment()

    fun checkMfaEligibility() = api.checkMfaEligibility()

    fun resendMfa(request: MfaResendRequest?) = api.resendMfa(request)

    fun seedEnrollment(
        fingerprint: String?
    ) = api.activationCode(fingerprint = fingerprint)

    fun seedChallenge(
        request: MerchantChallengerActivateRequest
    ) = api.postMerchantChallengeActivate(request)

    fun refreshTokenMfa(accessToken: String?, refreshToken: String?) =
        api.refreshTokenMfa(accessToken, refreshToken)

    fun getMfaBanks() = api.getMfaBanks()

    fun sendMFABankChallenge(account: MfaAccount) = api.sendMFABankChallenge(account)

    fun postBankEnrollment(account: MfaAccount) = api.postBankEnrollment(account)

    fun postEnrollmentActivate(code: String) = api.postEnrollmentActivate(code)
}