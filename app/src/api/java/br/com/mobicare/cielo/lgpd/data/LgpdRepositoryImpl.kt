package br.com.mobicare.cielo.lgpd.data

import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import io.reactivex.Observable
import retrofit2.Response

class LgpdRepositoryImpl(private val dataSource: LgpdDataSource) : LgpdRepository {

    override fun getEligibility(): Observable<LgpdElegibilityEntity> {
        return dataSource.getEligibility()
    }

    override fun postLgpdAgreement(): Observable<Response<Void>> {
        return dataSource.postLgpdAgreement()
    }

}