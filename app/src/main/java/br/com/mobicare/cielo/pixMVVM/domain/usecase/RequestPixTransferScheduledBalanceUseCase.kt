package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class RequestPixTransferScheduledBalanceUseCase(
    private val repository: PixTransactionsRepository,
) : UseCase<RequestPixTransferScheduledBalanceUseCase.Params, Unit> {
    override suspend fun invoke(params: Params) = repository.transferScheduledBalance(params.otpCode)

    data class Params(
        val otpCode: String?,
    )
}
