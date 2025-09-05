package br.com.mobicare.cielo.suporteTecnico.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.domain.repo.RequestTicketSupportRepository

class GetPostOrdersReplacementsUseCase(
    private val repository: RequestTicketSupportRepository
) {

    suspend operator fun invoke(
        request: OpenTicket
    ): CieloDataResult<OrderReplacementResponse> =
        repository.postOrdersReplacements(request)
}