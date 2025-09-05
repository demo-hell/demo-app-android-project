package br.com.mobicare.cielo.lgpd.data.remote

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.lgpd.data.LgpdDataSource
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import io.reactivex.Observable
import retrofit2.Response

class LgpdRemoteDataSourceImpl(private val api: CieloAPIServices) : LgpdDataSource {

    override fun getEligibility(): Observable<LgpdElegibilityEntity> {
        return api.getLgpdEligibility()
    }

    override fun postLgpdAgreement(): Observable<Response<Void>> {
        return api.postLgpdAgreement()
    }

}