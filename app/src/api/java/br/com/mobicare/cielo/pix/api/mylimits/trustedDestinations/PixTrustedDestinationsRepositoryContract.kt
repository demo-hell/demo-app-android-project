package br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations

import br.com.mobicare.cielo.pix.domain.PixAddNewTrustedDestinationRequest
import br.com.mobicare.cielo.pix.domain.PixAddNewTrustedDestinationResponse
import br.com.mobicare.cielo.pix.domain.PixDeleteTrustedDestinationRequest
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse
import io.reactivex.Observable
import retrofit2.Response

interface PixTrustedDestinationsRepositoryContract {
    fun getTrustedDestinations(servicesGroup: String? = null): Observable<List<PixTrustedDestinationResponse>>

    fun addNewTrustedDestination(
        otp: String?,
        body: PixAddNewTrustedDestinationRequest
    ): Observable<PixAddNewTrustedDestinationResponse>

    fun deleteTrustedDestination(
        otp: String?,
        body: PixDeleteTrustedDestinationRequest
    ): Observable<Response<Void>>
}