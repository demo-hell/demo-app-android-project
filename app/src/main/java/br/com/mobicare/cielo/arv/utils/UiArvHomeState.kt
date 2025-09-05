package br.com.mobicare.cielo.arv.utils

import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UiArvHomeState {
    object ShowLoadingArvNegotiation : UiArvHomeState()
    object HideLoadingArvNegotiation : UiArvHomeState()
    object ShowLoadingArvScheduledAnticipation : UiArvHomeState()
    object HideLoadingArvScheduledAnticipation : UiArvHomeState()

    data class SuccessArvScheduledNegotiation(val anticipation: ArvScheduledAnticipation) :
        UiArvHomeState()

    data class ErrorArvNegotiation(val message: Any, val error: NewErrorMessage? = null) :
        UiArvHomeState()

    data class NotEligible(val error: NewErrorMessage? = null) : UiArvHomeState()
    object ClosedMarket : UiArvHomeState()
    object CorporateDesk : UiArvHomeState()
    object NoValuesToAnticipate : UiArvHomeState()
}

sealed class UiArvUserState {
    object ShowLoadingMeInformation : UiArvUserState()
    object HideLoadingMeInformation : UiArvUserState()
    data class SuccessMeInformation(
        val name: String,
        val numberEstablishment: String,
        val cpnjEstablishment: String
    ) : UiArvUserState()

    data class ErrorMeInformation(val message: Any) : UiArvUserState()
}

sealed class UiArvTypeState {

    data class SetupAnticipationSingle(
        val anticipationSingleEnable: Boolean = false,
        @DrawableRes val anticipationSingleBackground: Int
    ) : UiArvTypeState()
}

sealed class UiArvScheduledAnticipationState {
    object FullHiredByRoot : UiArvScheduledAnticipationState()
    object CieloOnlyHiredByRoot : UiArvScheduledAnticipationState()
    object MarketOnlyHiredByRoot : UiArvScheduledAnticipationState()
    object FullHired : UiArvScheduledAnticipationState()
    object CieloOnlyHired : UiArvScheduledAnticipationState()
    object MarketOnlyHired : UiArvScheduledAnticipationState()
    object NotHired : UiArvScheduledAnticipationState()
    object CieloByRootMarketByBranch : UiArvScheduledAnticipationState()
    object MarketByRootCieloByBranch : UiArvScheduledAnticipationState()
    object DisabledScheduled : UiArvScheduledAnticipationState()
    object ShowLoading : UiArvScheduledAnticipationState()
}

sealed class OptInState {
    object MissingOptIn : OptInState()
}