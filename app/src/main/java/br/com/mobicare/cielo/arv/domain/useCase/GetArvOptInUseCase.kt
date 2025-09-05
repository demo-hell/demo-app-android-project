package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvOptIn
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvOptInUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(): CieloDataResult<ArvOptIn> =
        repository.getOptInStatus()
}