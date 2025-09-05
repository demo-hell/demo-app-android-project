package br.com.mobicare.cielo.arv.presentation.router

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvRouterState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.ANTECIPE_VENDAS
import kotlinx.coroutines.launch

class ArvRouterViewModel constructor(
    private val getUserViewHistory: GetUserViewHistoryUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private val _arvRouterLiveData = MutableLiveData<UiArvRouterState>()
    val arvRouterLiveData: LiveData<UiArvRouterState> get() = _arvRouterLiveData

    fun handleInitialFlow(arvAnticipation: ArvAnticipation?) {
        viewModelScope.launch {
            getFeatureTogglePreference(key = ANTECIPE_VENDAS).onSuccess { isShow ->
                if (isShow) {
                    handleArvAnticipationFlow(arvAnticipation)
                } else {
                    _arvRouterLiveData.value = UiArvRouterState.ShowUnavailableService
                }
            }
        }
    }

    private suspend fun handleArvAnticipationFlow(arvAnticipation: ArvAnticipation?) {
        if (arvAnticipation?.isFromCardHomeFlow == true) {
            _arvRouterLiveData.value =
                UiArvRouterState.ShowArvSingleAnticipation(arvAnticipation)
        } else {
            getUserViewArvOnboarding()
        }
    }

    private suspend fun getUserViewArvOnboarding() {
        getUserViewHistory(key = ArvConstants.USER_VIEW_ARV_ONBOARDING)
            .onSuccess { onboardingCompleted ->
                _arvRouterLiveData.value =
                    if (onboardingCompleted) {
                        UiArvRouterState.ShowHome
                    } else {
                        UiArvRouterState.ShowOnboarding
                    }
            }.onEmpty {
                _arvRouterLiveData.value = UiArvRouterState.ShowOnboarding
            }
    }
}
