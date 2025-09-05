package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationContractRequest
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvScheduledContractUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        request: ArvScheduledAnticipationContractRequest
    ): CieloDataResult<ArvScheduleContract> =
        repository.getScheduledAnticipationContract(request)
}