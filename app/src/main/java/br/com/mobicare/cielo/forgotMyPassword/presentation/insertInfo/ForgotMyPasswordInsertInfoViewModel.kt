package br.com.mobicare.cielo.forgotMyPassword.presentation.insertInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordLogin
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordPid
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.domain.useCase.PostForgotMyPasswordRecoveryPasswordUseCase
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState
import com.akamai.botman.CYFMonitor
import kotlinx.coroutines.launch

class ForgotMyPasswordInsertInfoViewModel(
    private val forgotMyPasswordRecoveryPasswordUseCase: PostForgotMyPasswordRecoveryPasswordUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val forgotPasswordUiState: LiveData<ForgotPasswordUiState<ForgotMyPassword>> get() = _forgotPasswordUiState
    private val _forgotPasswordUiState = MutableLiveData<ForgotPasswordUiState<ForgotMyPassword>>()

    fun sendRequestRecoveryPassword(userName: String) {
        _forgotPasswordUiState.value = ForgotPasswordUiState.Loading

        viewModelScope.launch {
            val request = makeRequest(userName)
            val akamaiSensorData = generateAkamaiSensor()
            forgotMyPasswordRecoveryPasswordUseCase(
                request,
                akamaiSensorData
            ).onSuccess {
                it.userName = userName
                _forgotPasswordUiState.value = ForgotPasswordUiState.Success(it)
            }.onError { error ->
                val errorCode = error.apiException.newErrorMessage.flagErrorCode
                _forgotPasswordUiState.value = if ( errorCode == ERROR_NOT_BOOTING) ForgotPasswordUiState.ErrorAkamai else ForgotPasswordUiState.Error(null)
            }
        }
    }

    private fun generateAkamaiSensor() : String? {
         return CYFMonitor.getSensorData()
    }

    private fun makeRequest(userName: String): ForgotMyPasswordRecoveryPasswordRequest {
        return ForgotMyPasswordRecoveryPasswordRequest(
            login = ForgotMyPasswordLogin(
                username = userName
            ),
            pid = ForgotMyPasswordPid(
                merchantId = ZERO.toString()
            )
        )
    }

    fun deleteUserInformation() {
        userPreferences.deleteUserInformation()
    }
}