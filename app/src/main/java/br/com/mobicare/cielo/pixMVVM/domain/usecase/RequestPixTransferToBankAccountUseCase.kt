package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class RequestPixTransferToBankAccountUseCase(
    private val repository: PixTransactionsRepository
) : UseCase<RequestPixTransferToBankAccountUseCase.Params, PixTransferResult> {

    override suspend fun invoke(params: Params) =
        repository.transferToBankAccount(params.otpCode, params.request)

    data class Params(
        val otpCode: String?,
        val request: PixTransferBankAccountRequest?
    )

}