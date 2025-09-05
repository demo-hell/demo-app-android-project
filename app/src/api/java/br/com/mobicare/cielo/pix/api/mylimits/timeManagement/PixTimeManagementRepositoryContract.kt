package br.com.mobicare.cielo.pix.api.myLimits.timeManagement

import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest
import br.com.mobicare.cielo.pix.domain.PixTimeManagementResponse
import io.reactivex.Observable
import retrofit2.Response

interface PixTimeManagementRepositoryContract {

    fun getNightTime(
    ): Observable<PixTimeManagementResponse>

    fun updateNightTime(
            otpCode: String?,
            body: PixTimeManagementRequest?
    ): Observable<Response<Void>>
}