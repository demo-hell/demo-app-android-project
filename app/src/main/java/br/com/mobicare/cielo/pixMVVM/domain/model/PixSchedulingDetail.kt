package br.com.mobicare.cielo.pixMVVM.domain.model

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixScheduleFrequencyTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import java.time.ZonedDateTime

data class PixSchedulingDetail(
    val idAccount: Int? = null,
    val idEndToEnd: String? = null,
    val payeeName: String? = null,
    val payeeDocumentNumber: String? = null,
    val payeeBankName: String? = null,
    val finalAmount: Double? = null,
    val message: String? = null,
    val transactionType: PixExtractType? = null,
    val schedulingCreationDate: ZonedDateTime? = null,
    val schedulingCancellationDate: ZonedDateTime? = null,
    val schedulingDate: ZonedDateTime? = null,
    val scheduledEndDate: ZonedDateTime? = null,
    val schedulingCode: String? = null,
    val merchantNumber: String? = null,
    val documentNumber: String? = null,
    val totalScheduled: Int? = null,
    val totalScheduledProcessed: Int? = null,
    val totalScheduledErrors: Int? = null,
    val frequencyTime: PixScheduleFrequencyTime? = null,
    val status: PixTransactionStatus? = null,
    val type: PixType? = null,
    val enable: PixEnable? = null,
)
