package br.com.mobicare.cielo.newLogin

import br.com.mobicare.cielo.accessManager.model.PendingInviteItem
import br.com.mobicare.cielo.commons.ui.widget.VersionBlockBottomSheetFragment
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.newLogin.domain.LoginResponse


interface NewLoginContract {
    interface View {
        fun render(state: NewLoginState)
        fun openForgotPasswordView()
        fun changeLoginButtonState(isEnabled: Boolean)
        fun callMainLoggedScreen(password: ByteArray? = null)
        fun showIdOnboarding()
        fun saveRoles(roles: List<String>)
        fun setUsername(username: String)
        fun setKeepData(isKeep: Boolean)
        fun showUserBiometricPrompt()
        fun setClickCallBiometricPrompt()
        fun showTokenButton()
        fun showTokenActivity(username: String)
        fun showVersionUpdaterDialog(isToForceUpdate: Boolean,
                                     versionTerminateListener: VersionBlockBottomSheetFragment.OnVersionTerminateListener)

        fun showUserLostDataWarning()
        fun showUpdateRegister()

        fun enableButtonWhenFingerprintEnabled()
        fun showConfigureFingerprintToTokenActivityError()
        fun showLGPD(elegible: LgpdElegibilityEntity, password: ByteArray? = null)
        fun showInternalUserScreen(password: ByteArray? = null)
        fun onSuccessFeatureToggle(modal: FeatureToggleModal)
        fun showInvite(invite: PendingInviteItem, onboardingRequired: Boolean) {}
        fun showPasswordExpired() {}
        fun setupCreateButtonVisibility(visibility: Boolean)
        fun showNewDeviceDetected(foreign: Boolean)
        fun successRegisterDevice(loginResponse: LoginResponse)
        fun showSdkError(id: Int)
    }

    interface Presenter {
        fun onCreated()
        fun onFirstStepNextButtonClicked(identification: String)
        fun onBackButtonClicked()
        fun onForgotPasswordButtonClicked()
        fun onUsernameChanged(value: String?)
        fun onPasswordChanged(value: String?)
        fun onLoginButtonClicked(identification: String, username: String, password: String, isKeepData: Boolean, fingerprint: String?)
        fun onLoginByFingerprint(password: String,allowMeToken: String?)
        fun callFingerprintToTokenActivity()
        fun onGoToFirstScreenCleanData()
        fun fingerprintIsEnabled(isEnable: Boolean)
        fun checkCreateButton()
        fun resetNewDevice()
        fun registerDevice(faceIdToken: String?, fingerprint: String?)
        fun continueLogin(loginResponse: LoginResponse, identification: String, username: String, password: String, isKeepData: Boolean)
        fun onPause()
        fun onResume()
    }
}