package br.com.mobicare.cielo.commons.utils.token.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.utils.token.domain.useCase.GetTokenUseCase
import br.com.mobicare.cielo.commons.utils.token.utils.UiTokenState
import kotlinx.coroutines.launch

class HandlerValidationTokenViewModel constructor(
    private val getTokenUseCase: GetTokenUseCase
) : ViewModel() {

    private val _uiTokenState = MutableLiveData<UiTokenState>()
    val uiTokenState: LiveData<UiTokenState> get() = _uiTokenState

    fun getToken() {
        viewModelScope.launch {
            getTokenUseCase.invoke()
                .onSuccess {
                    _uiTokenState.value = UiTokenState.Success(it)
                }.onEmpty {
                    _uiTokenState.value = UiTokenState.Error
                    _uiTokenState.value = UiTokenState.Default
                }.onError {
                    _uiTokenState.value = UiTokenState.ConfigureToken(it.apiException.newErrorMessage)
                }
        }
    }
}