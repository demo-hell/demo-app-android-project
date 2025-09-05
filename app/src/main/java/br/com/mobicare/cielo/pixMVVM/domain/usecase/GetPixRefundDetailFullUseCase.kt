package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository

class GetPixRefundDetailFullUseCase(
    private val repository: PixRefundsRepository
) : UseCase<GetPixRefundDetailFullUseCase.Params, PixRefundDetailFull> {

    override suspend fun invoke(params: Params) =
        repository.getDetailFull(params.transactionCode)

    data class Params(val transactionCode: String?)

}