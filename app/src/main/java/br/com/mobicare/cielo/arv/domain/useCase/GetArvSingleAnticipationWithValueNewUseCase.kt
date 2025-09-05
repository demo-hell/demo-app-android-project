package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvSingleAnticipationWithValueNewUseCase(
    private val repository: ArvRepositoryNew,
) {
    suspend operator fun invoke(
        negotiationType: String?,
        value: Double?,
        receiveToday: Boolean? = null,
        initialDate: String? = null,
        finalDate: String? = null,
    ): CieloDataResult<ArvAnticipation> =
        repository.getArvSingleAnticipationWithValue(
            negotiationType = negotiationType,
            amount = value,
            receiveToday = receiveToday,
            initialDate = initialDate,
            finalDate = finalDate,
        )
}
