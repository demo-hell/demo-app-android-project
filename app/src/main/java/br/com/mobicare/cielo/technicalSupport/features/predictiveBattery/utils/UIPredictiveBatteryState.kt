package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage

sealed class UIPredictiveBatteryState {
    object HideLoading : UIPredictiveBatteryState()

    object ShowLoading : UIPredictiveBatteryState()

    object ServiceAvailable : UIPredictiveBatteryState()

    object UnavailableService : UIPredictiveBatteryState()

    object SuccessRequestExchange : UIPredictiveBatteryState()

    object SuccessRefuseExchange : UIPredictiveBatteryState()

    object ValidateLogicNumberError : UIPredictiveBatteryState()

    data class RefuseExchangeError(val errorMessage: NewErrorMessage? = null) : UIPredictiveBatteryState()

    data class RequestExchangeError(val errorMessage: NewErrorMessage? = null) : UIPredictiveBatteryState()
}
