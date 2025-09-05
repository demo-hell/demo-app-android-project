package br.com.mobicare.cielo.merchant.data

import br.com.mobicare.cielo.merchant.domain.entity.MerchantPermissionsEligible
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import br.com.mobicare.cielo.merchant.domain.repository.MerchantRepository
import io.reactivex.Observable
import retrofit2.Response

class MerchantRepositoryImpl(private val dataSource: MerchantDataSource) : MerchantRepository {

    override fun postMerchantChallengerActivate(
            activationCode: String
    ): Observable<Response<Void>> {
        return dataSource.postMerchantChallengerActivate(activationCode)
    }

    override fun getMerchantPermissionsEligible(): Observable<MerchantPermissionsEligible> =
            dataSource.getMerchantPermissionsEligible()

    override fun sendPermisionRegister(): Observable<Response<Void>> =
            dataSource.sendPermisionRegister()

    override fun balcaoRecebiveisPermissionRegister(): Observable<MerchantResponseRegisterGet> =
            dataSource.balcaoRecebiveisPermissionRegister()

    override fun fetchEnrollmentActiveBank() = dataSource.fetchEnrollmentActiveBank()

    override fun getDebitoContaPermissionsEligible(): Observable<ResponseDebitoContaEligible> =  dataSource.getDebitoContaPermissionsEligible()

    override fun sendDebitoContaPermission(optin: String): Observable<Response<Void>> = dataSource.sendDebitoContaPermission(optin)
}