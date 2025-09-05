package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixExtractFilterRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixReceiptsScheduledRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixExtractRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled

class PixExtractRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller,
) : PixExtractRemoteDataSource {
    private val apiErrorResult =
        CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR),
        )

    override suspend fun getExtract(request: PixExtractFilterRequest): CieloDataResult<PixExtract> {
        var result: CieloDataResult<PixExtract> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.getExtract(
                    startDate = request.startDate,
                    endDate = request.endDate,
                    limit = request.limit,
                    idEndToEnd = request.idEndToEnd,
                    receiptsTab = request.receiptsTab.name,
                    schedulingCode = request.schedulingCode,
                    schedulingStatus = request.schedulingStatus,
                    period = request.period,
                    transferType = request.transferType,
                    cashFlowType = request.cashFlowType,
                )
            }.onSuccess { response ->
                response.body()?.let {
                    result = CieloDataResult.Success(it.toEntity())
                }
            }.onError {
                result = it
            }.onEmpty {
                result = it
            }

        return result
    }

    override suspend fun getReceiptsScheduled(request: PixReceiptsScheduledRequest): CieloDataResult<PixReceiptsScheduled> {
        var result: CieloDataResult<PixReceiptsScheduled> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.getReceiptsScheduled(
                    limit = request.limit,
                    lastSchedulingIdentifierCode = request.lastSchedulingIdentifierCode,
                    lastNextDateTimeScheduled = request.lastNextDateTimeScheduled,
                    schedulingStartDate = request.schedulingStartDate,
                    schedulingEndDate = request.schedulingEndDate,
                )
            }.onSuccess { response ->
                response.body()?.let {
                    result = CieloDataResult.Success(it.toEntity())
                }
            }.onError {
                result = it
            }.onEmpty {
                result = it
            }

        return result
    }
}
