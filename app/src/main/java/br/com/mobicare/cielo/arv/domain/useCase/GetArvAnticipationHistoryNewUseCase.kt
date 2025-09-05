package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.data.model.request.ArvHistoricRequest
import br.com.mobicare.cielo.arv.data.model.response.ArvHistoricResponse
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvAnticipationHistoryNewUseCase(private val repository: ArvRepositoryNew) {
    suspend operator fun invoke(params: ArvHistoricRequest): CieloDataResult<ArvHistoricResponse> =
        repository.getArvAnticipationHistory(
            negotiationType = params.negotiationType,
            status = params.status,
            initialDate = params.initialDate,
            finalDate = params.finalDate,
            page = params.page,
            pageSize = params.pageSize,
            modalityType = params.modalityType,
            operationNumber = params.operationNumber
        )
}