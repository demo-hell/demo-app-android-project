package br.com.mobicare.cielo.arv.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.utils.ArvConstants.USER_VIEW_ARV_ONBOARDING
import br.com.mobicare.cielo.arv.utils.UiArvOnboardingState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import kotlinx.coroutines.launch

class ArvOnboardingViewModel constructor(
    private val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase
) : ViewModel() {

    private val _arvOnboardingMutableLiveData = MutableLiveData<UiArvOnboardingState>()
    val arvOnboardingLiveData: LiveData<UiArvOnboardingState> get() = _arvOnboardingMutableLiveData

    fun userViewArvOnboarding() {
        viewModelScope.launch {
            saveUserViewHistoryUseCase(key = USER_VIEW_ARV_ONBOARDING)
                .onSuccess {
                    _arvOnboardingMutableLiveData.value = UiArvOnboardingState.ShowHome
                }.onEmpty {
                    _arvOnboardingMutableLiveData.value = UiArvOnboardingState.ShowHome
                }
        }
    }
}