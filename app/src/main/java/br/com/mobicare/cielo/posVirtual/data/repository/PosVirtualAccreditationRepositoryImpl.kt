package br.com.mobicare.cielo.posVirtual.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.posVirtual.data.dataSource.PosVirtualAccreditationDataSource
import br.com.mobicare.cielo.posVirtual.domain.model.Solutions
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository

class PosVirtualAccreditationRepositoryImpl(
    private val dataSource: PosVirtualAccreditationDataSource
) : PosVirtualAccreditationRepository {

    override suspend fun getOffers(additionalProduct: String?): CieloDataResult<OfferResponse> =
        dataSource.getOffers(additionalProduct)

    override suspend fun getBrands(): CieloDataResult<Solutions> =
        dataSource.getBrands()

    override suspend fun postCreateOrder(
        otpCode: String,
        request: OrdersRequest
    ): CieloDataResult<String> =
        dataSource.postCreateOrder(otpCode, request)

}