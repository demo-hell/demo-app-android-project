package br.com.mobicare.cielo.p2m.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface P2mAcceptRepository {
    suspend fun putP2mAccept(
        bannerId: String
    ): CieloDataResult<Void>
}