package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetail
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository

class GetPixRefundDetailUseCase(
    private val repository: PixRefundsRepository
) : UseCase<GetPixRefundDetailUseCase.Params, PixRefundDetail> {

    override suspend fun invoke(params: Params) =
        repository.getDetail(params.transactionCode)

    data class Params(val transactionCode: String?)

}