package br.com.mobicare.cielo.cieloFarol.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.cieloFarol.data.model.response.CieloFarolResponse
import br.com.mobicare.cielo.cieloFarol.domain.useCase.GetCieloFarolUseCase
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState
import br.com.mobicare.cielo.cieloFarol.utils.uiState.FarolUiState.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import kotlinx.coroutines.launch

class CieloFarolViewModel(
    private val getCieloFarolUseCase: GetCieloFarolUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _faroilUiState = MutableLiveData<FarolUiState<CieloFarolResponse>>()
    val farolUiState: LiveData<FarolUiState<CieloFarolResponse>> get() = _faroilUiState

    fun getCieloFarol() {
        val merchantId = userPreferences.userInformation?.merchant?.id

        viewModelScope.launch {
            getCieloFarolUseCase(userPreferences.token, merchantId)
                .onSuccess {
                    _faroilUiState.postValue(Success(it))
                }.onError {
                    _faroilUiState.postValue(Error(it.apiException.message))
                }.onEmpty {
                    _faroilUiState.postValue(Empty)
                }
        }
    }
}