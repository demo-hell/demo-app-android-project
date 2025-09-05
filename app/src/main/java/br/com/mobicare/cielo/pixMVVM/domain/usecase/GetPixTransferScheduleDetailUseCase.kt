package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class GetPixTransferScheduleDetailUseCase(
    private val repository: PixTransactionsRepository
) : UseCase<GetPixTransferScheduleDetailUseCase.Params, PixSchedulingDetail> {

    override suspend fun invoke(params: Params) =
        repository.getTransferScheduleDetail(params.schedulingCode)

    data class Params(val schedulingCode: String?)

}