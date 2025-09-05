package br.com.mobicare.cielo.newLogin

import android.text.InputType
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_NUMBER
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EMAIL
import br.com.mobicare.cielo.commons.constants.USER_INPUT_INTERNAL
import br.com.mobicare.cielo.commons.utils.ResourcesLoader

open class NewLoginState(
    val isShowFirstStepLogin: Boolean = false,
    val isShowSecondStepLogin: Boolean = false,
    val isShowHowToOpenApp: Boolean = false,
    val isShowIdentificatioField: Boolean = false,
    val isLoading: Boolean = false,
    val isEnabledIdenticationField: Boolean = false,
    val isEnabledUsernameField: Boolean = false,
    val isToBeCleanPassword: Boolean = false,
    val userInputType: Int? = null,
    val identification: String? = null,
    val username: String? = null,
    val titleIdentification: String? = null,
    val titleUsername: String? = null,
    val titleError: String? = null,
    val identificationErrorMessage: String? = null,
    val usernameMaxLength: Int? = null,
    var messageError: String? = null,
    val passwordInputType: Int? = null,
    val passwordMaxLength: Int? = null,
    val isNotBooting: Boolean = false
)

class DefaultNewLoginState : NewLoginState()

class LoadingNewLoginState(username: String, state: NewLoginState) : NewLoginState(
    isLoading = true,
    isShowFirstStepLogin = state.isShowFirstStepLogin,
    isShowSecondStepLogin = state.isShowSecondStepLogin,
    isShowHowToOpenApp = state.isShowHowToOpenApp,
    isShowIdentificatioField = state.isShowIdentificatioField,
    isEnabledIdenticationField = state.isEnabledIdenticationField,
    isEnabledUsernameField = state.isEnabledUsernameField,
    userInputType = state.userInputType,
    identification = state.identification,
    username = username,
    titleIdentification = state.titleIdentification,
    titleError = state.titleError,
    titleUsername = state.titleUsername,
    identificationErrorMessage = state.identificationErrorMessage,
    usernameMaxLength = state.usernameMaxLength,
    passwordInputType = state.passwordInputType,
    passwordMaxLength = state.passwordMaxLength
)

class SecondStepLoginState(username: String, state: NewLoginState, loading: Boolean = false) : NewLoginState(
    isLoading = loading,
    isShowFirstStepLogin = state.isShowFirstStepLogin,
    isShowSecondStepLogin = state.isShowSecondStepLogin,
    isShowHowToOpenApp = state.isShowHowToOpenApp,
    isShowIdentificatioField = state.isShowIdentificatioField,
    isEnabledUsernameField = state.isEnabledUsernameField,
    isToBeCleanPassword = false,
    userInputType = state.userInputType,
    identification = state.identification,
    username = username,
    titleIdentification = state.titleIdentification,
    titleUsername = state.titleUsername,
    titleError = state.titleError,
    identificationErrorMessage = state.identificationErrorMessage
)

class ShowIsFirstStepNewLoginState : NewLoginState(
    isShowFirstStepLogin = true,
    titleIdentification = ResourcesLoader.instance.getString(R.string.login_ec_hint),
    isEnabledIdenticationField = true,
    isShowIdentificatioField = true,
    isToBeCleanPassword = true,
    usernameMaxLength = 100
)

class ShowEstabiblishmentInSecondStepNewLoginState(
    identification: String? = null,
    username: String? = null
) : NewLoginState(
    isShowSecondStepLogin = true,
    isShowIdentificatioField = true,
    isToBeCleanPassword = true,
    titleIdentification = "Nº do estabelecimento",
    titleUsername = "Usuário",
    identification = identification,
    username = username,
    usernameMaxLength = 100,
    userInputType = USER_INPUT_EC_NUMBER,
    isEnabledUsernameField = true,
    passwordInputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
    passwordMaxLength = 100
)

class ShowInternalUserInSecondStepNewLoginState(
    identification: String? = null,
    username: String? = null
) : NewLoginState(
    isShowSecondStepLogin = true,
    isShowIdentificatioField = true,
    isToBeCleanPassword = true,
    titleIdentification = USER_INPUT_INTERNAL_NAME,
    titleUsername = "Usuário",
    identification = identification,
    username = username,
    usernameMaxLength = 100,
    passwordMaxLength = 100,
    userInputType = USER_INPUT_INTERNAL,
    isEnabledUsernameField = true,
    passwordInputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
)

class ShowCpfInSecondStepNewLoginState(username: String? = null, passwordMaxLength: Int = SIX) : NewLoginState(
    isShowSecondStepLogin = true,
    isShowIdentificatioField = false,
    isEnabledUsernameField = false,
    isToBeCleanPassword = true,
    titleUsername = "CPF",
    username = username,
    usernameMaxLength = 20,
    userInputType = USER_INPUT_CPF,
    passwordInputType = InputType.TYPE_CLASS_NUMBER,
    passwordMaxLength = passwordMaxLength
)

class ShowEmailInSecondStepNewLoginState(username: String? = null, passwordMaxLength: Int = SIX) : NewLoginState(
    isShowSecondStepLogin = true,
    isShowIdentificatioField = false,
    isEnabledUsernameField = false,
    isToBeCleanPassword = true,
    titleUsername = "Email",
    username = username,
    userInputType = USER_INPUT_EMAIL,
    passwordInputType = InputType.TYPE_CLASS_NUMBER,
    passwordMaxLength = passwordMaxLength
)

class ShowIdentificationErrorNewLoginState(
    userInput: String,
    messageError: String,
    state: NewLoginState
) : NewLoginState(
    isLoading = false,
    isToBeCleanPassword = true,
    isEnabledIdenticationField = true,
    identification = userInput,
    identificationErrorMessage = messageError,
    isShowFirstStepLogin = state.isShowFirstStepLogin,
    isShowSecondStepLogin = state.isShowSecondStepLogin,
    isShowHowToOpenApp = state.isShowHowToOpenApp,
    isShowIdentificatioField = state.isShowIdentificatioField,
    isEnabledUsernameField = state.isEnabledUsernameField,
    userInputType = state.userInputType,
    username = state.username,
    titleIdentification = state.titleIdentification,
    titleUsername = state.titleUsername,
    titleError = state.titleError
)

class ShowMessageErrorNewLoginState(
    title: String? = EMPTY,
    message: String = EMPTY,
    state: NewLoginState,
    isNotBooting: Boolean = false
) :
    NewLoginState(
        isLoading = false,
        isShowIdentificatioField = state.isShowIdentificatioField,
        titleError = title,
        messageError = message,
        isShowSecondStepLogin = true,
        isToBeCleanPassword = state.isToBeCleanPassword,
        identification = state.identification,
        username = state.username,
        userInputType = state.userInputType,
        isEnabledUsernameField = state.isEnabledUsernameField,
        titleIdentification = state.titleIdentification,
        titleUsername = state.titleUsername,
        usernameMaxLength = state.usernameMaxLength,
        passwordInputType = state.passwordInputType,
        passwordMaxLength = state.passwordMaxLength,
        isNotBooting = isNotBooting
    )

