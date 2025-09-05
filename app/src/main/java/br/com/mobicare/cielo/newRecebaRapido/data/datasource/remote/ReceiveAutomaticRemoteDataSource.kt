package br.com.mobicare.cielo.newRecebaRapido.data.datasource.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.newRecebaRapido.data.mapper.ReceiveAutomaticEligibilityMapper.mapToReceiveAutomaticEligibility
import br.com.mobicare.cielo.newRecebaRapido.data.mapper.ReceiveAutomaticOffersMapper.mapListOffer
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer

class ReceiveAutomaticRemoteDataSource(
    private val serverApi: ReceiveAutomaticApi,
    private val safeApiCaller: SafeApiCaller
) {
    private val errorDefault =
        CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

    suspend fun getReceiveAutomaticOffers(periodicity: String?, nextValidityPeriod: Boolean?): CieloDataResult<List<Offer>> {
        var result: CieloDataResult<List<Offer>> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getReceiveAutomaticOffers(periodicity, nextValidityPeriod)
        }.onSuccess {
            result = mapListOffer(it.body()).let { response ->
                CieloDataResult.Success(response)
            }
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun contractReceiveAutomaticOffer(params: ReceiveAutomaticContractRequest): CieloDataResult<Void> {
        var result: CieloDataResult<Void> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.contractReceiveAutomaticOffer(
               params
            )
        }.onSuccess {
            result = CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getReceiveAutomaticEligibility(): CieloDataResult<ReceiveAutomaticEligibility> {
        var result: CieloDataResult<ReceiveAutomaticEligibility> = errorDefault

        safeApiCaller.safeApiCall {
            serverApi.getReceiveAutomaticEligibility()
        }.onSuccess {
            result = it.body()?.let { response ->
                CieloDataResult.Success(
                    mapToReceiveAutomaticEligibility(response),
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