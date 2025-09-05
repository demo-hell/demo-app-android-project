package br.com.mobicare.cielo.pix.api.claim

import br.com.mobicare.cielo.pix.domain.*
import io.reactivex.Observable

interface PixClaimRepositoryContract {
    fun createClaims(otpCode: String?, body: ClaimsRequest): Observable<ClaimsResponse>
    fun revokeClaims(
        otpCode: String?,
        body: RevokeClaimsRequest
    ): Observable<RevokeClaimsResponse>
    fun confirmClaims(
        otpCode: String?,
            body: ConfirmClaimsRequest
    ): Observable<ConfirmClaimsResponse>
}