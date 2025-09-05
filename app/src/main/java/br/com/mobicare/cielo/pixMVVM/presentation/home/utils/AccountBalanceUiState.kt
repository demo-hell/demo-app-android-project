package br.com.mobicare.cielo.pixMVVM.presentation.home.utils

import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance

sealed class AccountBalanceUiState {
    object Loading : AccountBalanceUiState()
    data class Error(val message: String? = null) : AccountBalanceUiState()
    data class Success(val accountBalance: PixAccountBalance) : AccountBalanceUiState()
}