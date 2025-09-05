package br.com.mobicare.cielo.p2m.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.p2m.domain.repository.P2mAcceptRepository

class PutP2mAcceptUseCase(private val repository: P2mAcceptRepository) {

    suspend operator fun invoke(
        bannerId: String
    ): CieloDataResult<Void> =
        repository.putP2mAccept(bannerId)
}