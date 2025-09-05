package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository

class GetPixRefundReceiptsUseCase(
    private val repository: PixRefundsRepository
) : UseCase<GetPixRefundReceiptsUseCase.Params, PixRefundReceipts> {

    override suspend fun invoke(params: Params) =
        repository.getReceipts(params.idEndToEndOriginal)

    data class Params(val idEndToEndOriginal: String?)

}