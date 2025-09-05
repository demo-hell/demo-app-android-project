package br.com.mobicare.cielo.login.firstAccess.presentation.ui.createPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult.APIError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessPayIdRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.REQUEST_ADM_PERMISSION
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.REQUEST_MANAGER_PERMISSION
import br.com.mobicare.cielo.login.firstAccess.domain.usecase.FirstAccessRegistrationUseCase
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorGeneric
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorMessage
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorNotBooting
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessSuccess
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.HideLoading
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.ShowLoading
import com.akamai.botman.CYFMonitor
import kotlinx.coroutines.launch

class FirstAccessCreatePasswordViewModel(
    private val firstAccessRegistrationUseCase: FirstAccessRegistrationUseCase
) : ViewModel() {

    private var _firstAccessLiveData = MutableLiveData<FirstAccessUiState>()
    val firstAccessLiveData: LiveData<FirstAccessUiState>
        get() = _firstAccessLiveData

    fun sendRequest(
        numberEc: String?,
        cpf: String?,
        email: String?,
        password: String,
        passwordConfirmation: String
    ) {
        _firstAccessLiveData.value = ShowLoading

        viewModelScope.launch {
            val request = getRequest(numberEc, cpf, email, password, passwordConfirmation)
            val akamaiSensorData = generateAkamaiSensor()

            firstAccessRegistrationUseCase.invoke(request, ONE_SPACE, akamaiSensorData)
                .onSuccess { responseFirstAccess ->
                    _firstAccessLiveData.value = HideLoading
                    _firstAccessLiveData.value = FirstAccessSuccess(responseFirstAccess)
                }
                .onError { responseError ->
                    _firstAccessLiveData.value = HideLoading
                    onProcessError(responseError)
                }
                .onEmpty {
                    _firstAccessLiveData.value = HideLoading
                    _firstAccessLiveData.value = FirstAccessErrorGeneric
                }
        }
    }

    private fun onProcessError(apiError: APIError) {
        val error = apiError.apiException.newErrorMessage

        when (error.flagErrorCode) {
            ERROR_NOT_BOOTING -> {
                _firstAccessLiveData.value = FirstAccessErrorNotBooting
            }

            REQUEST_ADM_PERMISSION.errorType, REQUEST_MANAGER_PERMISSION.errorType -> {
                _firstAccessLiveData.value = FirstAccessErrorMessage(error.message, error.flagErrorCode)
            }

            else -> {
                _firstAccessLiveData.value = FirstAccessErrorGeneric
            }
        }
    }

    private fun generateAkamaiSensor(): String? {
        return CYFMonitor.getSensorData()
    }

    private fun getRequest(
        numberEc: String?,
        cpf: String?,
        email: String?,
        password: String,
        passwordConfirmation: String
    ): FirstAccessRegistrationRequest {
        return FirstAccessRegistrationRequest(
            pid = FirstAccessPayIdRequest(merchantId = numberEc),
            cpf = cpf,
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation
        )
    }
}