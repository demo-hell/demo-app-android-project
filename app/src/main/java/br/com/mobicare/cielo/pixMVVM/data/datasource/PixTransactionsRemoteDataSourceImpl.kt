package br.com.mobicare.cielo.pixMVVM.data.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.mapper.toEntity
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixTransactionsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult

class PixTransactionsRemoteDataSourceImpl(
    private val serviceApi: PixServiceApi,
    private val safeApiCaller: SafeApiCaller,
) : PixTransactionsRemoteDataSource {
    private val apiErrorResult =
        CieloDataResult.APIError(
            CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR),
        )

    override suspend fun getTransferDetails(
        endToEndId: String?,
        transactionCode: String?,
    ): CieloDataResult<PixTransferDetail> {
        var result: CieloDataResult<PixTransferDetail> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.getTransferDetails(endToEndId, transactionCode)
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

    override suspend fun transferWithKey(
        otpCode: String?,
        request: PixTransferKeyRequest?,
    ): CieloDataResult<PixTransferResult> {
        var result: CieloDataResult<PixTransferResult> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.transferWithKey(otpCode, request)
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

    override suspend fun transferToBankAccount(
        otpCode: String?,
        request: PixTransferBankAccountRequest?,
    ): CieloDataResult<PixTransferResult> {
        var result: CieloDataResult<PixTransferResult> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.transferToBankAccount(otpCode, request)
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

    override suspend fun getTransferBanks(): CieloDataResult<List<PixTransferBank>> {
        var result: CieloDataResult<List<PixTransferBank>> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.getTransferBanks()
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

    override suspend fun cancelTransferSchedule(
        otpCode: String,
        request: PixScheduleCancelRequest,
    ): CieloDataResult<PixTransferResult> {
        var result: CieloDataResult<PixTransferResult> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.cancelTransferSchedule(otpCode, request)
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

    override suspend fun getTransferScheduleDetail(schedulingCode: String?): CieloDataResult<PixSchedulingDetail> {
        var result: CieloDataResult<PixSchedulingDetail> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.getTransferScheduleDetail(schedulingCode)
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

    override suspend fun transferScheduledBalance(otpCode: String?): CieloDataResult<Unit> {
        var result: CieloDataResult<Unit> = apiErrorResult

        safeApiCaller
            .safeApiCall {
                serviceApi.transferScheduledBalance(otpCode)
            }.onSuccess {
                result = CieloDataResult.Success(Unit)
            }.onError {
                result = it
            }.onEmpty {
                result = it
            }

        return result
    }
}
