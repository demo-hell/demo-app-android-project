package br.com.mobicare.cielo.pix.api.myLimits

import br.com.mobicare.cielo.pix.domain.PixMyLimitsRequest
import br.com.mobicare.cielo.pix.domain.PixMyLimitsResponse
import io.reactivex.Observable
import retrofit2.Response

interface PixMyLimitsRepositoryContract {

    fun getLimits(
        serviceGroup: String?,
        beneficiaryType: String? = null
    ): Observable<PixMyLimitsResponse>

    fun updateLimits(
        otpCode: String?,
        body: PixMyLimitsRequest?
    ): Observable<Response<Void>>

}