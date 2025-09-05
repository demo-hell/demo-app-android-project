package br.com.mobicare.cielo.lgpd.data

import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import io.reactivex.Observable
import retrofit2.Response

interface LgpdDataSource {
    fun getEligibility() : Observable<LgpdElegibilityEntity>
    fun postLgpdAgreement() : Observable<Response<Void>>
}