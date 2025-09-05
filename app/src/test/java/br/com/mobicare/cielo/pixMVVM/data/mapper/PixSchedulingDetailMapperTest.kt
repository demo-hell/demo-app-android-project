package br.com.mobicare.cielo.pixMVVM.data.mapper

import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.LONG_TIME_WITH_MILLIS_NO_UTC
import br.com.mobicare.cielo.commons.utils.parseToZonedDateTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixScheduleFrequencyTime
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixSchedulingDetailMapperTest {
    private val response = PixTransactionsFactory.SchedulingDetail.response

    private val expectedResult =
        PixSchedulingDetail(
            idAccount = 0,
            idEndToEnd = "string",
            payeeName = "string",
            payeeDocumentNumber = "string",
            payeeBankName = "string",
            finalAmount = 0.0,
            message = "string",
            transactionType = PixExtractType.SCHEDULE_DEBIT,
            schedulingCreationDate = "2024-01-30T17:47:38.451".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
            schedulingCancellationDate = "2024-01-30T17:47:38.451".parseToZonedDateTime(LONG_TIME_WITH_MILLIS_NO_UTC),
            schedulingDate = "2024-01-30T17:47:38".parseToZonedDateTime(LONG_TIME_NO_UTC),
            scheduledEndDate = "2024-01-30T17:47:38".parseToZonedDateTime(LONG_TIME_NO_UTC),
            schedulingCode = "string",
            merchantNumber = "string",
            documentNumber = "string",
            totalScheduled = 0,
            totalScheduledProcessed = 0,
            totalScheduledErrors = 0,
            frequencyTime = PixScheduleFrequencyTime.MONTHLY,
            status = PixTransactionStatus.EXECUTED,
            type = PixType.SCHEDULE_DEBIT,
            enable =
                PixEnable(
                    refund = false,
                    cancelSchedule = false,
                    requestAnalysis = false,
                ),
        )

    @Test
    fun `it should map response to entity correctly`() {
        val result = response.toEntity()

        assertThat(result).isEqualTo(expectedResult)
    }
}
