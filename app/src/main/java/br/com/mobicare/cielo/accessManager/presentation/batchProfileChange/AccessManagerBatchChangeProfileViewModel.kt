package br.com.mobicare.cielo.accessManager.presentation.batchProfileChange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.accessManager.domain.usecase.GetCustomActiveProfilesUseCase
import br.com.mobicare.cielo.accessManager.domain.usecase.PostAssignRoleUseCase
import br.com.mobicare.cielo.accessManager.utils.AccessManagerBatchChangeProfileUiState
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pix.constants.ACTIVE
import kotlinx.coroutines.launch

class AccessManagerBatchChangeProfileViewModel(
    private val getCustomActiveProfilesUseCase: GetCustomActiveProfilesUseCase,
    private val postAssignRoleUseCase: PostAssignRoleUseCase,
    private val featureTogglePreference: FeatureTogglePreference
) : ViewModel() {

    private var customProfiles: List<CustomProfiles>? = null

    private val _accessManagerBatchChangeProfileLiveData =
        MutableLiveData<AccessManagerBatchChangeProfileUiState>()
    val accessManagerBatchChangeProfileLiveData: LiveData<AccessManagerBatchChangeProfileUiState>
        get() = _accessManagerBatchChangeProfileLiveData

    fun showTechnicalProfile(): Boolean {
        return featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO)
    }

    fun showCustomProfiles(): Boolean {
        return customProfiles.isNullOrEmpty().not()
    }

    fun getCustomActiveProfiles(customProfileEnabled: Boolean) {
        if (featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)
            && customProfileEnabled
        ) {

            viewModelScope.launch {
                getCustomActiveProfilesUseCase(Text.CUSTOM, ACTIVE)
                    .onSuccess {
                        customProfiles = it
                    }.onError {
                        customProfiles = null
                    }
            }
        }
    }

    fun assignRole(usersId: List<String>, role: String, otpCode: String) {
        viewModelScope.launch {
            postAssignRoleUseCase(usersId, role, otpCode)
                .onSuccess {
                    _accessManagerBatchChangeProfileLiveData.value =
                        AccessManagerBatchChangeProfileUiState.AssignRoleSuccess()
                }.onEmpty {
                    _accessManagerBatchChangeProfileLiveData.value =
                        AccessManagerBatchChangeProfileUiState.AssignRoleSuccess()
                }.onError {
                    _accessManagerBatchChangeProfileLiveData.value =
                        AccessManagerBatchChangeProfileUiState.AssignRoleError()
                }
        }
    }

}