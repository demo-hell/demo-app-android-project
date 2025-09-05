package br.com.mobicare.cielo.antifraud.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface AntiFraudRepository {

    suspend fun getSessionID(): CieloDataResult<String>

}