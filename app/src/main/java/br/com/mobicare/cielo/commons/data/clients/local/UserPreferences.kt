package br.com.mobicare.cielo.commons.data.clients.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_NUMBER
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EMAIL
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.login.domain.MultichannelLoginType
import br.com.mobicare.cielo.login.domain.MultichannelLoginType.CPF
import br.com.mobicare.cielo.login.domain.MultichannelLoginType.EC_NUMBER
import br.com.mobicare.cielo.login.domain.MultichannelLoginType.EMAIL
import br.com.mobicare.cielo.login.domain.MultichannelLoginType.values
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.newLogin.erase
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk
import timber.log.Timber
import java.io.File
import java.security.KeyStore
import java.util.Arrays

private const val ENCRYPTED_PREFS_FOLDER = "default_prefs"
private const val ANDROID_KEY_STORE = "AndroidKeyStore"
private const val PREFS_FILE_NAME = "shared_prefs"
private const val ENCRYPTED_PREFS_FILE = "default_prefs.xml"

class UserPreferences() {
    interface ONBOARDING {
        companion object {
            const val SUPERLINK = "ONBOARFING_SUPERLINK"
            const val MFA = "ONBOARDING_MFA"
        }
    }

    private var encryptedSharedPreferences: SharedPreferences? = null

    private fun checkMigration() {
        val currentVersion: Int = Hawk.get(KEY_VERSION, ONE_NEGATIVE)
        if (currentVersion < VERSION) {
            doMigration(currentVersion)
            put(KEY_VERSION, VERSION)
        }
    }

    private fun doMigration(version: Int) {
        if (version < ONE) {
            migrateToFirstVersion()
        }
    }

    private fun migrateToFirstVersion() {
        val isKeepData: Boolean = keepLogin
        if (isKeepData) {
            var userInputType: Int = userInputTypeUserLogged
            if (userInputType == USER_INPUT_EC_NUMBER) {
                if (userName.isEmpty()) {
                    if (multichannelLoginType === EMAIL) {
                        userInputType = USER_INPUT_EMAIL
                    } else if (multichannelLoginType === CPF) {
                        userInputType = USER_INPUT_CPF
                    }
                    saveUserInputTypeUserLogged(userInputType)
                }
            }
        }
        saveOnboardingAppNewsPushNotification(
            get(
                KEEP_WELCOME,
                false,
            ) && fingerprintCloseCount() == ZERO,
        )
    }

    fun saveTokenImpersonate(tokenFcm: String?) {
        put(key = TOKEN_IMPERSONATE, value = tokenFcm, isProtected = true)
    }

    val tokenImpersonate: String?
        get() = get(key = TOKEN_IMPERSONATE, defaultValue = null, isProtected = true)

    fun saveTokenFCM(tokenFcm: String?) {
        put(key = TOKEN_FCM, value = tokenFcm, isProtected = true)
    }

    val tokenFCM: String?
        get() = get(key = TOKEN_FCM, defaultValue = null, isProtected = true)

    fun saveTokenFcmSent(sentFCM: Boolean) {
        put(TOKEN_FCM_SENT, sentFCM)
    }

    val tokenFcmSent: Boolean
        get() = get(TOKEN_FCM_SENT, false)

    fun saveFingerprintCounter(currentCount: Int) {
        put(FINGERPRINT_NOTIFICATION_COUNTER_KEY, currentCount)
    }

    fun fingerprintCloseCount(): Int {
        return get(FINGERPRINT_NOTIFICATION_COUNTER_KEY, ZERO)
    }

    fun saveFingerprintData(encryptedMessage: ByteArray?) {
        put(
            key = FINGERPRINT_DATA,
            value =
                if (encryptedMessage == null) {
                    null
                } else {
                    String(
                        encryptedMessage,
                        Charsets.ISO_8859_1,
                    )
                },
            isProtected = true,
        )
    }

    val fingerprintData: ByteArray?
        get() = getFingerPrintData()

    val isShowOnboardingAppNewsPushNotification: Boolean
        get() = get(ONBOARDING_APP_NEWS_PUSH_NOTIFICATION_KEY, defaultValue = true)

    fun saveOnboardingAppNewsPushNotification(value: Boolean) {
        put(ONBOARDING_APP_NEWS_PUSH_NOTIFICATION_KEY, value = value)
    }

    val isShowBiometricNotification: Boolean
        get() = get(SHOW_BIOMETRIC_NOTIFICATION, defaultValue = true)

    fun saveShowBiometricNotification(value: Boolean){
        put(SHOW_BIOMETRIC_NOTIFICATION, value = value)
    }

    val isCalledBiometricNotificationByLogin: Boolean
        get() = get(CALLED_BIOMETRIC_NOTIFICATION_BY_LOGIN, defaultValue = false)

    fun saveCalledBiometricNotificationByLogin(value: Boolean){
        put(CALLED_BIOMETRIC_NOTIFICATION_BY_LOGIN, value = value)
    }

    val barcode: String?
        get() = get(KEEP_BARCODE, null)

    /**
     * Salvar os dados de informação do usuário logado
     *
     * @param userInformation
     */
    fun saveUserInformation(userInformation: MeResponse?) {
        val gson = GsonBuilder().create()
        put(
            key = USER_INFORMATION,
            value = if (userInformation != null) gson.toJson(userInformation) else null,
            isProtected = true,
        )
    }

    /**
     * Retorna as informações do usuário logado
     *
     * @return
     */
    val userInformation: MeResponse?
        get() {
            val gson = GsonBuilder().create()
            val jsonRestored = get(key = USER_INFORMATION, defaultValue = null, isProtected = true)

            return if (jsonRestored != null) {
                gson.fromJson(
                    jsonRestored,
                    MeResponse::class.java,
                )
            } else {
                null
            }
        }

    /**
     * Salvar refresh token do acesso as apis privadas
     *
     * @param token
     */
    fun saveRefreshToken(token: String?) {
        put(key = REFRESH_TOKEN, value = token, isProtected = true)
    }

    /**
     * Retorna valor do refresh token caso o usuário esteja autenticado
     *
     * @return
     */
    val refreshToken: String?
        get() = get(key = REFRESH_TOKEN, defaultValue = EMPTY, isProtected = true)

    /**
     * Salvar token de acesso as apis privadas
     *
     * @param token
     */
    fun saveToken(token: String?) {
        put(key = USER_TOKEN, value = token, isProtected = true)
    }

    fun saveNewlyAccredited(newlyAccredited: Boolean) {
        put(NEWLY_ACCREDITED, newlyAccredited)
    }

    val newlyAccredited: Boolean
        get() = get(NEWLY_ACCREDITED, false)

    /**
     * Retorna valor do token caso o usuário esteja autenticado
     *
     * @return
     */
    val token: String
        get() {
            return get(key = USER_TOKEN, defaultValue = EMPTY, isProtected = true) ?: EMPTY
        }

    /**
     * Grava estado da migração para o convivência
     *
     * @param isMigrated
     */
    fun saveConvivenciaStatus(isMigrated: Boolean) {
        put(CONVIVENCIA_STATUS, isMigrated)
    }

    /**
     * Grava estado da tela do tutorial do cancelamento
     *
     * @param isCancel
     */
    fun saveCancelStatus(isCancel: Boolean) {
        put(KEEP_CANCEL, isCancel)
    }

    /**
     * Retorna estado da tela de tuto cancel
     *
     * @return
     */
    val isCancelStatus: Boolean
        get() = get(KEEP_CANCEL, false)

    fun saveBannerStatusBalcaoRebevies(isTrue: Boolean) {
        put(KEEP_BANNER_BALCAO_RECEBIVEIS, isTrue)
    }

    val isBannerStatusBalcaoRebevies: Boolean
        get() = get(KEEP_BANNER_BALCAO_RECEBIVEIS, false)

    fun saveUserPJ(isWelcome: Boolean?) {
        put(USER_PJ, isWelcome ?: false)
    }

    val isUserPJ: Boolean
        get() = get(USER_PJ, false)

    fun saveLegalEntity(isLegalEntity: Boolean = false) {
        put(LEGAL_ENTITY, isLegalEntity)
    }

    fun deleteLegalEntity() {
        delete(LEGAL_ENTITY)
    }

    val isLegalEntity: Boolean
        get() = get(LEGAL_ENTITY, false)

    fun setTurboRegistrationErrorStep(step: Int) {
        put(TURBO_REGISTRATION_ERROR_STEP + ecUserLogged, step)
    }

    fun deleteTurboRegistrationErrorStep() {
        delete(TURBO_REGISTRATION_ERROR_STEP + ecUserLogged)
    }

    val turboRegistrationErrorStep: Int
        get() = get(TURBO_REGISTRATION_ERROR_STEP + ecUserLogged, RegistrationStepError.UNDEFINED.ordinal)

    fun isShowOnboardingAppNews(isCanAuthenticateWithBiometrics: Boolean): Boolean {
        return isShowOnboardingAppNewsPushNotification || isCanAuthenticateWithBiometrics && keepLogin && (!fingerprintRecorded && fingerprintCloseCount() < THREE)
    }

    fun isShowBiometricNotification(isCanAuthenticateWithBiometrics: Boolean): Boolean {
        return isCanAuthenticateWithBiometrics && isShowBiometricNotification && !fingerprintRecorded
    }

    /**
     * Grava estado da tela de Modal Receba Mais
     *
     * @param isRecebaMais
     */
    fun saveRecebaMaisChecked(isRecebaMais: Boolean) {
        put(KEEP_RECEBA_MAIS, isRecebaMais)
    }

    /**
     * Retorna estado do receba mais
     *
     * @return
     */
    val isRecebaMaisChecked: Boolean
        get() = get(KEEP_RECEBA_MAIS, false)

    /**
     * Grava estado da tela de Modal Receba Mais
     *
     * @param isBannerMigration
     */
    fun saveBannerMigration(isBannerMigration: Boolean) {
        put(KEEP_BANNER_MIGRATION, isBannerMigration)
    }

    fun saveCancelTutorialExibitionCount(count: Int) {
        put(CANCEL_TUTORIAL_EXIBITION_COUNT, count)
    }

    val cancelTutorialExibitionCount: Int
        get() = get(CANCEL_TUTORIAL_EXIBITION_COUNT, ZERO)

    /**
     * Retorna estado do receba mais
     *
     * @return
     */
    val isBannerMigration: Boolean
        get() = get(KEEP_BANNER_MIGRATION, false)

    fun saveUserLogged(userLogged: Boolean) {
        put(USER_LOGGED, userLogged)
    }

    /**
     * Retorna estado da migração para o convivência
     *
     * @return
     */
    val isConvivenciaUser: Boolean
        get() = get(CONVIVENCIA_STATUS, false)

    /**
     * Setar preferencia de guardar ec na tela de login
     *
     * @return
     */
    fun keepLogin(
        keep: Boolean,
        ec: String?,
        userName: String?,
    ) {
        put(KEEP_LOGIN, keep)
        keepEC(ec)
        keepUserName(userName)
    }

    fun isStepTwo(isStepTwo: Boolean) {
        put(KEEP_STEP_TWO, isStepTwo)
    }

    val isStepTwo: Boolean
        get() = get(KEEP_STEP_TWO, false)

    fun keepEC(ec: String?) {
        put(key = KEEP_NUMERO_EC, value = ec, isProtected = true)
    }

    fun keepUserName(userName: String?) {
        put(key = KEEP_USER_NAME, value = userName, isProtected = true)
    }

    fun keepUserPass(userPass: String) {
        put(key = KEEP_USER_PASSWORD, value = userPass, isProtected = true)
    }

    fun savePosVirtualWhiteList(eligible: Boolean = false) {
        put(key = POS_VIRTUAL_WHITE_LIST, value = eligible, isProtected = true)
    }

    val isPosVirtualWhiteList: Boolean
        get() = get(key = POS_VIRTUAL_WHITE_LIST, defaultValue = false, isProtected = true)

    val keepLogin: Boolean
        get() = get(KEEP_LOGIN, false)

    val numeroEC: String
        get() = get(key = KEEP_NUMERO_EC, defaultValue = EMPTY, isProtected = true) ?: EMPTY

    val userName: String
        get() = get(key = KEEP_USER_NAME, defaultValue = EMPTY, isProtected = true) ?: EMPTY

    val keepUserPassword: String
        get() = get(key = KEEP_USER_PASSWORD, defaultValue = EMPTY, isProtected = true) ?: EMPTY

    fun saveKeep(keep: Boolean) {
        put(KEEP_LOGIN, keep)
    }

    fun saveKeepStatusMfa(keep: Boolean) {
        put(KEEP_STATUS_MFA, keep)
    }

    val keepStatusMfa: Boolean
        get() = get(KEEP_STATUS_MFA, false)

    fun saveMultichannelLogin(multichannelLoginType: MultichannelLoginType?) {
        put(CURRENT_USER_IS_MULTICHANNEL_LOGIN_BY_MAIL, multichannelLoginType?.ordinal ?: ZERO)
    }

    val multichannelLoginType: MultichannelLoginType
        get() =
            values().get(
                get(
                    CURRENT_USER_IS_MULTICHANNEL_LOGIN_BY_MAIL,
                    EC_NUMBER.ordinal,
                ),
            )

    /**
     * Verifica se o usuário está autenticado
     *
     * @return
     */
    val isAuthenticated: Boolean
        get() {
            val token: String = token
            return token.isNotEmpty()
        }

    fun logout() {
        MenuPreference.instance.logout()
        saveToken(EMPTY)
        removeCurrentUserLogged()
        getInstance().saveResearchData(null)
        getInstance().saveStatusFarolNew(false)
        getInstance().saveStatusMyCardsNew(false)
        getInstance().saveBannerMigration(false)
        removeUserActionPermissions()
        removeSegmentCode()
    }

    fun saveFirstUse(firstUse: Boolean) {
        put(FIRST_USE, firstUse)
    }

    val firstUse: Boolean
        get() {
            return get(FIRST_USE, true)
        }

    fun clearUserData(cleanRecebaMais: Boolean) {
        keepLogin(keepLogin, EMPTY, EMPTY)
        saveUserLogged(false)
        cleanFingerprintData()
        removeSegmentCode()
        val configuration: List<Configuration>? =
            ConfigurationPreference.instance.configurationValues
        instance?.erase(userPreferencesKeys)
        saveRecebaMaisChecked(cleanRecebaMais)

        if (configuration != null) {
            ConfigurationPreference.instance.saveConfig(configuration)
        }
    }

    val isUserLogged: Boolean
        get() {
            return get(USER_LOGGED, false)
        }

    val statusFarol: Boolean
        get() {
            return get(STATUS_FAROL, false)
        }

    private fun saveStatusMyCardsNew(status: Boolean) {
        put(STATUS_MY_CARDS, status)
    }

    private fun saveStatusFarolNew(signed: Boolean) {
        put(STATUS_FAROL, signed)
    }

    fun noteResearch(): Int {
        return get(NOTE_RESEARCH, ZERO)
    }

    fun saveNoteResearch(note: Int) {
        put(NOTE_RESEARCH, note)
    }

    fun descriptionResearch(): String {
        return get(DESCRIPTION_RESEARCH, EMPTY) ?: EMPTY
    }

    fun saveDescriptionResearch(description: String) {
        put(DESCRIPTION_RESEARCH, description)
    }

    fun saveSentResearch(sentResearch: Boolean) {
        put(SENT_RESEARCH, sentResearch)
    }

    fun saveFingerprintRecorded(fingerprintStatus: Boolean) {
        put(FINGERPRINT_SAVED, fingerprintStatus)
    }

    val fingerprintRecorded: Boolean
        get() = get(FINGERPRINT_SAVED, false)

    fun sentResearch(): Boolean {
        return get(SENT_RESEARCH, true)
    }

    class UserLoggedData(
        var token: String? = EMPTY,
        var ecNumber: String? = EMPTY,
        var username: String? = EMPTY,
        var email: String? = EMPTY,
        var cpf: String? = EMPTY,
        var multichannelLoginType: MultichannelLoginType,
    )

    fun saveUserInputTypeUserLogged(userInputType: Int) {
        put(CURRENT_USER_INPUT_TYPE, userInputType)
    }

    val userInputTypeUserLogged: Int
        get() = get(CURRENT_USER_INPUT_TYPE, ZERO)

    private fun saveEcUserLogged(ecNumber: String?) {
        put(key = CURRENT_USER_LOGGED_EC, value = ecNumber, isProtected = true)
    }

    val ecUserLogged: String
        get() = get(key = CURRENT_USER_LOGGED_EC, defaultValue = EMPTY, isProtected = true) ?: EMPTY

    private fun saveEmailUserLogged(email: String?) {
        put(key = CURRENT_USER_EMAIL, value = email, isProtected = true)
    }

    private fun saveCpfUserLogged(cpf: String?) {
        put(key = CURRENT_USER_CPF, value = cpf, isProtected = true)
    }

    private fun saveUsernameUserLogged(username: String?) {
        put(key = CURRENT_USER_LOGGED_USER, value = username, isProtected = true)
    }

    private fun saveTokenUserLogged(token: String?) {
        put(key = CURRENT_USER_TOKEN, value = token, isProtected = true)
    }

    fun saveUserLogged(
        user: UserObj?,
        token: String? = null,
        userNumber: String? = null,
    ) {
        saveEcUserLogged(user?.ec)
        saveEmailUserLogged(user?.email)
        saveCpfUserLogged(user?.cpf)
        saveUsernameUserLogged(userNumber)
        saveTokenUserLogged(token)
        saveMultichannelLogin(user?.multichannelLoginType)
    }

    val currentUserLogged: UserLoggedData?
        get() {
            val token = get(key = CURRENT_USER_TOKEN, defaultValue = EMPTY, isProtected = true)
            val ecNumber =
                get(key = CURRENT_USER_LOGGED_EC, defaultValue = EMPTY, isProtected = true)
            val userNumber =
                get(key = CURRENT_USER_LOGGED_USER, defaultValue = EMPTY, isProtected = true)
            val cpf = get(key = CURRENT_USER_CPF, defaultValue = EMPTY, isProtected = true)
            val email = get(key = CURRENT_USER_EMAIL, defaultValue = EMPTY, isProtected = true)
            val multichannelLoginType =
                get(CURRENT_USER_IS_MULTICHANNEL_LOGIN_BY_MAIL, EC_NUMBER.ordinal)

            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(ecNumber)) {
                return UserLoggedData(
                    token,
                    ecNumber,
                    userNumber,
                    email,
                    cpf,
                    values()[multichannelLoginType],
                )
            }
            return null
        }

    val hasAccessManagerFirstView: Boolean
        get() {
            return try {
                val accessManagerFirstView = get(ACCESS_MANAGER_FIRST_VIEW, ArrayList())
                if (accessManagerFirstView.isEmpty()) {
                    delete(ACCESS_MANAGER_FIRST_VIEW)
                    false
                } else {
                    val ec: String = numeroEC
                    accessManagerFirstView.isEmpty().not() && accessManagerFirstView.contains(ec)
                }
            } catch (runTime: RuntimeException) {
                deleteAccessManager()
                false
            } catch (assertionError: java.lang.AssertionError) {
                deleteAccessManager()
                false
            } catch (ex: Exception) {
                ex.message?.logFirebaseCrashlytics()
                false
            }
        }

    fun removeCurrentUserLogged() {
        put(key = CURRENT_USER_TOKEN, value = EMPTY, isProtected = true)
        put(key = CURRENT_USER_LOGGED_EC, value = EMPTY, isProtected = true)
        put(key = CURRENT_USER_EMAIL, value = EMPTY, isProtected = true)
        put(key = CURRENT_USER_CPF, value = EMPTY, isProtected = true)
    }

    fun saveResearchData(researchResponse: ResearchResponse?) {
        val gson: Gson = GsonBuilder().create()
        val researchDataJson: String = gson.toJson(researchResponse, ResearchResponse::class.java)
        put(RESEARCH_DATA, researchDataJson)
    }

    val researchData: ResearchResponse?
        get() =
            try {
                val researchDataJson = get(RESEARCH_DATA, EMPTY)
                val gson = GsonBuilder().create()
                gson.fromJson(researchDataJson, ResearchResponse::class.java)
            } catch (ex: ClassCastException) {
                if (ex.message != null) {
                    FirebaseCrashlytics.getInstance().log(ex.message!!)
                }
                Timber.e(ex)
                null
            }

    fun cleanFingerprintData() {
        saveFingerprintCounter(ZERO)
        saveFingerprintData(null)
        saveFingerprintRecorded(false)
    }

    /**
     * método para adicionar os roles do usuário vinda da api
     *
     * @param tasksSet
     */
    fun saveUserActionPermissions(tasksSet: Set<String>) {
        put(LIST_PERMISION_USER, tasksSet)
    }

    /**
     * método para que mostras todas as roles do usuário vinda da api
     *
     * @return taskList
     */
    val userActionPermissions: List<String>
        get() {
            val userActionPermissions = get(LIST_PERMISION_USER, emptySet())
            return if (userActionPermissions.isEmpty()) {
                ArrayList(userActionPermissions)
            } else {
                ArrayList()
            }
        }

    /**
     * método que limpa as permissões do usuário
     */
    private fun removeUserActionPermissions() {
        delete(LIST_PERMISION_USER)
    }

    fun saveMenuApp(appMenu: AppMenuResponse?) {
        val gson: Gson = GsonBuilder().create()
        val appMenuJson: String = gson.toJson(appMenu)
        put(MENU_APP_KEY, appMenuJson)
    }

    fun clearMenuCache() {
        put(MENU_APP_KEY, null)
        removeSegmentCode()
    }

    val appMenu: AppMenuResponse?
        get() {
            val gson: Gson = GsonBuilder().create()
            val appMenuInJson: String = get(MENU_APP_KEY, null) ?: return null
            return gson.fromJson(appMenuInJson, AppMenuResponse::class.java)
        }

    fun isToShowOnboarding(onboardingName: String): Boolean {
        return get(onboardingName, true)
    }

    fun setShowOnboarding(
        onboardingName: String,
        isShow: Boolean,
    ) {
        put(onboardingName, isShow)
    }

    fun setAccessManagerFirstView(setHasFirstView: Boolean) {
        try {
            val ec: String = numeroEC
            val ecs = get(ACCESS_MANAGER_FIRST_VIEW, ArrayList())

            val ecsArray = ArrayList(ecs)
            if (setHasFirstView) {
                if (ecsArray.contains(ec).not()) {
                    ecsArray.add(ec)
                }
            } else {
                ecsArray.remove(ec)
            }
            put(ACCESS_MANAGER_FIRST_VIEW, ecsArray)
        } catch (runTime: RuntimeException) {
            deleteAccessManager()
        } catch (assertionError: java.lang.AssertionError) {
            deleteAccessManager()
        } catch (ex: Exception) {
            ex.message?.logFirebaseCrashlytics()
        }
    }

    var locationPermissionCheck: Boolean
        get() = get(PERMISSION_LOCATION_CHECK_KEY, true)
        set(isFirstTimeCheck) {
            put(PERMISSION_LOCATION_CHECK_KEY, isFirstTimeCheck)
        }

    val isShowBalanceValue: Boolean
        get() = get(SHOW_BALANCE_VALUE_PIX, true)

    fun saveShowBalanceValue(isShow: Boolean) {
        put(SHOW_BALANCE_VALUE_PIX, isShow)
    }

    val isOnboardingPixKeysWasViewed: Boolean
        get() = get(ONBOARDING_PIX_KEYS_WAS_VIEWED, false)

    fun saveOnboardingPixKeysWasViewed() {
        put(ONBOARDING_PIX_KEYS_WAS_VIEWED, true)
    }

    val isPixOnboardingHomeViewed: Boolean
        get() = get(IS_PIX_ONBOARDING_HOME_VIEWED, false, isProtected = true)

    fun savePixOnboardingHomeViewed() {
        put(IS_PIX_ONBOARDING_HOME_VIEWED, true, isProtected = true)
    }

    fun setCameraPermissionCheck(isFirstTimeCheck: Boolean) {
        put(PERMISSION_CAMERA_CHECK_KEY, isFirstTimeCheck)
    }

    val cameraPermissionCheck: Boolean
        get() =
            try {
                get(PERMISSION_CAMERA_CHECK_KEY, true)
            } catch (e: java.lang.Exception) {
                true
            }

    val isShowPixOnboardingExtract: Boolean
        get() = get(IS_SHOW_PIX_ONBOARDING_EXTRACT, false)

    fun saveShowPixOnboardingExtract() {
        put(IS_SHOW_PIX_ONBOARDING_EXTRACT, true)
    }

    fun saveDeepLinkModel(model: DeepLinkModel) {
        val modelJson = GsonBuilder().create().toJson(model)
        put(DEEPLINK_MODEL, modelJson)
    }

    val deepLinkModel: DeepLinkModel?
        get() {
            val modelJson = get(DEEPLINK_MODEL, null)
            return modelJson?.let {
                try {
                    GsonBuilder().create().fromJson(it, DeepLinkModel::class.java)
                } catch (e: Exception) {
                    e.message.logFirebaseCrashlytics()
                    null
                }
            }
        }

    fun deleteDeepLinkModel() = delete(DEEPLINK_MODEL)

    fun saveHolderIntentId(intentId: String) {
        put(HOLDER_INTENT_ID, intentId)
    }

    val holderIntentId: String?
        get() = get(HOLDER_INTENT_ID, EMPTY)

    fun deleteHolderIntentId() {
        delete(HOLDER_INTENT_ID)
    }

    fun saveHolderRedirectUri(redirectId: String) {
        put(HOLDER_REDIRECT_URI, redirectId)
    }

    val holderRedirectUri: String?
        get() = get(HOLDER_REDIRECT_URI, EMPTY)

    fun deleteHolderRedirectUri() {
        delete(HOLDER_REDIRECT_URI)
    }

    fun saveAuthorizationCodeOPF(authorizationCode: String) {
        put(AUTHORIZATION_CODE_OPF, authorizationCode)
    }

    val authorizationCodeOPF: String?
        get() = get(AUTHORIZATION_CODE_OPF, EMPTY)

    fun deleteAuthorizationCodeOPF() {
        delete(AUTHORIZATION_CODE_OPF)
    }

    fun saveRequestIdOPF(requestId: String) {
        put(REQUEST_ID_OPF, requestId)
    }

    val requestIdOPF: String?
        get() = get(REQUEST_ID_OPF, EMPTY)

    fun deleteRequestIdOPF() {
        delete(REQUEST_ID_OPF)
    }

    fun saveIdTokenOPF(requestId: String) {
        put(ID_TOKEN_OPF, requestId)
    }

    val idTokenOPF: String?
        get() = get(ID_TOKEN_OPF, EMPTY)

    fun deleteIdTokenOPF() {
        delete(ID_TOKEN_OPF)
    }

    fun saveErrorDescriptionOPF(errorDescription: String) {
        put(ERROR_DESCRIPTION_OPF, errorDescription)
    }

    val errorDescriptionOPF: String?
        get() = get(ERROR_DESCRIPTION_OPF, EMPTY)

    fun deleteErrorDescriptionOPF() {
        delete(ERROR_DESCRIPTION_OPF)
    }

    fun saveShareIdOPF(shareId: String) {
        put(SHARE_ID_OPF, shareId)
    }

    val shareIdOPF: String?
        get() = get(SHARE_ID_OPF, EMPTY)

    fun deleteShareIdOPF() {
        delete(SHARE_ID_OPF)
    }

    fun saveInfoDetailsShare(infoDetailsShare: String) {
        put(INFO_DETAILS_SHARE, infoDetailsShare)
    }

    val infoDetailsShare: String?
        get() = get(INFO_DETAILS_SHARE, EMPTY)

    fun deleteInfoDetailsShare() {
        delete(INFO_DETAILS_SHARE)
    }

    val isOnboardingOpenFinanceWasViewed: Boolean
        get() = get(ONBOARDING_OPEN_FINANCE_WAS_VIEWED, false)

    fun saveOnboardingOpenFinanceWasViewed() {
        put(ONBOARDING_OPEN_FINANCE_WAS_VIEWED, true)
    }

    fun saveMktExternalDeeplink(mktExternalDeeplink: String) {
        put(DEEPLINK_EXTERNAL_MKT_URL, mktExternalDeeplink)
    }

    val mktExternalDeeplink: String?
        get() = get(DEEPLINK_EXTERNAL_MKT_URL, EMPTY)

    fun deleteMktExternalDeeplink() {
        delete(DEEPLINK_EXTERNAL_MKT_URL)
    }

    val isUserViewedIDOnboarding: Boolean
        get() = get(USER_VIEWD_ID_ONBOARDING, false)

    fun saveUserViewedIDOnboarding() {
        put(USER_VIEWD_ID_ONBOARDING, true)
    }

    val isCieloUnificaOnboardingShown: Boolean
        get() = get(CIELO_UNIFICA_ONBOARDING, false)

    fun setCieloUnificaOnboardingShown() {
        put(CIELO_UNIFICA_ONBOARDING, true)
    }

    fun setShowErrorIDOnboardingP2(value: Boolean) {
        put(IS_SHOW_ERROR_ID_ONBOARDING_P2, value)
    }

    val isShowErrorIDOnboardingP2: Boolean
        get() = get(IS_SHOW_ERROR_ID_ONBOARDING_P2, false)

    fun saveCurrentUserName(value: String) {
        put(CURRENT_USER_NAME, value, isProtected = true)
    }

    val currentUserName: String?
        get() = get(CURRENT_USER_NAME, EMPTY, isProtected = true)

    fun setSawTerminalScreenReady() {
        put(key = TAP_ON_PHONE_SAW_TERMINAL_SCREEN_IS_READY, value = true, isProtected = true)
    }

    fun isSawTerminalScreenReady() =
        get(
            key = TAP_ON_PHONE_SAW_TERMINAL_SCREEN_IS_READY,
            defaultValue = false,
            isProtected = true,
        )

    fun getFingerPrintIds(): Set<Int>? {
        val setFingerIds: MutableSet<Int> = HashSet()
        val fingersIdAgrupada: String? =
            get(key = FINGERPRINT_SAVED_ID, defaultValue = EMPTY, isProtected = true)
        fingersIdAgrupada?.let {
            if (it.isNotEmpty()) {
                for (fingerId in it.split(TOKEN_SEP_FINGER_IDS).toTypedArray()) {
                    try {
                        if (fingerId.isNotEmpty()) {
                            setFingerIds.add(fingerId.toInt())
                        }
                    } catch (exception: Exception) {
                        exception.message?.logFirebaseCrashlytics()
                    }
                }
            }
        }

        return setFingerIds
    }

    fun setFingerprintIds(listIds: Set<Int?>) {
        try {
            val fingerIdsString = StringBuilder()
            for (id in listIds) {
                fingerIdsString.append(id).append(TOKEN_SEP_FINGER_IDS)
            }
            put(key = FINGERPRINT_SAVED_ID, value = fingerIdsString.toString(), isProtected = true)
        } catch (exception: Exception) {
            FirebaseCrashlytics.getInstance().recordException(exception)
        }
    }

    fun put(
        key: String,
        value: String?,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                encryptedSharedPreferences?.edit()?.putString(key, value)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun put(
        key: String,
        value: Long,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                encryptedSharedPreferences?.edit()?.putLong(key, value)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun put(
        key: String,
        value: Boolean,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                encryptedSharedPreferences?.edit()?.putBoolean(key, value)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun put(
        key: String,
        value: Int,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                encryptedSharedPreferences?.edit()?.putInt(key, value)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun put(
        key: String,
        value: Set<String>,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                encryptedSharedPreferences?.edit()?.putStringSet(key, value)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun put(
        key: String,
        value: ArrayList<String>,
        isProtected: Boolean = false,
    ) {
        try {
            if (isProtected) {
                val set: MutableSet<String> = HashSet()
                set.addAll(value)
                encryptedSharedPreferences?.edit()?.putStringSet(key, set)?.apply()
            } else {
                putHawk(key, value)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun get(
        key: String,
        defaultValue: String?,
        isProtected: Boolean = false,
    ): String? {
        if (isProtected) {
            val value = encryptedSharedPreferences?.getString(key, null)
            if (value != null) {
                return value
            }
        }

        return Hawk.get(key, defaultValue)
    }

    fun get(
        key: String,
        defaultValue: Int,
        isProtected: Boolean = false,
    ): Int {
        if (isProtected) {
            return encryptedSharedPreferences?.getInt(key, defaultValue) ?: defaultValue
        }

        return Hawk.get(key, defaultValue)
    }

    fun get(
        key: String,
        defaultValue: Long,
        isProtected: Boolean = false,
    ): Long {
        if (isProtected) {
            return encryptedSharedPreferences?.getLong(key, defaultValue) ?: defaultValue
        }

        return Hawk.get(key, defaultValue)
    }

    fun get(
        key: String,
        defaultValue: Boolean,
        isProtected: Boolean = false,
    ): Boolean {
        if (isProtected) {
            return encryptedSharedPreferences?.getBoolean(key, defaultValue) ?: false
        }

        return Hawk.get(key, defaultValue)
    }

    fun get(
        key: String,
        defaultValue: Set<String>,
        isProtected: Boolean = false,
    ): Set<String> {
        if (isProtected) {
            val value = encryptedSharedPreferences?.getStringSet(key, defaultValue) ?: setOf()
            if (value.isEmpty().not()) return value
        }

        return Hawk.get(key, defaultValue)
    }

    fun get(
        key: String,
        defaultValue: ArrayList<String>,
        isProtected: Boolean = false,
    ): ArrayList<String> {
        return try {
            if (isProtected) {
                encryptedSharedPreferences?.getStringSet(key, null)?.let { sets ->
                    ArrayList(sets)
                } ?: defaultValue
            } else {
                Hawk.get(key, defaultValue) ?: defaultValue
            }
        } catch (runTime: RuntimeException) {
            Hawk.delete(key)
            encryptedSharedPreferences?.edit()?.remove(key)?.apply()
            defaultValue
        } catch (ex: Exception) {
            ex.message?.logFirebaseCrashlytics()
            defaultValue
        }
    }

    private fun getFingerPrintData(): ByteArray? {
        return try {
            val fingerPrintData =
                encryptedSharedPreferences?.getString(FINGERPRINT_DATA, null)
                    ?.toByteArray(Charsets.ISO_8859_1)

            if (fingerPrintData?.firstOrNull() == null) {
                byteArrayOf()
            } else {
                fingerPrintData
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            byteArrayOf()
        }
    }

    fun getInteractOffers(): String? {
        return try {
            return get(key = LIST_INTERACT_OFFER, defaultValue = EMPTY, isProtected = true)
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            null
        }
    }

    fun putInteractOffers(offers: List<HiringOffers>) {
        val offersJson = Gson().toJson(offers)
        put(key = LIST_INTERACT_OFFER, value = offersJson, isProtected = true)
    }

    fun putProcessingOffer(offerName: Int) {
        val existingOffers = getProcessingOffers().toMutableList()
        existingOffers.add(offerName)
        val offersJson = Gson().toJson(existingOffers)
        put(key = OFFER_PROCESSING_LIST, value = offersJson, isProtected = true)
    }

    fun getProcessingOffers(): List<Int> {
        return try {
            val offersJson = get(key = OFFER_PROCESSING_LIST, defaultValue = EMPTY, isProtected = true)
            if (offersJson.isNullOrEmpty()) {
                return emptyList()
            }
            val listType = object : TypeToken<List<Int>>() {}.type
            return Gson().fromJson(offersJson, listType)
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            emptyList()
        }
    }

    fun getSegmentCode(): String? {
        return try {
            get(key = KEEP_SEGMENT_CODE, defaultValue = EMPTY, isProtected = true)
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            null
        }
    }

    fun putSegmentCode(segmentCode: String) {
        put(key = KEEP_SEGMENT_CODE, value = segmentCode, isProtected = true)
    }

    fun removeSegmentCode() {
        delete(key = KEEP_SEGMENT_CODE)
    }

    private fun putHawk(
        key: String,
        defaultValue: Int,
    ) {
        Hawk.put(key, defaultValue)
    }

    private fun putHawk(
        key: String,
        value: String?,
    ) {
        Hawk.put(key, value)
    }

    private fun putHawk(
        key: String,
        value: Boolean,
    ) {
        Hawk.put(key, value)
    }

    private fun putHawk(
        key: String,
        value: Set<String>,
    ) {
        Hawk.put(key, value)
    }

    private fun putHawk(
        key: String,
        value: ArrayList<String>,
    ) {
        Hawk.put(key, value)
    }

    private fun putHawk(
        key: String,
        value: Long,
    ) {
        Hawk.put(key, value)
    }

    fun delete(key: String?) {
        key?.let {
            deleteAllData(
                {
                    put(key = it, value = null, isProtected = true)
                },
                {
                    Hawk.delete(it)
                },
            )
        }
    }

    private fun deleteAllData(vararg action: () -> Unit) {
        try {
            action.forEach { itAction ->
                itAction.invoke()
            }
        } catch (ex: Exception) {
            ex.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    fun cacheClear(context: Context) {
        try {
            encryptedSharedPreferences?.edit()?.clear()?.apply()
            val dir = File(context.applicationInfo.dataDir, PREFS_FILE_NAME)
            File(dir, ENCRYPTED_PREFS_FILE).deleteOnExit()
            Hawk.deleteAll()
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
    }

    fun deleteAccessManager() {
        try {
            Hawk.delete(ACCESS_MANAGER_FIRST_VIEW)
            encryptedSharedPreferences?.edit()?.remove(ACCESS_MANAGER_FIRST_VIEW)?.apply()
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
    }

    fun deleteUserInformation() {
        try {
            Hawk.delete(USER_INFORMATION)
            encryptedSharedPreferences?.edit()?.remove(USER_INFORMATION)?.apply()
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
    }

    companion object {
        private val VERSION: Int = 1
        private val KEY_VERSION: String = "version"
        val USER_TOKEN: String = "user_token"
        private val SHOW_MASSIVA: String = "show_massiva"
        private val KEEP_LOGIN: String = "guardar_login_user"
        private val KEEP_STATUS_MFA: String = "guardar_status_mfa"
        private val KEEP_STATUS_SEED: String = "guardar_status_seed"
        private val KEEP_NUMERO_EC: String = "numero_ec"
        private val KEEP_USER_NAME: String = "guardar_user_name"
        private val KEEP_WELCOME: String = "guardar_welcome"
        private val KEEP_CANCEL: String = "guardar_tuto_cancel"
        private val KEEP_BANNER_BALCAO_RECEBIVEIS: String = "guardar_status_balcao_recebiveis"
        private val KEEP_RECEBA_MAIS: String = "guardar_receba_mais"
        private val KEEP_BANNER_MIGRATION: String = "guardar_banner_migration"
        private val KEEP_BARCODE: String = "guardar_barcode"
        private val CONVIVENCIA_STATUS: String = "convivencia_status"
        private val FIRST_USE: String = "first_use_onboarding"
        private val CANCEL_TUTORIAL_EXIBITION_COUNT: String = "cancel_tutorial_exibition_count"
        private val STATUS_FAROL: String = "status_farol"
        private val RESEARCH_DATA: String = "research_data"
        private val CONTRATED_FAROL: String = "contrated_foral"
        private val KEEP_STEP_TWO: String = "guardar_esta_no_passo_2"
        private val KEEP_USER_PASSWORD: String = "guardar_user_password"
        private val TOKEN_FCM: String = "TOKEN_FIRE_BASE_CLOUD"
        private val TOKEN_IMPERSONATE: String = "TOKEN_IMPERSONATE"
        private val LIST_PERMISION_USER: String = "LIST_PERMISION_USER"
        private val TOKEN_FCM_SENT: String = "TOKEN_FIRE_BASE_CLOUD_SENT"
        private val USER_INFORMATION: String = "USER_INFORMATION"
        private val REFRESH_TOKEN: String = "REFRESH_TOKEN"
        private val USER_LOGGED: String = "br.com.cielo.userLogged"
        private val ARV_INFORMATIONS: String = "br.com.cielo.arvInformation"
        private val CURRENT_USER_LOGGED_EC: String = "br.com.cielo.currentUserEcNumber"
        private val CURRENT_USER_LOGGED_USER: String = "br.com.cielo.currentUserUserNumber"
        private val CURRENT_USER_TOKEN: String = "br.com.cielo.currentUserToken"
        private val CURRENT_USER_INPUT_TYPE: String = "CURRENT_USER_INPUT_TYPE"
        private val STATUS_RECEBA_MAIS: String = "br.com.cielo.statusMyCards"
        private val STATUS_MY_CARDS: String = "br.com.cielo.statusRecebaMais"
        private val ACCESS_MANAGER_FIRST_VIEW: String = "br.com.cielo.accessManagerFirstView"
        private val STATUS_RECEBA_MAIS_STATUS_PRODUCT: String =
            "br.com.cielo.receba.mais.statusProduct"
        private val MENU_APP_KEY: String = "br.com.cielo.main.menuApp"
        private val USER_PJ: String = "br.com.cielo.user"
        private val LEGAL_ENTITY: String = "br.com.cielo.legalEntity"
        private val TURBO_REGISTRATION_ERROR_STEP: String = "br.com.cielo.turboRegistrationErrorStep"
        private val CURRENT_USER_EMAIL: String = "br.com.cielo.currentUserEmail"
        private val CURRENT_USER_CPF: String = "br.com.cielo.currentUserCpf"
        private val CURRENT_USER_IS_MULTICHANNEL_LOGIN_BY_MAIL: String =
            "br.com.cielo.multichannelLoginType"
        private val NOTE_RESEARCH: String = "br.com.cielo.noteResearch"
        private val DESCRIPTION_RESEARCH: String = "br.com.cielo.descriptionResearch"
        private val SENT_RESEARCH: String = "br.com.cielo.sentResearch"
        private val FINGERPRINT_SAVED: String = "br.com.cielo.fingerprint.fingerprintRecorded"
        private val FINGERPRINT_SAVED_ID: String =
            "br.com.cielo.fingerprint.fingerprintRecorded.IDS"
        private val TOKEN_SEP_FINGER_IDS: String =
            "br.com.cielo.fingerprint.fingerprintRecorded.SEP.IDS"
        private val FINGERPRINT_DATA: String = "br.com.cielo.fingerprint.fingerprintData"
        private val FINGERPRINT_NOTIFICATION_COUNTER_KEY: String =
            "br.com.cielo.fingerprint.fingerprintCounter"
        private val NEWLY_ACCREDITED: String = "saveNewlyAccredited"
        private val ONBOARDING_APP_NEWS_PUSH_NOTIFICATION_KEY: String =
            "ONBOARDING_APP_NEWS_PUSH_NOTIFICATION_KEY"
        private val SHOW_BIOMETRIC_NOTIFICATION: String = "SHOW_BIOMETRIC_NOTIFICATION"
        private val CALLED_BIOMETRIC_NOTIFICATION_BY_LOGIN: String = "CALLED_BIOMETRIC_NOTIFICATION_BY_LOGIN"
        private val SHOW_BALANCE_VALUE_PIX: String = "SHOW_BALANCE_VALUE_PIX"
        private val ONBOARDING_PIX_KEYS_WAS_VIEWED: String = " ONBOARDING_PIX_KEYS_WAS_VIEWED"
        private val USER_VIEWD_ID_ONBOARDING: String = " USER_VIEWD_ID_ONBOARDING"
        private val IS_SHOW_SUCCESS_HIRING_PIX: String = " IS_SHOW_SUCCESS_HIRING_PIX"
        val IS_PIX_ONBOARDING_HOME_VIEWED: String = "IS_PIX_ONBOARDING_HOME_VIEWED"
        private val IS_SHOW_PIX_ONBOARDING_EXTRACT: String = "IS_SHOW_PIX_ONBOARDING_EXTRACT"
        private val PERMISSION_LOCATION_CHECK_KEY: String = "PERMISSION_LOCATION_CHECK_KEY"
        private val PERMISSION_CAMERA_CHECK_KEY: String = "PERMISSION_CAMERA_CHECK_KEY"
        private val DEEPLINK_FLOW_ID: String = "DEEPLINK_FLOW_ID"
        private val DEEPLINK_EXTERNAL_MKT_URL: String = "DEEPLINK_EXTERNAL_MKT_URL"
        private const val DEEPLINK_MODEL = "DEEPLINK_MODEL"
        private val CIELO_UNIFICA_ONBOARDING: String = "CIELO_UNIFICA_ONBOARDING"
        private val IS_SHOW_ERROR_ID_ONBOARDING_P2: String = "IS_SHOW_ERROR_ID_ONBOARDING_P2"
        private val CURRENT_USER_NAME = "CURRENT_USER_NAME"
        private val LIST_INTERACT_OFFER = "LIST_INTERACT_OFFER"
        private val OFFER_PROCESSING_LIST = "OFFER_PROCESSING_LIST"
        private val TAP_ON_PHONE_SAW_TERMINAL_SCREEN_IS_READY: String =
            "TAP_ON_PHONE_SAW_TERMINAL_SCREEN_IS_READY"
        private val POS_VIRTUAL_WHITE_LIST: String = "POS_VIRTUAL_WHITE_LIST"
        val HOLDER_INTENT_ID = "HOLDER_INTENT_ID"
        val HOLDER_REDIRECT_URI = "HOLDER_REDIRECT_URI"
        private val ONBOARDING_OPEN_FINANCE_WAS_VIEWED: String =
            "ONBOARDING_OPEN_FINANCE_WAS_VIEWED"
        private val KEEP_SEGMENT_CODE: String = "KEEP_SEGMENT_CODE"
        private val MFA_MIGRATION_LAST_TENTATIVE: String = "MFA_MIGRATION_LAST_TENTATIVE"
        const val MFA_SERVER_TIME = "MFA_SERVER_TIME"
        const val MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED = "MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED"
        const val ARV_WHATSAPP_NEWS_ALREADY_VIEWED = "ARV_WHATSAPP_NEWS_ALREADY_VIEWED"
        const val ARV_WHATSAPP_NEWS_DISMISSED_COUNTER = "ARV_WHATSAPP_NEWS_DISMISSED_COUNTER"

        val AUTHORIZATION_CODE_OPF = "AUTHORIZATION_CODE_OPF"
        val REQUEST_ID_OPF = "REQUEST_ID_OPF"
        val ID_TOKEN_OPF = "ID_TOKEN_OPF"
        val ERROR_DESCRIPTION_OPF = "ERROR_DESCRIPTION_OPF"
        val SHARE_ID_OPF = "SHARE_ID_OPF"
        val INFO_DETAILS_SHARE = "INFO_DETAILS_SHARE"
        private var instance: UserPreferences? = null

        val userPreferencesKeys: List<String> =
            Arrays.asList(
                USER_TOKEN,
                SHOW_MASSIVA,
                KEEP_LOGIN,
                KEEP_STATUS_MFA,
                KEEP_STATUS_SEED,
                KEEP_NUMERO_EC,
                KEEP_USER_NAME,
                KEEP_WELCOME,
                KEEP_CANCEL,
                KEEP_BANNER_BALCAO_RECEBIVEIS,
                KEEP_RECEBA_MAIS,
                KEEP_BANNER_MIGRATION,
                KEEP_BARCODE,
                CONVIVENCIA_STATUS,
                FIRST_USE,
                CANCEL_TUTORIAL_EXIBITION_COUNT,
                STATUS_FAROL,
                RESEARCH_DATA,
                CONTRATED_FAROL,
                KEEP_STEP_TWO,
                KEEP_USER_PASSWORD,
                TOKEN_FCM,
                TOKEN_IMPERSONATE,
                LIST_PERMISION_USER,
                TOKEN_FCM_SENT,
                USER_INFORMATION,
                REFRESH_TOKEN,
                USER_LOGGED,
                ARV_INFORMATIONS,
                CURRENT_USER_LOGGED_EC,
                CURRENT_USER_LOGGED_USER,
                CURRENT_USER_TOKEN,
                CURRENT_USER_INPUT_TYPE,
                STATUS_RECEBA_MAIS,
                STATUS_MY_CARDS, // STATUS_READ_STATUS_PRODUCT,
                STATUS_RECEBA_MAIS_STATUS_PRODUCT,
                MENU_APP_KEY,
                USER_PJ,
                CURRENT_USER_EMAIL,
                CURRENT_USER_CPF,
                CURRENT_USER_IS_MULTICHANNEL_LOGIN_BY_MAIL,
                NOTE_RESEARCH,
                DESCRIPTION_RESEARCH,
                SENT_RESEARCH,
                FINGERPRINT_SAVED,
                FINGERPRINT_DATA,
                FINGERPRINT_NOTIFICATION_COUNTER_KEY,
                NEWLY_ACCREDITED,
                ONBOARDING_APP_NEWS_PUSH_NOTIFICATION_KEY,
                ONBOARDING.MFA,
                ONBOARDING.SUPERLINK,
                FINGERPRINT_SAVED_ID,
                ACCESS_MANAGER_FIRST_VIEW,
                SHOW_BALANCE_VALUE_PIX,
                IS_SHOW_PIX_ONBOARDING_EXTRACT,
                ONBOARDING_PIX_KEYS_WAS_VIEWED,
                IS_SHOW_SUCCESS_HIRING_PIX,
                IS_PIX_ONBOARDING_HOME_VIEWED,
                PERMISSION_LOCATION_CHECK_KEY,
                PERMISSION_CAMERA_CHECK_KEY,
                DEEPLINK_FLOW_ID,
                DEEPLINK_EXTERNAL_MKT_URL,
                DEEPLINK_MODEL,
                POS_VIRTUAL_WHITE_LIST,
                MFA_MIGRATION_LAST_TENTATIVE,
                MFA_SERVER_TIME,
                HOLDER_INTENT_ID,
                HOLDER_REDIRECT_URI,
                ONBOARDING_OPEN_FINANCE_WAS_VIEWED,
                MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED,
                ARV_WHATSAPP_NEWS_ALREADY_VIEWED,
                ARV_WHATSAPP_NEWS_DISMISSED_COUNTER,
                AUTHORIZATION_CODE_OPF,
                REQUEST_ID_OPF,
                ID_TOKEN_OPF,
                ERROR_DESCRIPTION_OPF,
                SHARE_ID_OPF,
                INFO_DETAILS_SHARE
            )

        @Synchronized
        fun getInstance(): UserPreferences {
            if (instance == null) {
                instance = UserPreferences()
            }
            return instance!!
        }

        fun init(context: Context) {
            val userPreferences = getInstance()

            try {
                configureEncryptedSharedPreferences(context, userPreferences)
            } catch (exception: Exception) {
                exception.message?.logFirebaseCrashlytics()

                deleteMasterKeyEntry()
                deleteExistingEncryptedSharedPreferences(context)
                configureEncryptedSharedPreferences(context, userPreferences)
            }

            userPreferences.checkMigration()
        }

        private fun deleteExistingEncryptedSharedPreferences(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.deleteSharedPreferences(ENCRYPTED_PREFS_FOLDER)
            } else {
                context.getSharedPreferences(ENCRYPTED_PREFS_FOLDER, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
            }
        }

        private fun deleteMasterKeyEntry() {
            KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                load(null)
                deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            }
        }

        private fun configureEncryptedSharedPreferences(
            context: Context,
            userPreferences: UserPreferences,
        ) {
            val masterKey =
                MasterKey.Builder(
                    context,
                    MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                ).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

            userPreferences.encryptedSharedPreferences =
                EncryptedSharedPreferences.create(
                    context,
                    ENCRYPTED_PREFS_FOLDER,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
                )
        }
    }
}
