package br.com.mobicare.cielo.pix.api.claim

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.ClaimsRequest
import br.com.mobicare.cielo.pix.domain.ConfirmClaimsRequest
import br.com.mobicare.cielo.pix.domain.RevokeClaimsRequest

class PixClaimDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun createClaims(
        otpCode: String?,
        body: ClaimsRequest
    ) = api.createClaims(authorization, otpCode, body)

    fun revokeClaims(
        otpCode: String?,
        body: RevokeClaimsRequest
    ) = api.revokeClaims(authorization, otpCode, body)

    fun confirmClaims(
        otpCode: String?,
        body: ConfirmClaimsRequest
    ) = api.confirmClaims(authorization, otpCode, body)
}