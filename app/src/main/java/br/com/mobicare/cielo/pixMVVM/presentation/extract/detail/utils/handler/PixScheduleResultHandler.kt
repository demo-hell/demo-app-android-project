package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult

class PixScheduleResultHandler {
    private lateinit var data: PixSchedulingDetail

    operator fun invoke(data: PixSchedulingDetail): PixScheduleUiResult {
        this.data = data

        return when {
            isScheduleCanceled || isRecurrenceCanceled -> PixScheduleUiResult.TransferScheduleCanceled(data)
            isRecurrence -> PixScheduleUiResult.RecurrentTransferScheduled(data)
            else -> PixScheduleUiResult.TransferScheduled(data)
        }
    }

    private val isScheduleCanceled
        get() =
            data.type == PixType.SCHEDULE_DEBIT &&
                data.status == PixTransactionStatus.CANCELLED

    private val isRecurrenceCanceled
        get() =
            data.type == PixType.SCHEDULE_RECURRENCE_DEBIT &&
                data.status == PixTransactionStatus.CANCELLED

    private val isRecurrence
        get() = data.type == PixType.SCHEDULE_RECURRENCE_DEBIT
}
