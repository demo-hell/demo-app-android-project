package br.com.mobicare.cielo.antifraud.data.repository

import br.com.mobicare.cielo.antifraud.data.dataSource.AntiFraudDataSource
import br.com.mobicare.cielo.antifraud.domain.repository.AntiFraudRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class AntiFraudRepositoryImpl(
    private val dataSource: AntiFraudDataSource
) : AntiFraudRepository {

    override suspend fun getSessionID(): CieloDataResult<String> = dataSource.getSessionID()

}