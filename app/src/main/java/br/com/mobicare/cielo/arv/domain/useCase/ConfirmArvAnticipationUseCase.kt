package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.response.ArvConfirmAnticipationResponse
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ConfirmArvAnticipationUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        request: ArvConfirmAnticipationRequest
    ): CieloDataResult<ArvConfirmAnticipationResponse> =
        repository.confirmAnticipation(request)
}