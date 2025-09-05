package br.com.mobicare.cielo.posVirtual.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.posVirtual.data.dataSource.remote.PosVirtualAPI
import br.com.mobicare.cielo.posVirtual.data.mapper.MapperPosVirtualBrands
import br.com.mobicare.cielo.posVirtual.domain.model.Solutions

class PosVirtualAccreditationDataSource(
    private val serverAPI: PosVirtualAPI,
    private val safeApiCaller: SafeApiCaller
) {

    suspend fun getOffers(additionalProduct: String? = null): CieloDataResult<OfferResponse> {
        lateinit var result: CieloDataResult<OfferResponse>

        val apiResult = safeApiCaller.safeApiCall {
            serverAPI.getOffer(additionalProduct)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.let { CieloDataResult.Success(it) } ?: CieloDataResult.Empty()
        }.onError {
            result = it
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun getBrands(): CieloDataResult<Solutions> {
        lateinit var result: CieloDataResult<Solutions>

        val apiResult = safeApiCaller.safeApiCall {
            serverAPI.getBrands()
        }

        apiResult.onSuccess { response ->
            result = MapperPosVirtualBrands.mapToBrands(response.body())
                ?.let { CieloDataResult.Success(it) }
                ?: CieloDataResult.Empty()
        }.onError {
            result = it
        }.onEmpty {
            result = CieloDataResult.Empty()
        }

        return result
    }

    suspend fun postCreateOrder(
        otpCode: String,
        request: OrdersRequest
    ): CieloDataResult<String> {
        lateinit var result: CieloDataResult<String>

        val apiResult = safeApiCaller.safeApiCall {
            serverAPI.postCreateOrder(otpCode, request)
        }

        apiResult.onSuccess { response ->
            result = response.body()?.orderId?.let {
                CieloDataResult.Success(it)
            } ?: CieloDataResult.Empty()
        }.onError {
            result = it
        }.onEmpty {
            result = it
        }

        return result
    }

}