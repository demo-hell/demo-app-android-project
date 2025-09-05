package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class RequestPixTransferWithKeyUseCase(
    private val repository: PixTransactionsRepository
) : UseCase<RequestPixTransferWithKeyUseCase.Params, PixTransferResult> {

    override suspend fun invoke(params: Params) =
        repository.transferWithKey(params.otpCode, params.request)

    data class Params(
        val otpCode: String?,
        val request: PixTransferKeyRequest?
    )

}