package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result

import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail

sealed class PixScheduleUiResult(val data: PixSchedulingDetail) {
    data class TransferScheduled(
        private val schedulingDetail: PixSchedulingDetail,
    ) : PixScheduleUiResult(schedulingDetail)

    data class TransferScheduleCanceled(
        private val schedulingDetail: PixSchedulingDetail,
    ) : PixScheduleUiResult(schedulingDetail)

    data class RecurrentTransferScheduled(
        private val schedulingDetail: PixSchedulingDetail,
    ) : PixScheduleUiResult(schedulingDetail)
}
