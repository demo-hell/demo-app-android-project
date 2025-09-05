package br.com.mobicare.cielo.component.requiredDataField.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import org.androidannotations.annotations.res.StringRes

sealed class UiRequiredDataFieldState {
    class Success(val orderID: String) : UiRequiredDataFieldState()
    class GenericError(val error: NewErrorMessage? = null) : UiRequiredDataFieldState()
    class InvalidDataError(@StringRes val message: Int) : UiRequiredDataFieldState()
    class TokenError(val error: NewErrorMessage? = null) : UiRequiredDataFieldState()
}