package br.com.mobicare.cielo.pix.api.extract

import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.pix.domain.PixExtractFilterRequest
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import io.reactivex.Observable

interface PixExtractRepositoryContract {
    fun getExtract(pixExtractRequest: PixExtractFilterRequest): Observable<PixExtractResponse>

    fun getUserCardBalance(
        cardProxy: String,
        accessToken: String
    ): Observable<PrepaidBalanceResponse>
}