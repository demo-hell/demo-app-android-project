package br.com.mobicare.cielo.pixMVVM.presentation.key.utils

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey

sealed class PixInsertAllKeysUIState {

    object ShowLoading : PixInsertAllKeysUIState()

    object HideLoading : PixInsertAllKeysUIState()

    data class Success(val pixValidateKey: PixValidateKey) : PixInsertAllKeysUIState()

    data class GenericError(val errorMessage: NewErrorMessage? = null) : PixInsertAllKeysUIState()

    data class InputError(val errorMessage: NewErrorMessage? = null) : PixInsertAllKeysUIState()

    data class UnavailableService(val errorMessage: NewErrorMessage? = null) : PixInsertAllKeysUIState()

}
