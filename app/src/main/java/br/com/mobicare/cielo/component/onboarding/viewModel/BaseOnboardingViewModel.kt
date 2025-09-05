package br.com.mobicare.cielo.component.onboarding.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import kotlinx.coroutines.launch

class BaseOnboardingViewModel(
    private val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase
) : ViewModel() {

    private val _uiOnboardingState = MutableLiveData<Boolean>()
    val uiOnboardingState: LiveData<Boolean> get() = _uiOnboardingState

    fun saveViewOnboarding(key: String) {
        viewModelScope.launch {
            saveUserViewHistoryUseCase.invoke(key)
                .onSuccess {
                    _uiOnboardingState.value = true
                }
                .onEmpty {
                    _uiOnboardingState.value = true
                }
        }
    }

}