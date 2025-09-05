package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class PixExtractDetailCancelScheduleUIState {
    object ShowLoading : PixExtractDetailCancelScheduleUIState()

    object HideLoading : PixExtractDetailCancelScheduleUIState()

    object HideLoadingCancelSchedule : PixExtractDetailCancelScheduleUIState()

    object CancelScheduleSuccess : PixExtractDetailCancelScheduleUIState()

    object ScheduleDetailSuccess : PixExtractDetailCancelScheduleUIState()

    object ScheduleDetailPending : PixExtractDetailCancelScheduleUIState()

    data class CancelScheduleError(val error: NewErrorMessage? = null) : PixExtractDetailCancelScheduleUIState()

    data class ScheduleDetailError(val error: NewErrorMessage? = null) : PixExtractDetailCancelScheduleUIState()
}
