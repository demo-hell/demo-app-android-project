package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse

interface DetainerRemoteRepository {
    suspend fun getDetainer(consentId: String): CieloDataResult<DetainerResponse>

}