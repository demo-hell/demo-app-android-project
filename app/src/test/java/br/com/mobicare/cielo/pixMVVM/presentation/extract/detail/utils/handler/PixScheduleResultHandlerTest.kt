package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixScheduleResultHandlerTest {
    private val entity = PixTransactionsFactory.SchedulingDetail.entity

    private val handler = PixScheduleResultHandler()

    @Test
    fun `it should return PixScheduleUiResult_TransferScheduleCanceled result`() {
        val scheduleCanceledResult =
            handler.invoke(
                entity.copy(
                    type = PixType.SCHEDULE_DEBIT,
                    status = PixTransactionStatus.CANCELLED,
                ),
            )

        val recurrenceCanceledResult =
            handler.invoke(
                entity.copy(
                    type = PixType.SCHEDULE_RECURRENCE_DEBIT,
                    status = PixTransactionStatus.CANCELLED,
                ),
            )

        assertThat(scheduleCanceledResult)
            .isInstanceOf(PixScheduleUiResult.TransferScheduleCanceled::class.java)
        assertThat(recurrenceCanceledResult)
            .isInstanceOf(PixScheduleUiResult.TransferScheduleCanceled::class.java)
    }

    @Test
    fun `it should return PixScheduleUiResult_RecurrentTransferScheduled result`() {
        val result =
            handler.invoke(
                entity.copy(
                    type = PixType.SCHEDULE_RECURRENCE_DEBIT,
                ),
            )

        assertThat(result).isInstanceOf(PixScheduleUiResult.RecurrentTransferScheduled::class.java)
    }

    @Test
    fun `it should return PixScheduleUiResult_TransferScheduled result`() {
        val result =
            handler.invoke(
                entity.copy(
                    type = PixType.SCHEDULE_DEBIT,
                ),
            )

        assertThat(result).isInstanceOf(PixScheduleUiResult.TransferScheduled::class.java)
    }
}
