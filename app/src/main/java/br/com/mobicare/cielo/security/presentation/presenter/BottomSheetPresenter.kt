package br.com.mobicare.cielo.security.presentation.presenter

import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.documentWithoutMask
import br.com.mobicare.cielo.extensions.isNotBooting
import br.com.mobicare.cielo.login.analytics.LoginAnalytics
import br.com.mobicare.cielo.newLogin.*
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.enums.SessionExpiredEnum
import br.com.mobicare.cielo.security.presentation.ui.BottomSheetSecurityContract
import com.akamai.botman.CYFMonitor
import kotlin.properties.Delegates

class BottomSheetPresenter(
    private val view: BottomSheetSecurityContract.View,
    private val repository: LoginRepository,
) : BottomSheetSecurityContract.Presenter {

    private var userInputType: Int = -1
    private var username: String? = null
    private var usernameCleanInput: String? = null
    private val loginAnalytics = LoginAnalytics()
    private var password: String? = null
    private val userPreferences: UserPreferences = UserPreferences.getInstance()

    private var currentState by Delegates.observable<NewLoginState>(
        DefaultNewLoginState()
    ) { _, oldState, newState ->
        if (newState != oldState) {
            view.render(newState)
            usernameCleanInput = newState.username

            if (newState is ShowCpfInSecondStepNewLoginState) {
                usernameCleanInput = usernameCleanInput?.removeNonNumbers()
            }
        }
    }

    override fun onLoginButtonClicked(
        identification: String,
        username: String,
        password: String?,
        isKeepData: Boolean,
        fingerprint: String?
    ) {
        currentState = LoadingNewLoginState(username, currentState)
        password?.let { itPassword ->
            repository.login(
                username = getFormattedUsername(username, userPreferences.userInputTypeUserLogged),
                password = itPassword,
                callback = object : APICallbackDefault<LoginResponse, String> {
                    override fun onError(error: ErrorMessage) {
                        loginAnalytics.logLoginProcessError(userInputType, error.code, username)
                        errorLogin(error)
                    }

                    override fun onSuccess(response: LoginResponse) {
                        loginAnalytics.logLoginProcessSucess(userInputType, username)

                        if (isKeepData)
                            loginAnalytics.logKeepData(userInputType)

                        userPreferences.keepUserPass(password)
                        proceedLogin(identification, isKeepData)
                    }
                },
                sessionExpired = SessionExpiredEnum.NO_REQUIRE,
                fingerprint = fingerprint,
                akamaiSensorData = CYFMonitor.getSensorData()
            )
        } ?: errorLogin()
    }

    private fun errorLogin(error: ErrorMessage = ErrorMessage()) {
        currentState = ShowMessageErrorNewLoginState(
            title = TITLE_LOGIN,
            message = error.message,
            state = currentState,
            isNotBooting = error.errorCode.isNotBooting()
        )
    }

    override fun onPasswordChanged(value: String?) {
        password = value
        view.changeLoginButtonState(isLoginButtonEnabled())
    }

    private fun isLoginButtonEnabled() = password.isNullOrEmpty().not()

    private fun proceedLogin(identification: String, isKeepData: Boolean) {
        userPreferences.saveKeep(isKeepData)
        userPreferences.keepEC(identification)

        username?.run {
            if (userInputType == USER_INPUT_CPF && CPF_REGEX_PATTERN
                    .findAll(this)
                    .count() > ZERO
            )
                userPreferences.keepUserName(userPreferences.userName.removeNonNumbers())
            else
                userPreferences.keepUserName(userPreferences.userName)
        }
        userPreferences.saveUserInputTypeUserLogged(userInputType)
        view.successAuth()
    }

    override fun validatePassword(identification: String) {
        val identificationTrimmed = identification.trim()
        val identificationClean = identificationTrimmed.documentWithoutMask()

        if (identificationClean.isNumeric()) {
            if (verifyCpfOrEC(identificationTrimmed).not()) return
        } else {
            if (identificationTrimmed.length > USER_INPUT_INTERNAL_NAME.length) {
                if (verifyEmail(identificationTrimmed).not()) return
            } else if (verifyInternalUser(identificationTrimmed).not()) return
        }
        currentState = when (userInputType) {
            USER_INPUT_EC_NUMBER -> ShowEstabiblishmentInSecondStepNewLoginState(identification = identification)
            USER_INPUT_CPF -> ShowCpfInSecondStepNewLoginState(username = identification)
            USER_INPUT_INTERNAL -> ShowInternalUserInSecondStepNewLoginState(identification = identification)
            else -> ShowEmailInSecondStepNewLoginState(username = identification)
        }
    }

    private fun verifyCpfOrEC(userInput: String): Boolean {
        if (userInput.length > EC_NUMBER_LENGTH) {
            if (ValidationUtils.isCPF(userInput).not()) {
                currentState = ShowIdentificationErrorNewLoginState(
                    userInput = userInput,
                    messageError = INVALID_CPF,
                    state = currentState
                )
                loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
                return false
            }

            userInputType = USER_INPUT_CPF
        } else {

            if (userInput.length < EC_NUMBER_MIM_LENGTH) {
                currentState = ShowIdentificationErrorNewLoginState(
                    userInput = userInput,
                    messageError = INVALID_EC,
                    state = currentState
                )

                loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
                return false
            }
            userInputType = USER_INPUT_EC_NUMBER
        }

        return true
    }

    private fun verifyEmail(userInput: String): Boolean {
        if (ValidationUtils.isEmail(userInput).not()) {
            currentState = ShowIdentificationErrorNewLoginState(
                userInput = userInput,
                messageError = INVALID_EMAIL,
                state = currentState
            )
            loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
            return false
        }
        userInputType = USER_INPUT_EMAIL
        return true
    }

    private fun verifyInternalUser(userInput: String): Boolean {
        userInputType = USER_INPUT_INTERNAL

        if (userInput.lowercase() != USER_INPUT_INTERNAL_NAME) {
            currentState = ShowIdentificationErrorNewLoginState(
                userInput = userInput,
                messageError = ERROR_INTERNAL_NAME,
                state = currentState
            )
            loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
            return false
        }
        return true
    }
}