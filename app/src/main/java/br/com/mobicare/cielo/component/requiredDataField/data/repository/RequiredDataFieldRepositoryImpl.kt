package br.com.mobicare.cielo.component.requiredDataField.data.repository

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.domain.datasource.RequiredDataFieldDataSource
import br.com.mobicare.cielo.component.requiredDataField.domain.repository.RequiredDataFieldRepository

class RequiredDataFieldRepositoryImpl(
    val dataSource: RequiredDataFieldDataSource
) : RequiredDataFieldRepository {

    override suspend fun postUpdateData(
        otpCode: String,
        data: OrdersRequest
    ) = dataSource.postUpdateData(otpCode, data)

}