package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentParams
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository

class GetChargebackDocumentUseCase(
    private val repository: ChargebackRepository
) {
    suspend operator fun invoke(params: ChargebackDocumentParams) =
        repository.getChargebackDocument(params)
}