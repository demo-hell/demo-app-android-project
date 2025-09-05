package br.com.mobicare.cielo.chargeback.presentation.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterCardBrand
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterProcess
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilters
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackFiltersUseCase
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import kotlinx.coroutines.launch

class ChargebackFiltersViewModel(
    private val filtersUseCase: GetChargebackFiltersUseCase
): ViewModel() {

    private val _uiState = MutableLiveData<UiState<ChargebackFilters>>()
    val uiState : LiveData<UiState<ChargebackFilters>> get() = _uiState

    fun getChargebackFilters(){
        viewModelScope.launch {
            _uiState.postValue(UiState.Loading)
            filtersUseCase.invoke()
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