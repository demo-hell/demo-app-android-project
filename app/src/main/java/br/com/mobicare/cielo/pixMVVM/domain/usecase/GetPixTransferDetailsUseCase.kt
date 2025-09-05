package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class GetPixTransferDetailsUseCase(
    private val repository: PixTransactionsRepository
) : UseCase<GetPixTransferDetailsUseCase.Params, PixTransferDetail> {

    override suspend fun invoke(params: Params) =
        repository.getTransferDetails(params.endToEndId, params.transactionCode)

    data class Params(
        val endToEndId: String?,
        val transactionCode: String?
    )

}