package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class CancelPixTransferScheduleUseCase(
    private val repository: PixTransactionsRepository
) : UseCase<CancelPixTransferScheduleUseCase.Params, PixTransferResult> {

    override suspend fun invoke(params: Params) =
        repository.cancelTransferSchedule(params.otpCode, params.request)

    data class Params(
        val otpCode: String,
        val request: PixScheduleCancelRequest
    )

}