package br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAccountBalance
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.home.models.PixAccountBalanceStore
import br.com.mobicare.cielo.pixMVVM.presentation.home.utils.AccountBalanceUiState
import kotlinx.coroutines.launch

open class PixAccountBalanceViewModel(
    private val getPixAccountBalanceUseCase: GetPixAccountBalanceUseCase
) : ViewModel() {

    private val _accountBalanceUiState = MutableLiveData<AccountBalanceUiState>()
    val accountBalanceUiState: LiveData<AccountBalanceUiState> = _accountBalanceUiState

    private var _accountBalanceStore = PixAccountBalanceStore()
    val accountBalanceStore get() = _accountBalanceStore

    private var _showAccountBalance = false
    val showAccountBalance get() = _showAccountBalance

    fun toggleShowAccountBalanceValue() {
        _showAccountBalance = _showAccountBalance.not()
    }

    fun loadAccountBalance() {
        if (_accountBalanceUiState.value != AccountBalanceUiState.Loading) {
            viewModelScope.launch {
                _accountBalanceUiState.value = AccountBalanceUiState.Loading

                getPixAccountBalanceUseCase()
                    .onSuccess {
                        updateAccountBalanceStore(it)
                        _accountBalanceUiState.value = AccountBalanceUiState.Success(it)
                    }.onError {
                        _accountBalanceUiState.value =
                            AccountBalanceUiState.Error(it.apiException.message)
                    }.onEmpty {
                        _accountBalanceUiState.value = AccountBalanceUiState.Error()
                    }
            }
        }
    }

    private fun updateAccountBalanceStore(accountBalance: PixAccountBalance) {
        _accountBalanceStore = _accountBalanceStore.copy(
            balance = accountBalance.currentBalance,
            updatedAt = accountBalance.timeOfRequest
        )
    }

}