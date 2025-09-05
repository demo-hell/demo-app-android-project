package br.com.mobicare.cielo.newLogin

import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference

//fun UserPreferences.saveUserData(token: String?, userInputType: Int, username: String, identification: String) {
//    this.saveTokenUserLogged(token)
//    this.saveUsernameUserLogged(username)
//    this.saveUserInputTypeUserLogged(userInputType)
//    when(userInputType) {
//        USER_INPUT_EC_NUMBER -> saveEcUserLogged(identification)
//        USER_INPUT_CPF -> saveCpfUserLogged(username)
//        USER_INPUT_EMAIL -> saveEmailUserLogged(username)
//    }
//}
//
//fun UserPreferences.clearDataByUserKeep() {
//    val userLogged = currentUserLogged
//    val userTypeInput = this.userInputTypeUserLogged
//    val isKeepLogin = this.keepLogin
//    val username = when(userTypeInput) {
//        USER_INPUT_EC_NUMBER -> userLogged.ecNumber
//        USER_INPUT_CPF -> userLogged.cpf
//        USER_INPUT_EMAIL -> userLogged.email
//        else -> ""
//    }
//    this.clearUserData()
//
//    saveKeep(isKeepLogin)
//
//    if (isKeepLogin) {
//        saveUserData(null, userTypeInput, userLogged.userNumber, userLogged.ecNumber)
//    }
//}

fun UserPreferences.clearDataByUserKeep() {
    val isKeepLogin = this.keepLogin
    val identification = this.numeroEC
    val username = this.userName
    val userTypeInput = this.userInputTypeUserLogged

    val bckShowOnboardingAppNewsPushNotification = this.isShowOnboardingAppNewsPushNotification

    val bckFingerprintCloseCount = this.fingerprintCloseCount()
    val bckFingerprintData = this.fingerprintData
    val bckIsCancelStatus = this.isCancelStatus
    val bckCancelTutorialExibitionCount = this.cancelTutorialExibitionCount
    val bckiShowOnboardingLoggiSuperLink = isToShowOnboarding(UserPreferences.ONBOARDING.SUPERLINK)
    val bckShowOnboardingMfa = isToShowOnboarding(UserPreferences.ONBOARDING.MFA)

    val bckNewlyAccredited = this.newlyAccredited
    val isFingerprintRecorded = this.fingerprintRecorded

    val loginObj:LoginObj? = MenuPreference.instance.getLoginObj()

    this.clearUserData(this.isRecebaMaisChecked)

    this.saveCancelStatus(bckIsCancelStatus)
    this.saveCancelTutorialExibitionCount(bckCancelTutorialExibitionCount)
    this.saveNewlyAccredited(bckNewlyAccredited)
    this.saveFingerprintCounter(bckFingerprintCloseCount)
    this.saveOnboardingAppNewsPushNotification(bckShowOnboardingAppNewsPushNotification)
    this.setShowOnboarding(UserPreferences.ONBOARDING.SUPERLINK, bckiShowOnboardingLoggiSuperLink)
    this.setShowOnboarding(UserPreferences.ONBOARDING.MFA, bckShowOnboardingMfa)
    this.saveFirstUse(false)

    if (isKeepLogin) {
        this.saveKeep(true)
        this.keepEC(identification)
        this.keepUserName(username)
        this.saveUserInputTypeUserLogged(userTypeInput)

        loginObj?.let {
            it.token = null
            MenuPreference.instance.saveLoginObj(it)
        }

        if (isFingerprintRecorded) {
            saveFingerprintData(bckFingerprintData)
            saveFingerprintRecorded(isFingerprintRecorded)
        }
    } else {
        saveFingerprintRecorded(false)
        MfaUserInformation(UserPreferences.getInstance()).cleanMfaRegisters()
    }
}

fun UserPreferences.erase(keysToDelete: List<String>) {
    val configuration = ConfigurationPreference.instance.configurationValues
    val userPreferences = UserPreferences.getInstance()

    keysToDelete.forEach { key ->
        userPreferences.delete(key)
    }

    if (configuration != null) {
        ConfigurationPreference.instance.saveConfig(configuration)
    }
}
