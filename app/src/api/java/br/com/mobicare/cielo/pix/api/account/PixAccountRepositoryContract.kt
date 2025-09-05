package br.com.mobicare.cielo.pix.api.account

import br.com.mobicare.cielo.pix.domain.PixMerchantResponse
import br.com.mobicare.cielo.pix.domain.PixProfileRequest
import br.com.mobicare.cielo.pix.domain.PixProfileResponse
import io.reactivex.Observable
import retrofit2.Response

interface PixAccountRepositoryContract {

    fun getMerchant(): Observable<PixMerchantResponse>

    fun getProfile(): Observable<PixProfileResponse>

    fun updateProfile(
        otpCode: String,
        body: PixProfileRequest?
    ): Observable<Response<Void>>
}