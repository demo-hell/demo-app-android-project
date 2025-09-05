package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views

import android.view.LayoutInflater
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.scheduled.PixReceiptRecurrentTransferScheduledViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.scheduled.PixReceiptTransferScheduledViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.PixStatusScheduleCanceledViewBuilder

class PixScheduleViewSelector(
    private val inflater: LayoutInflater,
) {
    operator fun invoke(result: PixScheduleUiResult) =
        when (result) {
            is PixScheduleUiResult.TransferScheduled ->
                PixReceiptTransferScheduledViewBuilder(inflater, result.data).build()
            is PixScheduleUiResult.RecurrentTransferScheduled ->
                PixReceiptRecurrentTransferScheduledViewBuilder(inflater, result.data).build()
            is PixScheduleUiResult.TransferScheduleCanceled ->
                PixStatusScheduleCanceledViewBuilder(inflater, result.data).build()
        }
}
