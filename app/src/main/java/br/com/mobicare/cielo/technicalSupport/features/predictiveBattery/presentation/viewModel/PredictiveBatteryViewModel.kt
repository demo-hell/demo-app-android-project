package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.domain.useCase.PostChangeBatteryUseCase
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryConstants.PREDICTIVE_BATTERY_PARAM_URL_DEEP_LINK
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.UIPredictiveBatteryState
import kotlinx.coroutines.launch

class PredictiveBatteryViewModel(
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val postChangeBatteryUseCase: PostChangeBatteryUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<UIPredictiveBatteryState>()
    val uiState: LiveData<UIPredictiveBatteryState> get() = _uiState

    private var _logicalNumber = EMPTY
    val logicalNumber get() = _logicalNumber

    fun start(dataFromActivity: Any?) {
        viewModelScope.launch {
            verifyServiceAvailable(dataFromActivity)
        }
    }

    private suspend fun verifyServiceAvailable(data: Any?) {
        getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY).onSuccess { ftPredictiveBattery ->
            if (ftPredictiveBattery) {
                validateLogicNumber(data)
            } else {
                _uiState.value = UIPredictiveBatteryState.UnavailableService
            }
        }
    }

    private fun validateLogicNumber(data: Any?) {
        _uiState.value = data?.let {
            _logicalNumber =
                when (data) {
                    is String -> data
                    is DeepLinkModel -> data.params[PREDICTIVE_BATTERY_PARAM_URL_DEEP_LINK].orEmpty()
                    else -> EMPTY
                }

            if (logicalNumber.isNotBlank()) {
                UIPredictiveBatteryState.ServiceAvailable
            } else {
                UIPredictiveBatteryState.ValidateLogicNumberError
            }
        } ?: UIPredictiveBatteryState.ValidateLogicNumberError
    }

    fun requestExchange(phoneNumber: String) {
        _uiState.value = UIPredictiveBatteryState.ShowLoading
        viewModelScope.launch {
            postChangeBattery(
                BatteryRequest(
                    equipmentId = logicalNumber,
                    chargeBattery = true,
                    phone = phoneNumber.removeNonNumbers(),
                ),
                isRequestExchange = true,
            )
        }
    }

    fun refuseExchange() {
        _uiState.value = UIPredictiveBatteryState.ShowLoading
        viewModelScope.launch {
            postChangeBattery(
                BatteryRequest(
                    equipmentId = logicalNumber,
                    chargeBattery = false,
                ),
                isRequestExchange = false,
            )
        }
    }

    private suspend fun postChangeBattery(
        request: BatteryRequest,
        isRequestExchange: Boolean,
    ) {
        postChangeBatteryUseCase(request)
            .onSuccess {
                setUiStateHideLoading()
                setUiStateSuccess(isRequestExchange)
            }.onEmpty {
                setUiStateHideLoading()
                setUiStateError(isRequestExchange, null)
            }.onError {
                handleError(it.apiException.newErrorMessage, isRequestExchange)
            }
    }

    private suspend fun handleError(
        errorMessage: NewErrorMessage,
        isRequestExchange: Boolean,
    ) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = errorMessage,
            onHideLoading = {
                setUiStateHideLoading()
            },
            onErrorAction = {
                setUiStateHideLoading()
                setUiStateError(isRequestExchange, errorMessage)
            },
        )
    }

    private fun setUiStateHideLoading() {
        _uiState.value = UIPredictiveBatteryState.HideLoading
    }

    private fun setUiStateSuccess(isRequestExchange: Boolean) {
        _uiState.value =
            if (isRequestExchange) {
                UIPredictiveBatteryState.SuccessRequestExchange
            } else {
                UIPredictiveBatteryState.SuccessRefuseExchange
            }
    }

    private fun setUiStateError(
        isRequestExchange: Boolean,
        errorMessage: NewErrorMessage?,
    ) {
        _uiState.value =
            if (isRequestExchange) {
                UIPredictiveBatteryState.RequestExchangeError(errorMessage)
            } else {
                UIPredictiveBatteryState.RefuseExchangeError(errorMessage)
            }
    }
}
