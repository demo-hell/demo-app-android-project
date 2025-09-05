package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvSingleAnticipationWithDateNewUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        negotiationType: String?,
        initialDate: String?,
        endDate: String?
    ): CieloDataResult<ArvAnticipation> =
        repository.getArvSingleAnticipationWithDate(
            negotiationType = negotiationType,
            initialDate = initialDate,
            finalDate = endDate
        )
}