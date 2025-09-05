package br.com.mobicare.cielo.chargeback.presentation.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackLifecycleUseCase
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import kotlinx.coroutines.launch

class ChargebackDoneDetailsViewModel(
    private val getChargebackLifecycleUseCase: GetChargebackLifecycleUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState<List<Lifecycle>>>()
    val uiState: LiveData<UiState<List<Lifecycle>>> get() = _uiState

    fun getChargebackLifecycle(caseId: Int) {
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            getChargebackLifecycleUseCase(caseId)
                .onSuccess {
                    _uiState.postValue(UiState.Success(it))
                }.onError {
                    _uiState.postValue(UiState.Error(it.apiException.newErrorMessage))
                }.onEmpty {
                    _uiState.postValue(UiState.Empty)
                }
        }
    }

}