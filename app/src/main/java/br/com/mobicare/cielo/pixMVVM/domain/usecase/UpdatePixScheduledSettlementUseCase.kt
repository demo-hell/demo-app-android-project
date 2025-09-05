package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixScheduledSettlementResponse
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixScheduledSettlementRepository

class UpdatePixScheduledSettlementUseCase(
    private val repository: PixScheduledSettlementRepository
) : UseCase<UpdatePixScheduledSettlementUseCase.Params, PixScheduledSettlementResponse> {

    override suspend fun invoke(params: Params) = repository.update(
        otpCode = params.token,
        request = PixScheduledSettlementRequest(listScheduled = params.scheduledList)
    )

    data class Params(
        val token: String?,
        val scheduledList: List<String>
    )

}