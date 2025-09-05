package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class GetChargebackPendingUseCase(
    private val repository: ChargebackRepository
) {
    suspend operator fun invoke(params: ChargebackListParams): CieloDataResult<Chargebacks> =
        repository.getChargebackList(params)
}