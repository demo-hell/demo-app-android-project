package br.com.mobicare.cielo.merchant.domain.repository

import br.com.mobicare.cielo.merchant.domain.entity.MerchantPermissionsEligible
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import io.reactivex.Observable
import retrofit2.Response

interface MerchantRepository {

    fun postMerchantChallengerActivate(
        activationCode: String
    ): Observable<Response<Void>>

    fun getMerchantPermissionsEligible(): Observable<MerchantPermissionsEligible>
    fun sendPermisionRegister(): Observable<Response<Void>>
    fun balcaoRecebiveisPermissionRegister(): Observable<MerchantResponseRegisterGet>
    fun getDebitoContaPermissionsEligible(): Observable<ResponseDebitoContaEligible>
    fun fetchEnrollmentActiveBank() : Observable<EnrollmentBankResponse>
    fun sendDebitoContaPermission(optin: String): Observable<Response<Void>>

}