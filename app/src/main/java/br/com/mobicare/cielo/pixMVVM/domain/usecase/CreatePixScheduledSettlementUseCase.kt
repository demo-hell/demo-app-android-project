package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixScheduledSettlementRepository

class CreatePixScheduledSettlementUseCase(
    private val repository: PixScheduledSettlementRepository
) : UseCase<CreatePixScheduledSettlementUseCase.Params, PixScheduledSettlementResponse> {

    override suspend fun invoke(params: Params) = repository.create(params.token, params.request)

    data class Params(
        val token: String?,
        val request: PixScheduledSettlementRequest
    )

}