package br.com.mobicare.cielo.lgpd.domain.repository

import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import io.reactivex.Observable
import retrofit2.Response

interface LgpdRepository {
    fun getEligibility() : Observable<LgpdElegibilityEntity>
    fun postLgpdAgreement() : Observable<Response<Void>>
}