package br.com.mobicare.cielo.arv.utils

import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage


sealed class UiArvSingleFeatureToggleState {
    object Disabled : UiArvSingleFeatureToggleState()
}

sealed class UiArvSingleState {
    data class SuccessArvSingle(val anticipation: ArvAnticipation) : UiArvSingleState()
    object NoValuesToAnticipate : UiArvSingleState()
    object Disabled : UiArvSingleState()
}

sealed class UiArvSingleWithDateState {
    object ShowLoadingArvSingleWithDate : UiArvSingleWithDateState()
    object HideLoadingArvSingleWithDate : UiArvSingleWithDateState()
    data class SuccessArvSingleWithDate(val anticipation: ArvAnticipation) : UiArvSingleWithDateState()
    data class ErrorArvSingleWithDateMessage(val message: Any) : UiArvSingleWithDateState()
    data class ErrorArvSingleWithDate(val error: NewErrorMessage, val message: Any) : UiArvSingleWithDateState()

    object NoValuesToAnticipate : UiArvSingleWithDateState()
}

sealed class UiArvSingleWithValueState {
    object ShowLoadingArvSingleWithValue : UiArvSingleWithValueState()
    object HideLoadingArvSingleWithValue : UiArvSingleWithValueState()
    data class SuccessArvSingleWithValue(val anticipation: ArvAnticipation) : UiArvSingleWithValueState()
    data class ErrorArvSingleWithValueMessage(val message: Any) : UiArvSingleWithValueState()
    data class ErrorArvSingleWithValue(val error: NewErrorMessage, val message: Any) : UiArvSingleWithValueState()
}