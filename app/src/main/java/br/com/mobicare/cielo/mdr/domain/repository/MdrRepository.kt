package br.com.mobicare.cielo.mdr.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface MdrRepository {
    suspend fun postContractDecision(
        apiId: String,
        bannerId: Int,
        isAccepted: Boolean,
    ): CieloDataResult<Void>
}
