package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixReceiptsScheduledRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixExtractRepository

class GetPixReceiptsScheduledUseCase(
    private val repository: PixExtractRepository,
) : UseCase<GetPixReceiptsScheduledUseCase.Params, PixReceiptsScheduled> {
    override suspend fun invoke(params: Params): CieloDataResult<PixReceiptsScheduled> =
        repository.getReceiptsScheduled(
            PixReceiptsScheduledRequest(
                limit = params.limit,
                lastNextDateTimeScheduled = params.lastNextDateTimeScheduled,
                lastSchedulingIdentifierCode = params.lastSchedulingIdentifierCode,
                schedulingEndDate = params.schedulingEndDate,
                schedulingStartDate = params.schedulingStartDate,
            ),
        )

    data class Params(
        val limit: Int = TWENTY_FIVE,
        val lastNextDateTimeScheduled: String? = null,
        val lastSchedulingIdentifierCode: String? = null,
        val schedulingEndDate: String? = null,
        val schedulingStartDate: String? = null,
    )
}
