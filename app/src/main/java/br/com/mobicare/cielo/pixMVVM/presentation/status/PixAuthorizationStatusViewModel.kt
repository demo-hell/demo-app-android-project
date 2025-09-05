package br.com.mobicare.cielo.pixMVVM.presentation.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixAuthorizationStatus
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAuthorizationStatusUseCase
import kotlinx.coroutines.launch

class PixAuthorizationStatusViewModel(
    private val getPixAuthorizationStatusUseCase: GetPixAuthorizationStatusUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<PixAuthorizationStatusUiState>()
    val uiState get(): LiveData<PixAuthorizationStatusUiState> = _uiState

    fun getPixAuthorizationStatus() {
        viewModelScope.launch {
            _uiState.postValue(PixAuthorizationStatusUiState.Loading)

            getPixAuthorizationStatusUseCase()
                .onSuccess {
                    setSuccessState(it)
                }.onEmpty {
                    _uiState.postValue(PixAuthorizationStatusUiState.Error())
                }.onError {
                    _uiState.postValue(PixAuthorizationStatusUiState.Error(it.apiException.message))
                }
        }
    }

    private fun setSuccessState(result: PixAuthorizationStatus) {
        _uiState.postValue(
            when (result.status) {
                PixStatus.ACTIVE -> PixAuthorizationStatusUiState.Active(result)
                PixStatus.WAITING_ACTIVATION -> PixAuthorizationStatusUiState.WaitingActivation(result)
                else -> PixAuthorizationStatusUiState.Pending(result)
            }
        )
    }

}