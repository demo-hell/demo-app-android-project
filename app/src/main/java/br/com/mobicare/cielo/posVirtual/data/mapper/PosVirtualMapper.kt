package br.com.mobicare.cielo.posVirtual.data.mapper

import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualProductResponse
import br.com.mobicare.cielo.posVirtual.data.model.response.PosVirtualResponse
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtualProduct

fun PosVirtualResponse.toEntity() = PosVirtual(
    status = PosVirtualStatus.find(status),
    merchantId = merchantId,
    impersonateRequired = impersonateRequired,
    products = products?.toEntityList()
)

fun PosVirtualProductResponse.toEntity() = PosVirtualProduct(
    id = PosVirtualProductId.find(id),
    logicalNumber = logicalNumber,
    status = PosVirtualStatus.find(status)
)

fun List<PosVirtualProductResponse>.toEntityList() = map { it.toEntity() }