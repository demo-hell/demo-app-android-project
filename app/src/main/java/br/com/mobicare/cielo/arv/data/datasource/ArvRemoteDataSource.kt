package br.com.mobicare.cielo.arv.data.datasource

import br.com.mobicare.cielo.arv.data.datasource.mapper.MapperArv
import br.com.mobicare.cielo.arv.data.datasource.remote.ArvServerApi
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationContractRequest
import br.com.mobicare.cielo.arv.data.model.response.ArvConfirmAnticipationResponse
import br.com.mobicare.cielo.arv.data.model.response.ArvHistoricResponse
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvBranchesContracts
import br.com.mobicare.cielo.arv.domain.model.ArvOptIn
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.model.RateSchedules
import br.com.mobicare.cielo.arv.domain.model.Schedules
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess

class ArvRemoteDataSource(
    private val serverApi: ArvServerApi,
    private val safeApiCaller: SafeApiCaller
) {

    private val errorDefault =
        CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    suspend fun getAnticipation(
        negotiationType: String?,
        receiveToday: Boolean
    ): CieloDataResult<ArvAnticipation> {
        var result: CieloDataResult<ArvAnticipation> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvAnticipation(
                negotiationType = negotiationType,
                receiveToday = receiveToday
            )
        }.onSuccess {
            result = MapperArv.mapToArv(
                it.body(),
                negotiationType
            )?.let { response ->
                CieloDataResult.Success(response)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getNegotiations(
        negotiationType: String?,
        status: String?,
        initialDate: String?,
        finalDate: String?,
        page: Int,
        pageSize: Int,
        modalityType: String?,
        operationNumber: String?
    ): CieloDataResult<ArvHistoricResponse> {
        var result: CieloDataResult<ArvHistoricResponse> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getNegotiations(
                negotiationType,
                status,
                initialDate,
                finalDate,
                page,
                pageSize,
                modalityType,
                operationNumber
            )
        }.onSuccess {
            result = it.body()?.let { response ->
                if (response.pagination?.lastPage == true || response.items?.firstOrNull() != null)
                    CieloDataResult.Success(response)
                else
                    CieloDataResult.Empty()
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getArvSingleAnticipationWithDate(
        negotiationType: String?,
        initialDate: String?,
        finalDate: String?
    ): CieloDataResult<ArvAnticipation> {
        var result: CieloDataResult<ArvAnticipation> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvAnticipation(negotiationType, initialDate, finalDate)
        }.onSuccess {
            result = MapperArv.mapToArv(it.body(), negotiationType, initialDate, finalDate)
                ?.let { response ->
                    CieloDataResult.Success(response)
                } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getArvSingleAnticipationWithValue(
        negotiationType: String?,
        amount: Double?,
        receiveToday: Boolean?,
        initialDate: String?,
        finalDate: String?,
    ): CieloDataResult<ArvAnticipation> {
        var result: CieloDataResult<ArvAnticipation> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvAnticipation(
                negotiationType = negotiationType,
                amount = amount,
                receiveToday = receiveToday,
                initialDate = initialDate,
                finalDate = finalDate,
            )
        }.onSuccess {
            result = MapperArv.mapToArv(
                it.body(),
                negotiationType,
                initialDate,
                finalDate,
            )?.let { response ->
                CieloDataResult.Success(response)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getArvSingleAnticipationByBrands(
        negotiationType: String?,
        initialDate: String?,
        finalDate: String?,
        brandCodes: List<Int>?,
        acquirerCode: List<Int>?,
        receiveToday: Boolean?
    ): CieloDataResult<ArvAnticipation> {
        var result: CieloDataResult<ArvAnticipation> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvAnticipation(
                negotiationType = negotiationType,
                initialDate = initialDate,
                finalDate = finalDate,
                cardBrandCode = brandCodes,
                acquirerCode = acquirerCode,
                receiveToday = receiveToday
            )
        }.onSuccess {
            result = MapperArv.mapToArv(it.body(), negotiationType, initialDate, finalDate)
                ?.let { response ->
                    CieloDataResult.Success(response)
                } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getBanks(): CieloDataResult<List<ArvBank>> {
        safeApiCaller.safeApiCall {
            serverApi.getBanks()
        }.onSuccess { response ->
            response.body()?.let { arvBankResponse ->
                return CieloDataResult.Success(arvBankResponse.map {
                    ArvBank(
                        it.code,
                        it.name,
                        it.agency,
                        it.agencyDigit,
                        it.account,
                        it.accountDigit,
                        it.accountType,
                        it.businessType,
                        it.receiveToday
                    )
                })
            } ?: CieloDataResult.Empty()
        }.onEmpty {
            return CieloDataResult.Empty()
        }.onError {
            return CieloDataResult.APIError(it.apiException)
        }
        return errorDefault
    }

    suspend fun confirmAnticipation(
        request: ArvConfirmAnticipationRequest
    ): CieloDataResult<ArvConfirmAnticipationResponse> {
        var result: CieloDataResult<ArvConfirmAnticipationResponse> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.confirmArvAnticipation(request)
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(response)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getScheduledAnticipation(): CieloDataResult<ArvScheduledAnticipation> {
        var result: CieloDataResult<ArvScheduledAnticipation> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvScheduledAnticipation()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    ArvScheduledAnticipation(
                        response.token,
                        response.rateSchedules?.map { itRate ->
                            RateSchedules(
                                itRate?.name,
                                itRate?.schedule,
                                itRate?.rate,
                                itRate?.cnpjRoot,
                                itRate?.cnpjBranch
                            )
                        },
                        response.domicile?.map { itBank ->
                            ArvBank(
                                itBank?.code,
                                itBank?.name,
                                itBank?.agency,
                                itBank?.agencyDigit,
                                itBank?.account,
                                itBank?.accountDigit,
                                itBank?.accountType,
                                itBank?.businessType
                            )
                        }
                    )
                )
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun confirmScheduledAnticipation(
        request: ArvConfirmScheduledAnticipationRequest
    ): CieloDataResult<Void> {
        var result: CieloDataResult<Void> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.confirmArvScheduledAnticipation(request)
        }.onSuccess {
            result = CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun cancelScheduledAnticipation(
        request: ArvScheduledAnticipationCancelRequest
    ): CieloDataResult<Void> {
        var result: CieloDataResult<Void> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.cancelArvScheduledAnticipation(request.negotiationType.name)
        }.onSuccess {
            result = CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getOptInStatus(): CieloDataResult<ArvOptIn> {
        var result: CieloDataResult<ArvOptIn> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getOptInStatus()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    ArvOptIn(response.eligible)
                )
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getArvScheduledContract(
        request: ArvScheduledAnticipationContractRequest
    ): CieloDataResult<ArvScheduleContract> {
        var result: CieloDataResult<ArvScheduleContract> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getArvScheduledContract(request.negotiationType.name)
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    ArvScheduleContract(
                        file = response.file,
                        size = response.size
                    )
                )
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getBranchContracts(): CieloDataResult<ArvBranchesContracts> {
        var result: CieloDataResult<ArvBranchesContracts> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getBranchesContracts()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    ArvBranchesContracts(
                        total = response.total,
                        schedules = response.schedules?.map { scheduleResponse ->
                            Schedules(
                                cnpj = scheduleResponse.cnpj,
                                name = scheduleResponse.name,
                                nominalRateCielo = scheduleResponse.nominalRateCielo,
                                contractDateCielo = scheduleResponse.contractDateCielo,
                                nominalRateMarket = scheduleResponse.nominalRateMarket,
                                contractDateMarket = scheduleResponse.contractDateMarket
                            )
                        }
                    )
                )
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }
}
