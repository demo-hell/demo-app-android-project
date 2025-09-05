package br.com.mobicare.cielo.arv.domain.useCase

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetArvSingleAnticipationWithFilterUseCase(
    private val repository: ArvRepositoryNew
) {
    suspend operator fun invoke(
        negotiationType: String?,
        initialDate: String?,
        endDate: String?,
        brandCodes: List<Int>?,
        acquirerCode: List<Int>?,
        receiveToday: Boolean?
    ): CieloDataResult<ArvAnticipation> =
        repository.getArvSingleAnticipationByBrands(
            negotiationType = negotiationType,
            initialDate = initialDate,
            finalDate = endDate,
            brandCodes = brandCodes,
            acquirerCode = acquirerCode,
            receiveToday = receiveToday
        )
}