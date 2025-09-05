package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.DONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED_EIGHTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import java.time.LocalDate

class GetChargebackTreatedUseCase(
    private val repository: ChargebackRepository
) {
    suspend operator fun invoke(params: ChargebackListParams): CieloDataResult<Chargebacks> =
        repository.getChargebackList(params)
}