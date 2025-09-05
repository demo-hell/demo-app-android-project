package br.com.mobicare.cielo.posVirtual.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.posVirtual.domain.model.Solutions

interface PosVirtualAccreditationRepository {

    suspend fun getOffers(additionalProduct: String? = null): CieloDataResult<OfferResponse>

    suspend fun getBrands(): CieloDataResult<Solutions>

    suspend fun postCreateOrder(
        otpCode: String,
        request: OrdersRequest
    ): CieloDataResult<String>

}