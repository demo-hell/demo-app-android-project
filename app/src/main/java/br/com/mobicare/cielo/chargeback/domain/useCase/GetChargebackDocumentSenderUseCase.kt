package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentSenderParams
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository

class GetChargebackDocumentSenderUseCase (
    private val repository: ChargebackRepository
) {
    suspend operator fun invoke(params: ChargebackDocumentSenderParams) =
        repository.getChargebackDocumentSender(params)
}