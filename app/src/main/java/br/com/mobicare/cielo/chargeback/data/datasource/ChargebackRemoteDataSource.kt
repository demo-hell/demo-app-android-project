package br.com.mobicare.cielo.chargeback.data.datasource

import br.com.mobicare.cielo.chargeback.data.datasource.remote.ChargebackServerApi
import br.com.mobicare.cielo.chargeback.data.mapper.MapperChargeback
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentSenderParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocument
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocumentSender
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.utils.parseToLocalDate

class ChargebackRemoteDataSource(
    private val serverApi: ChargebackServerApi,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getChargebackList(params: ChargebackListParams): CieloDataResult<Chargebacks> {
        var result: CieloDataResult<Chargebacks> = CieloDataResult
            .APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverApi.getChargebackList(
                page = params.page,
                pageSize = params.pageSize,
                status = params.status,
                orderBy = params.orderBy,
                order = params.order,
                initialDate = params.initDate,
                finalDate = params.finalDate,
                cardBrand = params.cardBrandCode,
                processCode = params.processCode,
                reasonCode = params.reasonCode,
                caseId = params.idCase,
                tid = params.tid,
                nsu = params.nsu,
                disputeStatus = params.disputeStatus
            )
        }.onSuccess { response ->
            result = MapperChargeback.mapToChargebacks(response.body())?.let {
                CieloDataResult.Success(it)
            } ?: result
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty { return result }
        return result
    }

    suspend fun getChargebackLifecycle(caseId: Int): CieloDataResult<List<Lifecycle>> {
        var result: CieloDataResult<List<Lifecycle>> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getChargebackLifecycle(caseId)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { lifecycleResponseList ->
                CieloDataResult.Success(
                    lifecycleResponseList.map {
                        Lifecycle(it.action, it.actionDate?.parseToLocalDate())
                    }.toList()
                )
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getChargebackDocument(params: ChargebackDocumentParams): CieloDataResult<ChargebackDocument> {
        var result: CieloDataResult<ChargebackDocument> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getChargebackDocument(
                merchantId = params.merchantId,
                chargebackId = params.chargebackId
            )
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { documentResponse ->
                CieloDataResult.Success(
                    ChargebackDocument(
                        code = documentResponse.code,
                        message = documentResponse.message,
                        fileName = documentResponse.fileName,
                        file = documentResponse.file,
                        inclusionDate = documentResponse.inclusionDate?.parseToLocalDate()
                    )
                )
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getChargebackDocumentSender(params: ChargebackDocumentSenderParams): CieloDataResult<ChargebackDocumentSender> {
        var result: CieloDataResult<ChargebackDocumentSender> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        val apiResult = safeApiCaller.safeApiCall {
            serverApi.getChargebackDocumentSender(
                merchantId = params.merchantId,
                documentId = params.documentId
            )
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { documentResponse ->
                CieloDataResult.Success(
                    ChargebackDocumentSender(
                        dateInclusion = documentResponse.dateInclusion,
                        nameFile = documentResponse.nameFile,
                        code = documentResponse.code,
                        message = documentResponse.message,
                        fileBase64 = documentResponse.fileBase64
                    )
                )
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }
}