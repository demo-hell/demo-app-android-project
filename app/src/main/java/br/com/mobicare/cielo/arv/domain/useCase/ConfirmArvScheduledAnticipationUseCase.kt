package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ConfirmArvScheduledAnticipationUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        request: ArvConfirmScheduledAnticipationRequest
    ): CieloDataResult<Void> =
        repository.confirmScheduledAnticipation(request)
}