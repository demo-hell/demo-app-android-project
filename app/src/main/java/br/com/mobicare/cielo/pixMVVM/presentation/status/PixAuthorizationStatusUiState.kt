package br.com.mobicare.cielo.pixMVVM.presentation.status

import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus

sealed class PixAuthorizationStatusUiState {
    object Loading : PixAuthorizationStatusUiState()
    data class Error(val message: String? = null) : PixAuthorizationStatusUiState()

    abstract class Success(val data: PixAuthorizationStatus) : PixAuthorizationStatusUiState()
    data class Active(val result: PixAuthorizationStatus): Success(result)
    data class WaitingActivation(val result: PixAuthorizationStatus): Success(result)
    data class Pending(val result: PixAuthorizationStatus): Success(result)
}
