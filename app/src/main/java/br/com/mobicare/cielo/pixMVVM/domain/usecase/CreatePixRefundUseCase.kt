package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository

class CreatePixRefundUseCase(
    private val repository: PixRefundsRepository
) : UseCase<CreatePixRefundUseCase.Params, PixRefundCreated> {

    override suspend fun invoke(params: Params) = repository.refund(params.token, params.request)

    data class Params(
        val token: String?,
        val request: PixRefundCreateRequest
    )

}