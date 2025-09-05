package br.com.mobicare.cielo.newLogin

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.PendingInviteItem
import br.com.mobicare.cielo.commons.constants.EC_NUMBER_LENGTH
import br.com.mobicare.cielo.commons.constants.EC_NUMBER_MIM_LENGTH
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_PASSWORD_EXPIRED
import br.com.mobicare.cielo.commons.constants.HTTP_UNAUTHORIZED
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_NUMBER
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EMAIL
import br.com.mobicare.cielo.commons.constants.USER_INPUT_INTERNAL
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domain.repository.PosVirtualWhiteListRepository
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.widget.VersionBlockBottomSheetFragment
import br.com.mobicare.cielo.commons.utils.CPF_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.CPF_REGEX_PATTERN
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import br.com.mobicare.cielo.commons.utils.PHONE_WITHOUT_MASK_LENGTH
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.commons.utils.executeUpdateDialog
import br.com.mobicare.cielo.commons.utils.getFormattedUsername
import br.com.mobicare.cielo.commons.utils.isNumeric
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.extensions.documentWithoutMask
import br.com.mobicare.cielo.extensions.isNotBooting
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_WHITE_LIST
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.TRATAMENTO_FULL_SEC
import br.com.mobicare.cielo.featureToggle.data.managers.FeatureToggleRepository
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggle
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleParams
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import br.com.mobicare.cielo.login.analytics.LoginAnalytics
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.presentation.util.PriorityWarningUtil
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceRequest
import br.com.mobicare.cielo.newLogin.enums.SessionExpiredEnum
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.INTERNAL
import br.com.mobicare.cielo.transparentLogin.presentation.TransparentLoginActivity
import com.akamai.botman.CYFMonitor
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.redmadrobot.inputmask.helper.Mask
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

/**
 * Conventional way to perform "manual" user login. Here, the user is required to enter their credentials to log in.
 * An alternative flow called [TransparentLoginActivity] involves facilitating login with parameters, thus bypassing the login screen.
 * */
class NewLoginPresenter(
    private val view: NewLoginContract.View,
    private val repository: LoginRepository,
    private val tapRepository: PosVirtualWhiteListRepository,
    private val featureToggleRepository: FeatureToggleRepository,
    private val userInformationRepository: UserInformationRepository,
    private val lgpdRepository: LgpdRepository,
    private val accessManagerRepository: AccessManagerRepository,
    private val featureTogglePreference: FeatureTogglePreference,
    private val userPreferences: UserPreferences,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val mfaUserInformation: MfaUserInformation
) : NewLoginContract.Presenter {


    private var userInputType: Int = -1

    private var username: String? = null
    private var password: String? = null
    private var usernameCleanInput: String? = null

    private var fingerprintIsEnabled = false
    private var isCallTokenActivity = false

    private var disposable = CompositeDisposable()
    private val loginAnalytics = LoginAnalytics()

    private var invites: List<PendingInviteItem> = listOf()

    private var currentState by Delegates.observable<NewLoginState>(
        DefaultNewLoginState()
    ) { _, oldState, newState ->
        if (newState != oldState) {
            view.render(newState)
            usernameCleanInput = newState.username

            if (newState is ShowCpfInSecondStepNewLoginState) usernameCleanInput =
                usernameCleanInput?.removeNonNumbers()

            if (repository.hasValidTokenSeed(usernameCleanInput) && repository.hasEnabledMfa()) view.showTokenButton()
        }
    }

    override fun onCreated() {
        blockVersionIfNeeded(object : VersionBlockBottomSheetFragment.OnVersionTerminateListener {
            override fun onContinueTask() {
                if (userPreferences.keepLogin) {
                    loadKeepData()
                    if (userPreferences.fingerprintRecorded) view.setClickCallBiometricPrompt()
                } else {
                    currentState = ShowIsFirstStepNewLoginState()
                }
                callFeatureToggle()
            }
        })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun onFirstStepNextButtonClicked(identification: String) {
        loginAnalytics.logFirstStepNextButtonClicked(userInputType)

        val identificationTrimmed = identification.trim()
        val identificationClean = identificationTrimmed.documentWithoutMask()

        if (identificationClean.isNumeric()) {
            if (verifyCpfOrEC(identificationTrimmed).not())
                return
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
        setKeepData(userPreferences.keepLogin)
        loginAnalytics.logSuccessFirstStepNextButtonClicked(userInputType, identification)
    }

    override fun onBackButtonClicked() {
        val username = userPreferences.userName
        val keepData = userPreferences.keepLogin

        if (keepData && username.isNotEmpty()) view.showUserLostDataWarning()
        else goToFirstStepNewLogin()
    }

    private fun goToFirstStepNewLogin() {
        currentState = ShowIsFirstStepNewLoginState()
    }

    override fun onForgotPasswordButtonClicked() {
        loginAnalytics.logForgotButtonClicked(userInputType)
        loginAnalytics.logLoginForgotButtonClickedGa()
        view.openForgotPasswordView()
    }

    private fun loadKeepData() {
        userInputType = userPreferences.userInputTypeUserLogged
        username = userPreferences.userName
        val isKeepData = userPreferences.keepLogin
        var identification = userPreferences.numeroEC

        when (userInputType) {
            USER_INPUT_EC_NUMBER -> {
                currentState = ShowEstabiblishmentInSecondStepNewLoginState(
                    identification = identification, username = username
                )
            }

            USER_INPUT_EMAIL -> {
                currentState = ShowEmailInSecondStepNewLoginState(username = username)
            }

            USER_INPUT_CPF -> {
                val cpfMask = Mask(CPF_MASK_FORMAT)
                if (username.isNullOrEmpty().not()) {
                    identification = username as String
                }
                identification.run {
                    currentState = ShowCpfInSecondStepNewLoginState(
                        username = cpfMask.apply(CustomCaretString.forward(this)).formattedText.string
                    )
                }
            }

            USER_INPUT_INTERNAL -> {
                currentState = ShowInternalUserInSecondStepNewLoginState(
                    identification = userPreferences.userInformation?.merchant?.id
                        ?: USER_INPUT_INTERNAL_NAME, username = username
                )
            }
        }

        setKeepData(isKeepData)
    }

    private fun setKeepData(isKeepData: Boolean) {
        view.setKeepData(isKeepData)
    }

    private fun verifyInternalUser(userInput: String): Boolean {
        userInputType = USER_INPUT_INTERNAL

        if (userInput.lowercase() != USER_INPUT_INTERNAL_NAME) {
            currentState = ShowIdentificationErrorNewLoginState(
                userInput = userInput, messageError = ERROR_INTERNAL_NAME, state = currentState
            )
            loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
            loginAnalytics.logLoginDisplayContent(ERROR_INTERNAL_NAME_GA)

            return false
        }
        return true
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
                loginAnalytics.logLoginDisplayContent(INVALID_CPF_GA)
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
                loginAnalytics.logLoginDisplayContent(INVALID_EC_GA)
                return false
            }
            userInputType = USER_INPUT_EC_NUMBER
        }

        return true
    }

    private fun verifyEmail(userInput: String): Boolean {
        if (ValidationUtils.isEmail(userInput).not()) {
            currentState = ShowIdentificationErrorNewLoginState(
                userInput = userInput, messageError = INVALID_EMAIL, state = currentState
            )
            loginAnalytics.logErrorFirstStepNextButtonClicked(userInputType, userInput)
            loginAnalytics.logLoginDisplayContent(INVALID_EMAIL_GA)
            return false
        }
        userInputType = USER_INPUT_EMAIL
        return true
    }

    override fun onLoginButtonClicked(
        identification: String,
        username: String,
        password: String,
        isKeepData: Boolean,
        fingerprint: String?
    ) {

        loginAnalytics.logLoginButtonClicked(userInputType, username)
        loginAnalytics.logLoginButtonClickedGa(userInputType)

        currentState = LoadingNewLoginState(username, currentState)

        repository.login(
            username = getFormattedUsername(username, userInputType),
            password = password,
            merchant = identification,
            callback = object : APICallbackDefault<LoginResponse, String> {

                override fun onError(error: ErrorMessage) {
                    loginAnalytics.logLoginProcessError(userInputType, error.code, username)

                    currentState = if (error.httpStatus == HTTP_UNAUTHORIZED
                        && error.errorCode == ERROR_CODE_PASSWORD_EXPIRED
                    ) {
                        view.showPasswordExpired()
                        SecondStepLoginState(username, currentState)
                    } else {
                        ShowMessageErrorNewLoginState(
                            title = TITLE_LOGIN,
                            message = error.message,
                            state = currentState,
                            isNotBooting = error.errorCode.isNotBooting()
                        )
                    }
                }

                override fun onSuccess(response: LoginResponse) {
                    userPreferences.saveToken(response.accessToken)
                    userPreferences.saveRefreshToken(response.refreshToken)
                    if (response.additionalData != null && response.additionalData.newDeviceDetected) {
                        view.showNewDeviceDetected(response.additionalData.foreign)
                    } else {
                        if (featureTogglePreference.getFeatureTogle(POS_VIRTUAL_WHITE_LIST))
                            checkWhiteListPosVirtual(response, identification, username, password, isKeepData)
                        else
                            loginSuccess(response, identification, username, password, isKeepData, true)
                    }
                }
            },
            sessionExpired = SessionExpiredEnum.REQUIRED,
            fingerprint = fingerprint,
            akamaiSensorData = CYFMonitor.getSensorData()
        )
    }

    private fun loginSuccess(
        response: LoginResponse,
        identification: String,
        username: String,
        password: String,
        isKeepData: Boolean,
        isPosWhiteList: Boolean
    ) {
        loginAnalytics.logLoginProcessSucess(userInputType, username)
        userPreferences.savePosVirtualWhiteList(isPosWhiteList)
        userPreferences.keepUserPass(password)

        if (isKeepData)
            loginAnalytics.logKeepData(userInputType)

        checkFullSec(response, identification, isKeepData)
        currentState = SecondStepLoginState(username, currentState, loading = true)
    }

    private fun checkWhiteListPosVirtual(
        loginResponse: LoginResponse,
        identification: String,
        username: String,
        password: String,
        isKeepData: Boolean
    ) {
        disposable.add(
            tapRepository.getPosVirtualWhiteList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ posWhiteList ->
                    loginSuccess(
                        loginResponse,
                        identification,
                        username,
                        password,
                        isKeepData,
                        posWhiteList.eligible
                    )
                }, {
                    loginSuccess(
                        loginResponse,
                        identification,
                        username,
                        password,
                        isKeepData,
                        false
                    )
                })
        )
    }

    private fun checkFullSec(
        loginResponse: LoginResponse,
        identification: String,
        isKeepData: Boolean
    ) {
        val isHandlingFullSec =
            featureTogglePreference.getFeatureTogle(TRATAMENTO_FULL_SEC)

        if (loginResponse.isConvivenciaUser) {
            proceedLogin(loginResponse, identification, isKeepData)
            if (isHandlingFullSec.not())
                view.showUpdateRegister()
        } else {
            if (isHandlingFullSec)
                view.showUpdateRegister()
        }
    }

    private fun callFeatureToggle() {
        featureToggleRepository.getFeatureToggle(disposable,
            FeatureToggleParams.getParams(),
            object : APICallbackDefault<List<FeatureToggle>, ErrorMessage> {

                override fun onSuccess(response: List<FeatureToggle>) {
                    try {
                        checkDynamicModal()
                    } catch (undeliverableException: UndeliverableException) {
                        undeliverableException.message.logFirebaseCrashlytics()
                    } catch (castException: ClassCastException) {
                        castException.message.logFirebaseCrashlytics()
                    } catch (ex: Exception) {
                        ex.message.logFirebaseCrashlytics()
                    }
                }
            })
    }

    private fun checkDynamicModal() {
        val featureToggleModal = FeatureTogglePreference.instance.getFeatureToggleModal()
        val sawWarning = FeatureTogglePreference.instance.getSawWarning()

        val isShow =
            FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.MODAL_DINAMICA)

        featureToggleModal?.let {
            val hasId = PriorityWarningUtil.hasId(sawWarning, it.id)
            if (it.loggedModal.not() && isShow) {
                if (it.stickyModal) view.onSuccessFeatureToggle(it)
                else if (hasId.not()) view.onSuccessFeatureToggle(it)

            }
        }
    }

    private fun proceedLogin(
        response: LoginResponse, identification: String, isKeepData: Boolean
    ) {
        userPreferences.saveConvivenciaStatus(response.isConvivenciaUser)
        userPreferences.saveToken(response.accessToken)
        userPreferences.saveRefreshToken(response.refreshToken)
        userPreferences.saveKeep(isKeepData)
        userPreferences.keepEC(identification)

        username?.run {
            if (userInputType == USER_INPUT_CPF && CPF_REGEX_PATTERN.findAll(this)
                    .count() > ZERO
            ) userPreferences.keepUserName(removeNonNumbers())
            else userPreferences.keepUserName(this)
        }
        userPreferences.saveUserInputTypeUserLogged(userInputType)
        checkUserInvites()
    }

    private fun checkUserInvites() {
        accessManagerRepository.getPendingInvites().configureIoAndMainThread()
            .subscribe({ response ->
                if (response.summary.totalQuantity > ZERO) invites =
                    response.items.filter { it.expired.not() }

                getUserInformation(userPreferences.token)
            }, {
                getUserInformation(userPreferences.token)
            }).addTo(disposable)
    }

    fun getUserInformation(token: String) {
        username?.let {
            currentState = LoadingNewLoginState(it, currentState)
        } ?: view.render(NewLoginState(isLoading = true))

        userInformationRepository.getUserInformation(accessToken = token)
            .configureIoAndMainThread().subscribe({ userInformationResponse ->
                view.saveRoles(userInformationResponse.roles)

                currentState = username?.let {
                    SecondStepLoginState(it, currentState, loading = true)
                } ?: DefaultNewLoginState()

                validateInvites(userInformationResponse)

            }, {
                Firebase.crashlytics.recordException(it)
                val errorMessage = ErrorMessage.fromThrowable(it)
                currentState = ShowMessageErrorNewLoginState(
                    title = TITLE_LOGIN,
                    message = errorMessage.message,
                    state = currentState
                )
            }).addTo(disposable)
    }

    private fun validateInvites(userInformation: MeResponse) {
        val onboardingRequired = userInformation.onboardingRequired == true
        if (invites.isNotEmpty()) view.showInvite(
            invite = invites.first(), onboardingRequired = onboardingRequired
        )
        else if (onboardingRequired) {
            setupUserInformation(userInformation)
            view.showIdOnboarding()
        } else checkLgpd(password?.toByteArray())
    }

    private fun setupUserInformation(userInformation: MeResponse) {
        userStatus.run {
            cpf = userInformation.identity?.cpf
            isForeigner = userInformation.identity?.foreigner
            name = userInformation.username
            email = userInformation.email

            cellphone =
                if (userInformation.phoneNumber.onlyDigits().length == PHONE_WITHOUT_MASK_LENGTH) userInformation.phoneNumber
                else null
        }
    }

    override fun onUsernameChanged(value: String?) {
        username = value
        view.changeLoginButtonState(isLoginButtonEnabled())
    }

    override fun onPasswordChanged(value: String?) {
        password = value
        view.changeLoginButtonState(isLoginButtonEnabled())
    }

    override fun onGoToFirstScreenCleanData() {
        userPreferences.erase(UserPreferences.userPreferencesKeys)
        userPreferences.saveFirstUse(false)
        userPreferences.saveFingerprintData(byteArrayOf())
        currentState = ShowIsFirstStepNewLoginState()
        mfaUserInformation.cleanMfaRegisters()
    }

    private fun isLoginButtonEnabled() =
        username.isNullOrEmpty().not() && password.isNullOrEmpty()
            .not() || fingerprintIsEnabled

    override fun fingerprintIsEnabled(isEnable: Boolean) {
        fingerprintIsEnabled = isEnable
    }

    override fun checkCreateButton() {
        val visibility = featureTogglePreference.getFeatureTogle(FeatureTogglePreference.CREATE_ACCOUNT_LOGIN)
        view.setupCreateButtonVisibility(visibility)
    }

    override fun onLoginByFingerprint(password: String, allowMeToken: String?) {
        if (isCallTokenActivity) {
            view.showTokenActivity(usernameCleanInput ?: EMPTY)
        } else {
            val isKeepData = userPreferences.keepLogin
            val identification = when (userInputType) {
                USER_INPUT_EC_NUMBER -> userPreferences.numeroEC
                USER_INPUT_INTERNAL -> INTERNAL
                else -> EMPTY
            }

            username?.let { itUsername ->
                onLoginButtonClicked(
                    identification, itUsername, password, isKeepData, allowMeToken
                )
            }
        }
        isCallTokenActivity = false
    }

    override fun callFingerprintToTokenActivity() {
        isCallTokenActivity = true
        view.showUserBiometricPrompt()
    }

    private fun blockVersionIfNeeded(
        versionTerminateListener: VersionBlockBottomSheetFragment.OnVersionTerminateListener
    ) {
        Unit.executeUpdateDialog { needsUpdate, forceUpdate ->
            if (needsUpdate) view.showVersionUpdaterDialog(
                forceUpdate,
                versionTerminateListener
            )
            else versionTerminateListener.onContinueTask()
        }
    }

    private fun checkLgpd(password: ByteArray?) {
        if (userInputType == USER_INPUT_INTERNAL) view.showInternalUserScreen()
        else if (featureTogglePreference.isActivate(FeatureTogglePreference.LGPD)) lgpdRepository.getEligibility()
            .configureIoAndMainThread().subscribe({
                if (it.eligible == true) view.showLGPD(it, password)
                else view.callMainLoggedScreen(password)
            }, {
                view.callMainLoggedScreen()
            }).addTo(disposable)
        else view.callMainLoggedScreen()
    }

    override fun resetNewDevice(){
        currentState = username?.let {
            SecondStepLoginState(it, currentState)
        } ?: DefaultNewLoginState()
    }

    override fun registerDevice(faceIdToken: String?, fingerprint: String?) {
        disposable.add(
            repository.postRegisterDevice(
                faceIdToken ?: EMPTY,
                PostRegisterDeviceRequest(fingerprint)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    refreshToken()
                }, { error ->
                    view.showSdkError(TWO)
                })
        )
    }

    private fun refreshToken() {
        disposable.add(
            repository.refreshToken(
                userPreferences.token, userPreferences.refreshToken
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    userPreferences.saveToken(it.accessToken)
                    view.successRegisterDevice(it)
                }, { error ->
                    view.showSdkError(THREE)
                })
        )
    }

    override fun continueLogin(
        loginResponse: LoginResponse,
        identification: String,
        username: String,
        password: String,
        isKeepData: Boolean
    ) {
        if (featureTogglePreference.getFeatureTogle(POS_VIRTUAL_WHITE_LIST))
            checkWhiteListPosVirtual(loginResponse, identification, username, password, isKeepData)
        else
            loginSuccess(loginResponse, identification, username, password, isKeepData, true)
    }
}