package br.com.mobicare.cielo.login.firstAccess.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse

sealed class FirstAccessUiState {

    object ShowLoading : FirstAccessUiState()

    object HideLoading : FirstAccessUiState()

    class FirstAccessSuccess(val firstAccessResult: FirstAccessResponse) : FirstAccessUiState()

    class FirstAccessErrorMessage(val message: String, val code: String) : FirstAccessUiState()

    object FirstAccessErrorGeneric: FirstAccessUiState()
    object FirstAccessErrorNotBooting: FirstAccessUiState()

}