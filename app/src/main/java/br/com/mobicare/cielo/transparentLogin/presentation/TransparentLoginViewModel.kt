package br.com.mobicare.cielo.transparentLogin.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.TRANSPARENT_LOGIN
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.enums.SessionExpiredEnum
import br.com.mobicare.cielo.newLogin.enums.SessionExpiredEnum.REQUIRED
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4.Companion.INCOMPLETE_INFORMATION
import br.com.mobicare.cielo.transparentLogin.domain.useCase.PostTransparentLoginUseCase
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.Error
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.ManualLogin
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.Success
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.TransparentLogin
import com.akamai.botman.CYFMonitor
import kotlinx.coroutines.launch

class TransparentLoginViewModel(
    private val transparentLoginUseCase: PostTransparentLoginUseCase,
    private val featureToggleUseCase: GetFeatureTogglePreferenceUseCase,
    private val getMeInformationUseCase: GetMeInformationUseCase,
    private val userPreferences: UserPreferences,
    private val datadogEvent: DatadogEvent
) : ViewModel() {

    private val _loginState = MutableLiveData<TransparentLoginUiState>()
    val loginState: LiveData<TransparentLoginUiState> get() = _loginState

    fun checkFeatureToggle() {
        viewModelScope.launch {
            featureToggleUseCase(key = TRANSPARENT_LOGIN, isLocal = false)
                .onSuccess { useTransparentLogin ->
                    _loginState.value = if (useTransparentLogin) TransparentLogin else ManualLogin
                }.onError {
                    _loginState.value = ManualLogin
                }.onEmpty {
                    _loginState.value = ManualLogin
                }
        }
    }

    fun login(
        request: LoginRequest,
        ignoreSessionExpired: SessionExpiredEnum = REQUIRED
    ) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: Iniciando Login Nativo TransparentLoginViewModel",
            key = "TransparentLoginViewModel",
            value = "Login request iniciado"
        )
        with(request) {
            saveUserInformation(username, merchant)
            // 1. Log já existente para início do processo de login.
            if (username.isNullOrBlank() || password.isNullOrBlank() || merchant.isNullOrBlank()) {
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento Login transparente: Campos de login inválidos",
                    key = "LoginInvalidFields",
                    value = "Campos username, password ou merchant estão inválidos."
                )
                _loginState.value = Error(INCOMPLETE_INFORMATION)
                return
            }

            viewModelScope.launch {
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento Login transparente: Iniciando chamada de rede para login",
                    key = "LoginNetworkCallStart",
                    value = "Chamada de rede para login iniciada."
                )
                transparentLoginUseCase(
                    request,
                    ignoreSessionExpired.value,
                    CYFMonitor.getSensorData()
                )
                    .onSuccess { loginResponse ->
                        datadogEvent.LoggerInfo(
                            message = "Novo Credenciamento Login transparente: Login bem-sucedido",
                            key = "LoginSuccess",
                            value = "Dados de login salvos, iniciando obtenção de informações do usuário."
                        )
                        saveLoginData(loginResponse, request)
                        getMeInformation()
                    }.onError { error ->
                        datadogEvent.LoggerInfo(
                            message = "Novo Credenciamento Login transparente: Erro no login",
                            key = "LoginError",
                            value = "Erro: ${error.apiException.newErrorMessage.message}"
                        )
                        _loginState.value = Error(error.apiException.newErrorMessage.message)
                    }.onEmpty {
                        datadogEvent.LoggerInfo(
                            message = "Novo Credenciamento Login transparente: Resposta vazia no login",
                            key = "LoginEmptyResponse",
                            value = "A chamada de login retornou uma resposta vazia."
                        )
                        _loginState.value = Error()
                    }
            }
        }
    }

    private fun saveUserInformation(username: String?, merchant: String?) {
        userPreferences.saveUserLogged(UserObj().apply {
            cpf = username
            ec = merchant
        })

        Analytics.Update.updateUserProperties()
    }

    private fun saveLoginData(loginResponse: LoginResponse, loginRequest: LoginRequest) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente:Salvando dados do usuario",
            key = "saveLoginData",
            value = "Dados - > IsconvivenciaUser: ${loginResponse.isConvivenciaUser}, KeepUserName: ${loginRequest.username?.removeNonNumbers()}, KeepEC: ${loginRequest.merchant}"
        )
        with(loginResponse) {
            userPreferences.also {
                it.saveConvivenciaStatus(isConvivenciaUser)
                it.saveToken(accessToken)
                it.saveRefreshToken(refreshToken)
                it.keepUserName(loginRequest.username?.removeNonNumbers())
                it.keepEC(loginRequest.merchant)
            }
        }
    }

    private suspend fun getMeInformation() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente:Iniciando obtenção de informações do usuário /me",
            key = "GetMeInformationStart",
            value = "Início"
        )
        getMeInformationUseCase(isLocal = false)
            .onSuccess {
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento Login transparente:Obtenção de informações do usuário bem-sucedida /me\"",
                    key = "GetMeInformationSuccess",
                    value = "Sucesso"
                )
                _loginState.value = Success
            }.onError {
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento Login transparente:Erro na obtenção de informações do usuário: ${it.apiException.newErrorMessage.message}",
                    key = "GetMeInformationError",
                    value = "Erro"
                )
                _loginState.value = Error(it.apiException.newErrorMessage.message)
            }.onEmpty {
                datadogEvent.LoggerInfo(
                    message = "Novo Credenciamento Login transparente:Resposta vazia na obtenção de informações do usuário /me\"",
                    key = "GetMeInformationEmpty",
                    value = "Resposta Vazia"
                )
                _loginState.value = Error()
            }
    }
}