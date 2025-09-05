package br.com.mobicare.cielo.merchant.data.remote

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.merchant.data.MerchantDataSource
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest
import br.com.mobicare.cielo.merchant.domain.entity.MerchantPermissionsEligible
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import io.reactivex.Observable
import retrofit2.Response

class MerchantRemoteDataSourceImpl(private val api: CieloAPIServices) : MerchantDataSource {

    override fun postMerchantChallengerActivate(
        activationCode: String
    ): Observable<Response<Void>> {
        return this.api.postMerchantChallengeActivate(
            MerchantChallengerActivateRequest(
                activationCode
            )
        )
    }

    override fun getMerchantPermissionsEligible(): Observable<MerchantPermissionsEligible> = this.api.getMerchantPermissionsEligible()
    override fun sendPermisionRegister(): Observable<Response<Void>> = this.api.sendPermisionRegister()
    override fun balcaoRecebiveisPermissionRegister(): Observable<MerchantResponseRegisterGet> = this.api.balcaoRecebiveisPermissionRegister()
    override fun getDebitoContaPermissionsEligible(): Observable<ResponseDebitoContaEligible> = this.api.getDebitoContaPermissionsEligible()
    override fun fetchEnrollmentActiveBank()= this.api.fetchEnrollmentActiveBank()
    override fun sendDebitoContaPermission(optin: String): Observable<Response<Void>> = this.api.sendDebitoContaPermission(optin)
}