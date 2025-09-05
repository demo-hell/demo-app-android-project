package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.parseToLocalDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixAuthorizationStatusResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus

fun PixAuthorizationStatusResponse.toEntity() = PixAuthorizationStatus(
    status = PixStatus.find(status),
    beginTime = beginTime?.parseToLocalDateTime()
)