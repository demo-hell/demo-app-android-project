package br.com.mobicare.cielo.posVirtual.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository

class GetPosVirtualAccreditationOffersUseCase(
    private val repository: PosVirtualAccreditationRepository
) {

    suspend operator fun invoke(additionalProduct: String? = null): CieloDataResult<OfferResponse> {
        return repository.getOffers(additionalProduct)
    }

}