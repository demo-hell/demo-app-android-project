package br.com.mobicare.cielo.newRecebaRapido.presentation.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.USER_VIEW_RECEIVE_AUTOMATIC_ONBOARDING
import br.com.mobicare.cielo.newRecebaRapido.util.UiReceiveAutoOnBoardingState
import kotlinx.coroutines.launch

class ReceiveAutomaticOnBoardingViewModel(
    private val saveUserViewHistoryUseCase: SaveUserViewHistoryUseCase
) : ViewModel() {

    private val _getFastOnBoardingMutableLiveData = MutableLiveData<UiReceiveAutoOnBoardingState>()
    val getFastOnBoardingLiveData: LiveData<UiReceiveAutoOnBoardingState> get() = _getFastOnBoardingMutableLiveData

    fun userViewReceiveAutomaticOnBoarding() {
        viewModelScope.launch {
            saveUserViewHistoryUseCase(key = USER_VIEW_RECEIVE_AUTOMATIC_ONBOARDING)
                .onSuccess {
                    _getFastOnBoardingMutableLiveData.value = UiReceiveAutoOnBoardingState.ShowHome
                }.onEmpty {
                    _getFastOnBoardingMutableLiveData.value = UiReceiveAutoOnBoardingState.ShowHome
                }
        }
    }
}