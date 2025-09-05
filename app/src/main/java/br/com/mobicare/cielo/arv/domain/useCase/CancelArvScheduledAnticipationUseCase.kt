package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class CancelArvScheduledAnticipationUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        request: ArvScheduledAnticipationCancelRequest
    ): CieloDataResult<Void> =
        repository.cancelScheduledAnticipation(request)
}