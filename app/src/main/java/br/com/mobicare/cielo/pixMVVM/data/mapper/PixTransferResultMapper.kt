package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixTransferResultResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult

fun PixTransferResultResponse.toEntity() =
    PixTransferResult(
        endToEndId = endToEndId,
        transactionCode = transactionCode,
        transactionDate = transactionDate?.clearDate()?.parseToZonedDateTime(LONG_TIME_NO_UTC),
        transactionStatus = PixTransactionStatus.find(transactionStatus),
        schedulingDate = schedulingDate?.clearDate()?.parseToZonedDateTime(LONG_TIME_NO_UTC),
        schedulingCode = schedulingCode,
    )
