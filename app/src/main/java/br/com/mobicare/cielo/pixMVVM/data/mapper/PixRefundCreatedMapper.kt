package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixRefundCreatedResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundCreated

fun PixRefundCreatedResponse.toEntity() = PixRefundCreated(
    idEndToEndReturn = idEndToEndReturn,
    idEndToEndOriginal = idEndToEndOriginal,
    transactionDate = transactionDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
    idAdjustment = idAdjustment,
    transactionCode = transactionCode,
    transactionStatus = PixTransactionStatus.find(transactionStatus),
    idTx = idTx
)