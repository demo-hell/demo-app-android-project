package br.com.mobicare.cielo.pix.api.claim

import br.com.mobicare.cielo.pix.domain.ClaimsRequest
import br.com.mobicare.cielo.pix.domain.ConfirmClaimsRequest
import br.com.mobicare.cielo.pix.domain.RevokeClaimsRequest

class PixClaimRepository(private val dataSource: PixClaimDataSource) : PixClaimRepositoryContract {

    override fun createClaims(
        otpCode: String?,
        body: ClaimsRequest
    ) = dataSource.createClaims(otpCode, body)

    override fun revokeClaims(
        otpCode: String?,
        body: RevokeClaimsRequest
    ) = dataSource.revokeClaims(otpCode, body)

    override fun confirmClaims(
        otpCode: String?,
        body: ConfirmClaimsRequest
    ) = dataSource.confirmClaims(otpCode, body)
}