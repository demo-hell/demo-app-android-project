package br.com.mobicare.cielo.turboRegistration.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.TURBO_REGISTRATION
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetEligibilityUseCase
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import kotlinx.coroutines.launch

class TurboRegistrationViewModel(
    private val getEligibilityUseCase: GetEligibilityUseCase,
    private val featureToggleUseCase: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _eligibility = MutableLiveData<RegistrationResource<Boolean>>()
    val eligibility: LiveData<RegistrationResource<Boolean>> get() = _eligibility

    fun verifyUserNeedsToUpdateDocuments() {
        viewModelScope.launch {
            _eligibility.value = RegistrationResource.Loading
            featureToggleUseCase(key = TURBO_REGISTRATION, isLocal = false).onSuccess { useTurboRegistration ->
                if (useTurboRegistration) {
                    getEligibility()
                } else {
                    deleteLegalEntity()
                    _eligibility.value = RegistrationResource.Success(false)
                }
            }.onError {
                deleteLegalEntity()
                _eligibility.value = RegistrationResource.Error(it.apiException)
            }.onEmpty {
                deleteLegalEntity()
                _eligibility.value = RegistrationResource.Empty
            }
        }
    }

    private suspend fun getEligibility() {
        getEligibilityUseCase().onSuccess {
            UserPreferences.getInstance().saveLegalEntity(it.legalEntity)
            _eligibility.value = RegistrationResource.Success(it.eligible)
        }.onError {
            deleteLegalEntity()
            _eligibility.value = RegistrationResource.Error(it.apiException)
        }.onEmpty {
            deleteLegalEntity()
            _eligibility.value = RegistrationResource.Empty
        }
    }

    private fun deleteLegalEntity() {
        UserPreferences.getInstance().deleteLegalEntity()
    }
}