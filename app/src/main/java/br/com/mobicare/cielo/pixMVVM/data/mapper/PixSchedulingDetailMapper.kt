package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixSchedulingDetailResponse
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixScheduleFrequencyTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail

fun PixSchedulingDetailResponse.toEntity() =
    PixSchedulingDetail(
        idAccount = idAccount,
        idEndToEnd = idEndToEnd,
        payeeName = payeeName,
        payeeDocumentNumber = payeeDocumentNumber,
        payeeBankName = payeeBankName,
        finalAmount = finalAmount,
        message = message,
        transactionType = PixExtractType.find(transactionType),
        schedulingCreationDate = schedulingCreationDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        schedulingCancellationDate = schedulingCancellationDate?.parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
        schedulingDate = schedulingDate?.parseToZonedDateTime(LONG_TIME_NO_UTC),
        scheduledEndDate = scheduledEndDate?.parseToZonedDateTime(LONG_TIME_NO_UTC),
        schedulingCode = schedulingCode,
        merchantNumber = merchantNumber,
        documentNumber = documentNumber,
        totalScheduled = totalScheduled,
        totalScheduledProcessed = totalScheduledProcessed,
        totalScheduledErrors = totalScheduledErrors,
        frequencyTime = PixScheduleFrequencyTime.find(frequencyTime),
        status = PixTransactionStatus.find(status),
        type = PixType.find(type),
        enable = enable?.toEntity(),
    )
