package br.com.mobicare.cielo.newLogin

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_HW_NOT_PRESENT
import androidx.biometric.BiometricPrompt.ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricPrompt.ERROR_LOCKOUT_PERMANENT
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet.HeaderConfigurator
import br.com.cielo.libflue.check.CheckBoxStyle
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.cielo.libflue.util.ELEVEN
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.FIVE
import br.com.cielo.libflue.util.ONE_NEGATIVE
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.LegacyUserInviteReceiveNavigationActivity
import br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.accept.InviteLegacyUserAcceptFragment.Companion.INVITE_ARGS
import br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.accept.InviteLegacyUserAcceptFragment.Companion.ONBOARDING_REQUIRED_ARGS
import br.com.mobicare.cielo.accessManager.model.PendingInviteItem
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.biometricNotification.ui.BiometricNotificationBottomSheetFragment
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.LOGIN_INTERACAO_1
import br.com.mobicare.cielo.commons.analytics.LOGIN_INTERACAO_2
import br.com.mobicare.cielo.commons.analytics.LOGIN_LABEL_EMAIL
import br.com.mobicare.cielo.commons.analytics.LOGIN_LABEL_SENHA
import br.com.mobicare.cielo.commons.analytics.LOGIN_PASSO_1
import br.com.mobicare.cielo.commons.analytics.LOGIN_PASSO_2
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.analytics.MFA_NOVO_TOKEN
import br.com.mobicare.cielo.commons.analytics.MFA_NOVO_TOKEN_LOGIN
import br.com.mobicare.cielo.commons.analytics.MFA_VISUALIZAR_TOKEN
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LGPD_ELEGIBLE
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_LGPD_START_BY_LOGIN
import br.com.mobicare.cielo.commons.constants.AT_SIGN
import br.com.mobicare.cielo.commons.constants.EIGHT_HUNDRED
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.SIX_HUNDRED
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.AppHelper
import br.com.mobicare.cielo.commons.helpers.BiometricHelper.Companion.canAuthenticateWithBiometrics
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.VersionBlockBottomSheetFragment
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants.AF_SCREEN_NAME
import br.com.mobicare.cielo.commons.utils.AppsFlyerUtil
import br.com.mobicare.cielo.commons.utils.EVENT_ENTERBUTTON
import br.com.mobicare.cielo.commons.utils.EVENT_LOGIN
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.containsOnlyNumbers
import br.com.mobicare.cielo.commons.utils.createAead
import br.com.mobicare.cielo.commons.utils.fingerprint.createBiometricPromptFragmentActivity
import br.com.mobicare.cielo.commons.utils.fingerprint.isAndroidVersionOorOMR1
import br.com.mobicare.cielo.commons.utils.fingerprint.isFingerprintSequentialID
import br.com.mobicare.cielo.commons.utils.fingerprint.saveFingerPrints
import br.com.mobicare.cielo.commons.utils.fingerprint.validateNewFingerprintAdded
import br.com.mobicare.cielo.commons.utils.hideKeyBoard
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.warning.PriorityWarningModal
import br.com.mobicare.cielo.databinding.ActivityNewLoginBinding
import br.com.mobicare.cielo.databinding.BottomSheetAnotherAccountBinding
import br.com.mobicare.cielo.databinding.LayoutNewLoginFirstStepBinding
import br.com.mobicare.cielo.databinding.LayoutNewLoginSecondStepBinding
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.activities.EsqueciUsuarioAndEstabelecimentoActivity
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.errorNotBooting
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.forgotMyPassword.presentation.ForgotMyPasswordNavigationFlowActivity
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.internaluser.InternalUserActivity
import br.com.mobicare.cielo.lgpd.LgpdActivity
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.login.analytics.LoginAnalytics
import br.com.mobicare.cielo.login.analytics.LoginAnalytics.Companion.CREATE_ACCOUNT
import br.com.mobicare.cielo.login.analytics.LoginAnalytics.Companion.GA_LOGIN_PATH
import br.com.mobicare.cielo.login.analytics.LoginAnalytics.Companion.TOKEN
import br.com.mobicare.cielo.login.firstAccess.presentation.FirstAccessNavigationFlowActivity
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.main.presentation.ui.activities.PASSWORD_EXTRA_PARAM
import br.com.mobicare.cielo.mfa.token.TokenGeneratorActivity
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.updateregister.OnBoardUpdateRegister
import br.com.mobicare.cielo.onboardingWeb.presentation.OnboardingWebActivity
import br.com.mobicare.cielo.precisoDeAjuda.presentation.ui.activities.PrecisoAjudaActivity
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants
import br.com.mobicare.cielo.selfieChallange.constants.SelfieChallengeConstants.FACEID_1X1_REJECT
import br.com.mobicare.cielo.selfieChallange.presentation.SelfieChallengeActivity
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeError
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeParams
import br.com.mobicare.cielo.selfieChallange.utils.SelfieChallengeResult
import br.com.mobicare.cielo.selfieChallange.utils.SelfieErrorEnum
import br.com.mobicare.cielo.selfieChallange.utils.SelfieOperation
import br.com.mobicare.cielo.transparentLogin.presentation.TransparentLoginActivity
import com.akamai.botman.CYFMonitor
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.exceptions.UndeliverableException
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.Serializable
import java.security.GeneralSecurityException

/**
 * Conventional way to perform "manual" user login. Here, the user is required to enter their credentials to log in.
 * An alternative flow called [TransparentLoginActivity] involves facilitating login with parameters, thus bypassing the login screen.
 * */
class NewLoginActivity : AppCompatActivity(), NewLoginContract.View, AllowMeContract.View {

    private lateinit var binding: ActivityNewLoginBinding
    private lateinit var firstStepBinding: LayoutNewLoginFirstStepBinding
    private lateinit var secondStepBinding: LayoutNewLoginSecondStepBinding

    val presenter: NewLoginPresenter by inject {
        parametersOf(this)
    }
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }
    private val useSecurityHash: Boolean? by lazy {
        FeatureTogglePreference.instance.getFeatureTogle(
            FeatureTogglePreference.SECURITY_HASH
        )
    }
    private val loginAnalytics by lazy { LoginAnalytics() }
    private var authFinger: Boolean = false

    private var mIDOnboardingRouter: IDOnboardingRouter? = null
    private lateinit var mAllowMeContextual: AllowMeContextual
    private lateinit var selfieChallengeLauncher: ActivityResultLauncher<Intent>
    private var isForeign: Boolean = false
    private var faceIdToken: String? = null
    private var isSdkAllowMe: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewLoginBinding.inflate(layoutInflater)
        firstStepBinding = LayoutNewLoginFirstStepBinding.bind(binding.root)
        secondStepBinding = LayoutNewLoginSecondStepBinding.bind(binding.root)
        setContentView(binding.root)
        mAllowMeContextual = allowMePresenter.init(this)
        setupMonitor()
        configureViews()
        configureListeners()
        presenter.onCreated()
        SessionExpiredHandler.sessionCalled = false
        getScreenName()
        loginAnalytics.logLoginScreenViewGa(this.javaClass)
        loginAnalytics.logLoginScreenViewAppsFlyer(applicationContext)
        registerSDK()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(application, BuildConfig.HOST_API)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        showErrorIDOnboardingP2()
        mIDOnboardingRouter?.onResume()
        mIDOnboardingRouter?.activity = this
    }

    private fun showErrorIDOnboardingP2() {
        val userPreferences = UserPreferences.getInstance()
        if (userPreferences.isShowErrorIDOnboardingP2) {
            userPreferences.setShowErrorIDOnboardingP2(false)
            bottomSheetGenericFlui(
                image = R.drawable.ic_07,
                title = getString(R.string.id_onboarding_title_bs_error_p2_login),
                subtitle = getString(R.string.id_onboarding_message_error_send_docs),
                nameBtn2Bottom = getString(R.string.entendi),
                statusNameTopBar = false,
                statusBtnClose = false,
                statusView2Line = false,
                txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
                txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
                btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
                btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
                isPhone = false
            ).apply {
                this.onClick =
                    object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                        override fun onBtnSecond(dialog: Dialog) {
                            dialog.dismiss()
                        }
                    }
            }.show(supportFragmentManager, EMPTY)
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
        mIDOnboardingRouter?.onPause()
    }

    override fun render(state: NewLoginState) {
        try {
            binding.apply {
                secondStepBinding.apply {
                    if (isAttached()) {
                        getScreenNameVerification(state.isShowFirstStepLogin)
                        firstStepBinding.firstStepContentLayout.visible(state.isShowFirstStepLogin)
                        secondStepContentLayout.visible(state.isShowSecondStepLogin)
                        frameBackArrowGroup.visible(state.isShowSecondStepLogin)

                        textInputEditNewLogin.apply{
                            visible(state.isShowIdentificatioField)
                            hint  = state.titleIdentification ?: getString(R.string.login_ec_hint)
                            text = state.identification ?: EMPTY

                            configureCardError(state.identificationErrorMessage)

                            isInputEnabled = state.isEnabledIdenticationField
                            state.identificationErrorMessage?.let {
                                announceForAccessibility(it)
                            }
                        }

                        textInputEditLoginUsername.apply {
                            maxLength = state.usernameMaxLength ?: ONE_NEGATIVE
                            text = state.username ?: EMPTY
                            hint = state.titleUsername ?: EMPTY
                            isInputEnabled = state.isEnabledUsernameField
                        }


                        buttonNewLoginUserEnter.loading = state.isLoading
                        handleClickableElementsDuringLoading(state.isLoading.not())

                        checkBoxSaveUserData.text = getString(R.string.login_reminder_user)

                        if (state.isToBeCleanPassword) {
                            textInputEditLoginPassword.text = EMPTY
                        }

                        textInputEditLoginPassword.hint = getString(R.string.hint_senha)

                        val title =
                            state.titleError ?: getString(R.string.text_title_server_generic_error)

                        if (state.messageError?.contains(Text.HTML) == true) {
                            state.messageError = getString(R.string.error_not_booting_login_message)
                        }

                        val message =
                            state.messageError
                                ?: getString(R.string.error_not_booting_login_message)

                        if (state.isNotBooting) {
                            gaSendLoginError(title, message)
                            errorNotBooting(
                                onAction = {
                                    setupMonitor()
                                },
                                message = getString(R.string.error_not_booting_login_message)
                            )
                        } else
                            state.messageError?.let { itMessageError ->
                                gaSendLoginError(title, itMessageError)
                                showAlert(state.titleError ?: EMPTY, itMessageError)
                            }

                        state.passwordInputType?.let { itUserType ->
                            textInputEditLoginPassword.inputType = itUserType
                        }

                        textInputEditLoginPassword.maxLength = state.passwordMaxLength ?: ONE_NEGATIVE

                        if (canAuthenticateWithBiometrics(this@NewLoginActivity) && ((isAndroidVersionOorOMR1() && isFingerprintSequentialID(
                                applicationContext
                            ).not()) || isAndroidVersionOorOMR1().not()) && state.isShowSecondStepLogin &&
                            UserPreferences.getInstance().fingerprintRecorded && validateNewFingerprintAdded(
                                this@NewLoginActivity
                            )
                        ) {
                            enableButtonWhenFingerprintEnabled()
                            presenter.fingerprintIsEnabled(true)
                        }
                    }
                }
            }
        } catch (undeliverableException: UndeliverableException) {
            undeliverableException.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        } catch (castException: ClassCastException) {
            castException.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        } catch (ex: Exception) {
            ex.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
    }

    private fun configureCardError(message: String?) {
        binding.apply {
            val showError = !message.isNullOrEmpty()

            textInputEditNewLogin.apply {
                if (showError) setError() else unsetError()
            }

            cardLoginError.apply {
                cardText = message.orEmpty()
                visibility = if (showError) View.VISIBLE else View.GONE
            }
        }
    }

    private fun getScreenNameVerification(showFirstStepLogin: Boolean) {
        Analytics.trackScreenView(
            screenName = if (showFirstStepLogin) LOGIN_PASSO_1 else LOGIN_PASSO_2,
            screenClass = this.javaClass
        )
    }

    override fun openForgotPasswordView() {
        gaSendLinkEsqueceuSenha()
        startActivity<ForgotMyPasswordNavigationFlowActivity>()
    }

    override fun changeLoginButtonState(isEnabled: Boolean) {
        secondStepBinding.buttonNewLoginUserEnter.isEnabled = isEnabled
    }

    private fun getScreenName(): String {
        return if (firstStepBinding.firstStepContentLayout.visibility == View.VISIBLE) {
            LOGIN_PASSO_1
        } else {
            LOGIN_PASSO_2
        }
    }

    override fun saveRoles(roles: List<String>) {
        val listSet = HashSet<String>(roles)
        UserPreferences.getInstance().saveUserActionPermissions(listSet)
    }

    override fun setUsername(username: String) {
        secondStepBinding.textInputEditLoginUsername.text = username
    }

    override fun setKeepData(isKeep: Boolean) {
        secondStepBinding.checkBoxSaveUserData.apply {
            checkBoxStyle = CheckBoxStyle(borderColorUnchecked = R.color.neutral_600)
            isChecked = isKeep
            visibility = if (isKeep) View.GONE else View.VISIBLE
        }

        secondStepBinding.firstTimeButton.visible(isKeep.not())
    }

    override fun showUserBiometricPrompt() {
        this.createBiometricPromptFragmentActivity(title = getString(R.string.text_fingerprint_prompt_login_title),
            description = getString(R.string.text_fingerprint_prompt_login_description),
            biometricPromptAuthCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    when (errorCode) {

                        in ERROR_HW_UNAVAILABLE..
                                ERROR_LOCKOUT_PERMANENT -> {
                            showFingerprintCaptureError()

                        }

                        in ERROR_NO_BIOMETRICS..
                                ERROR_HW_NOT_PRESENT -> {
                            UserPreferences.getInstance().cleanFingerprintData()
                            showFingerprintCaptureError()
                        }

                        else -> {
                            //enableFingerprintLogin = true
                        }

                    }

                }


                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    try {
                        if (isAndroidVersionOorOMR1()) {
                            saveFingerPrints(applicationContext)
                        }
                        val EMPTY_ASSOCIATED_DATA = ByteArray(EMPTY_ASSOCIATED_DATA_VALUE)
                        val aead = this@NewLoginActivity.createAead(
                            BiometricNotificationBottomSheetFragment.KEY_NAME,
                            BiometricNotificationBottomSheetFragment.MASTER_KEY_URI
                        )

                        UserPreferences.getInstance().fingerprintData?.run {
                            try {
                                val dataDecrypted = aead.decrypt(this, EMPTY_ASSOCIATED_DATA)
                                useSecurityHash?.let { useSecurityHash ->
                                    if (useSecurityHash) {
                                        authFinger = true
                                        allowMePresenter.collect(
                                            mAllowMeContextual = mAllowMeContextual,
                                            this@NewLoginActivity,
                                            mandatory = false
                                        )
                                    } else {
                                        this@NewLoginActivity.presenter.onLoginByFingerprint(
                                            String(
                                                dataDecrypted
                                            ), null
                                        )
                                    }
                                }

                            } catch (ex: GeneralSecurityException) {
                                FirebaseCrashlytics.getInstance()
                                    .log(getString(R.string.security_log, ex.message))
                                UserPreferences.getInstance().cleanFingerprintData()
                            }
                        }
                    } catch (ex: Exception) {
                        FirebaseCrashlytics.getInstance()
                            .log("#BM Não foi possivel recuperar os dados de biometria: ${ex.message}")
                        UserPreferences.getInstance().cleanFingerprintData()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authFinger = false
                }
            })
    }

    override fun setClickCallBiometricPrompt() {
        secondStepBinding.textInputEditLoginPassword.setOnFocusChanged { _, hasFocus ->
            if (canAuthenticateWithBiometrics(this)
                && ((isAndroidVersionOorOMR1() && isFingerprintSequentialID(applicationContext).not())
                        || isAndroidVersionOorOMR1().not())
                && hasFocus
                && UserPreferences.getInstance().fingerprintRecorded
                && validateNewFingerprintAdded(
                    this
                )
            ) {
                showUserBiometricPrompt()
            }
        }
    }

    override fun showTokenButton() {
        secondStepBinding.apply {
            btnToken.visible()
            btnToken.setOnClickListener {
                loginAnalytics.logButtonClickedGa(TOKEN)
                gaButtonToken()
                if (canAuthenticateWithBiometrics(this@NewLoginActivity) && ((isAndroidVersionOorOMR1() && isFingerprintSequentialID(
                        applicationContext
                    ).not()) || isAndroidVersionOorOMR1().not()) &&
                    UserPreferences.getInstance().fingerprintRecorded && validateNewFingerprintAdded(
                        this@NewLoginActivity
                    )
                ) {
                    presenter.callFingerprintToTokenActivity()
                } else {
                    showConfigureFingerprintToTokenActivityError()
                }
            }
        }
    }

    override fun showTokenActivity(username: String) {
        startActivity<TokenGeneratorActivity>(
            TokenGeneratorActivity.USERNAME_KEY to username
        )
    }

    override fun showConfigureFingerprintToTokenActivityError() {
        showMessage(
            "Você precisa cadastrar sua digital para acessar o token",
            "Fingerprint"
        )
    }

    override fun showLGPD(elegible: LgpdElegibilityEntity, password: ByteArray?) {
        startActivity<LgpdActivity>(
            PASSWORD_EXTRA_PARAM to password,
            ARG_PARAM_LGPD_ELEGIBLE to elegible,
            ARG_PARAM_LGPD_START_BY_LOGIN to true
        )
        this.finish()
    }

    override fun showInternalUserScreen(password: ByteArray?) {
        startActivity<InternalUserActivity>(PASSWORD_EXTRA_PARAM to password)
        this.finish()
    }

    override fun showVersionUpdaterDialog(
        isToForceUpdate: Boolean,
        versionTerminateListener: VersionBlockBottomSheetFragment.OnVersionTerminateListener
    ) {
        if (isToForceUpdate || AppHelper.canRedirectToGooglePlay()) {
            VersionBlockBottomSheetFragment().apply {
                this.isToForceUpdate = isToForceUpdate
                this.onVersionTerminateListener = versionTerminateListener
            }.show(
                supportFragmentManager,
                "${NewLoginActivity::class.java.simpleName}#${VersionBlockBottomSheetFragment::class.java.simpleName}"
            )
        } else {
            versionTerminateListener.onContinueTask()
        }
    }

    override fun showUserLostDataWarning() {
        secondStepBinding.buttonNewLoginUserEnter.hideKeyBoard(this)

        CieloContentBottomSheet.create(
            headerConfigurator = HeaderConfigurator(
                title = getString(R.string.text_change_account_title),
                showCloseButton = false
            ),
            contentLayoutRes = R.layout.bottom_sheet_another_account,
            onContentViewCreated = { view, bottomSheet ->
                (bottomSheet.dialog as? BottomSheetDialog)?.apply {
                    behavior.isDraggable = false
                    setCancelable(false)
                }

                BottomSheetAnotherAccountBinding.bind(view).apply {
                    btnCancel.setOnClickListener {
                        tagCancelButtonClick()
                        bottomSheet.dismiss()
                    }

                    btnConfirm.setOnClickListener {
                        tagChangeAccountButtonClick()
                        presenter.onGoToFirstScreenCleanData()
                        authFinger = false
                        UserPreferences.getInstance().cleanFingerprintData()

                        secondStepBinding.btnToken.gone()
                        bottomSheet.dismiss()
                    }
                }
            },
            disableExpandableMode = true
        ).show(supportFragmentManager, EMPTY)
    }

    private fun tagChangeAccountButtonClick() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(getScreenName()),
            label = listOf(
                Label.BOTAO,
                getString(R.string.text_user_another_account_label).toLowerCasePTBR()
            )
        )
    }

    private fun tagCancelButtonClick() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(getScreenName()),
            label = listOf(
                Label.BOTAO,
                getString(R.string.text_cancel_label).toLowerCasePTBR()
            )
        )
    }

    override fun showUpdateRegister() {
        this.startActivity(Intent(this, OnBoardUpdateRegister::class.java))
    }

    override fun enableButtonWhenFingerprintEnabled() {
        secondStepBinding.apply {
            buttonNewLoginUserEnter.isEnabled = true
            buttonNewLoginUserEnter.setOnClickListener {
                hideSoftKeyboard()

                if (UserPreferences.getInstance().fingerprintRecorded
                    && textInputEditLoginPassword.text.isEmpty()
                ) {
                    showUserBiometricPrompt()
                } else {
                    loginButtonClicked()
                }
            }
        }
    }

    override fun onSuccessFeatureToggle(modal: FeatureToggleModal) {
        PriorityWarningModal.create(modal = modal)
            .show(supportFragmentManager, this.getScreenName())
    }

    override fun showPasswordExpired() {
        CieloDialog.create(
            title = getString(R.string.txt_title_dialog_password_expired),
            message = getString(R.string.txt_message_dialog_password_expired)
        )
            .setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setPrimaryButton(getString(R.string.txt_button_dialog_password_expired))
            .setOnPrimaryButtonClickListener {
                openForgotPasswordView()
            }
            .show(supportFragmentManager, EMPTY)
    }

    private fun handleClickableElementsDuringLoading(isEnabled: Boolean) {
        secondStepBinding.apply {
            binding.frameBackArrowGroup.isClickable = isEnabled
            textForgottenUsernameOrPassword.isClickable = isEnabled
            btnToken.isClickable = isEnabled
            firstTimeButton.setClickable(isEnabled)
        }
    }

    override fun setupCreateButtonVisibility(visibility: Boolean) {
        firstStepBinding.buttonCreateAccount.visible(visibility)
    }

    private fun configureViews() {
        firstStepBinding.buttonContinue.isEnabled = false
        fillVersionApp()
        showContentLayout()
        presenter.checkCreateButton()
    }

    private fun configureListeners() {
        binding.apply {
            secondStepBinding.apply {
                buttonNewLoginUserEnter.setOnClickListener {
                    buttonNewLoginUserEnter.hideKeyBoard(this@NewLoginActivity)
                    loginButtonClicked()
                }

                firstTimeButton.setOnClickListener {
                    firstTimeButton.hideKeyBoard(this@NewLoginActivity)
                    showFirstAccessScreen()
                }

                textInputEditLoginUsername.setOnEndIconClickListener {
                    Analytics.trackEvent(
                        category = listOf(Category.APP_CIELO, Action.FORMULARIO),
                        action = listOf(getScreenName()),
                        label = listOf(Label.TOOLTIP, getString(R.string.text_new_login_username)),
                    )
                    showRecuperarEC(
                        R.string.login_title_duvida_user,
                        R.string.login_message_duvida_user,
                        true,
                        getString(R.string.ajuda_esqueci_usuario)
                    )
                }

                firstStepBinding.buttonContinue.setOnClickListener {
                    hideSoftKeyboard()

                    presenter.onFirstStepNextButtonClicked(
                        textInputEditNewLogin.text.trim()
                    )
                }

                frameBackArrowGroup.apply {
                    setOnClickListener {
                        hideKeyBoard(this@NewLoginActivity)

                        Analytics.trackEvent(
                            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
                            action = listOf(getScreenName()),
                            label = listOf(
                                Label.MENSAGEM,
                                Label.INFO,
                                getString(R.string.text_change_user_title)
                            ),
                        )

                        Analytics.trackEvent(
                            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
                            action = listOf(getScreenName()),
                            label = listOf(
                                Label.TOOLTIP,
                                getString(R.string.label_ga_estabelecimento_duvida)
                            ),
                        )

                        presenter.onBackButtonClicked()
                    }
                }

                textForgottenUsernameOrPassword.setOnClickListener {
                    presenter.onForgotPasswordButtonClicked()
                }


                firstStepBinding.buttonCreateAccount.setOnClickListener {
                    loginAnalytics.logClickButton(contentName = CREATE_ACCOUNT, screenName = GA_LOGIN_PATH)

                    startActivity<OnboardingWebActivity>()
                }

                textInputEditLoginUsername.textInputEditText.doAfterTextChanged {
                    presenter.onUsernameChanged(it.toString())
                }

                textInputEditLoginPassword.setOnImageClicked {
                    loginAnalytics.logLoginHidePassword(
                        textInputEditLoginPassword.showPasswordToggle.not(),
                        this.javaClass
                    )
                }

                textInputEditLoginPassword.textInputEditText.doAfterTextChanged {
                    if (it.toString().length > FIVE)
                        presenter.onPasswordChanged(it.toString())
                }

                handleEcEmailCpfInputValidation()
            }
        }
    }

    private fun logButtonClickedOnAppsFlyer() {
        AppsFlyerUtil.send(
            this@NewLoginActivity,
            event = EVENT_ENTERBUTTON,
            obj = Pair(AF_SCREEN_NAME, EVENT_LOGIN)
        )
    }

    private fun handleEcEmailCpfInputValidation() {
        binding.textInputEditNewLogin.apply {
            setCpfEcEmailMask {
                var isValid = false
                var errorMessage = EMPTY

                when {
                    text.contains(AT_SIGN).not() && text.removeNonNumbers().containsOnlyNumbers() && text.length >= ELEVEN -> {
                        isValid = ValidationUtils.isCPF(text)
                        errorMessage = if (isValid) EMPTY else INVALID_CPF
                    }

                    text.contains(AT_SIGN) -> {
                        isValid = ValidationUtils.isEmail(text)
                        errorMessage = if (isValid) EMPTY else INVALID_EMAIL
                    }

                    else -> {
                        errorMessage = EMPTY
                        isValid = true
                    }
                }

                configureCardError(errorMessage)
                firstStepBinding.buttonContinue.isEnabled = isValid && text.isNotBlank()
            }

            setOnFocusChangeListener { _, focused ->
                if (focused.not()) {
                    if (this.text.isNotBlank() && this.text.trim() != getString(R.string.login_ec_hint)) {
                        gaSendFormInteraction(LOGIN_INTERACAO_1, LOGIN_LABEL_EMAIL)
                    }
                }
            }
        }
    }

    private fun loginButtonClicked() {
        logButtonClickedOnAppsFlyer()

        secondStepBinding.apply {
            if (textInputEditLoginPassword.text.trim().isNotEmpty() &&
                textInputEditLoginPassword.text.trim() != getString(R.string.login_password_hint)
            ) {
                gaSendFormInteraction(LOGIN_INTERACAO_2, LOGIN_LABEL_SENHA)
            }
            useSecurityHash?.let { useSecurityHash ->
                if (useSecurityHash)
                    allowMePresenter.collect(
                        mAllowMeContextual = mAllowMeContextual,
                        this@NewLoginActivity,
                        mandatory = false
                    )
                else performLogin(null)
            }
        }
    }

    private fun performLogin(allowMeToken: String?) {
        binding.apply {
            secondStepBinding.apply {
                presenter.onLoginButtonClicked(
                    textInputEditNewLogin.text,
                    textInputEditLoginUsername.text,
                    textInputEditLoginPassword.text,
                    checkBoxSaveUserData.isChecked,
                    allowMeToken
                )
            }
        }
    }

    private fun gaButtonToken() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, MFA_NOVO_TOKEN),
            action = listOf(Action.CLIQUE, MFA_NOVO_TOKEN, MFA_NOVO_TOKEN_LOGIN),
            label = listOf(MFA_VISUALIZAR_TOKEN)
        )
    }

    private fun gaSendLinkEsqueceuSenha() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "login/esqueceu-usuario-senha"),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.LINK, "Esqueceu usuário ou senha")
        )
    }

    private fun gaSendLoginError(title: String, message: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, getScreenName()),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.MENSAGEM, title, message)
        )
    }

    private fun showAlert(title: String, message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(title)
            .message(message)
            .closeTextButton("OK")
            .build().let {
                it.showAllowingStateLoss(
                    this.supportFragmentManager,
                    getString(R.string.text_cieloalertdialog)
                )
            }
    }

    private fun showFingerprintCaptureError() {
        this.showMessage(
            message = getString(R.string.text_fingerprint_prompt_error_message),
            title = getString(R.string.text_fingerprint_prompt_error_title)
        ) {
            this.setBtnRight(getString(R.string.ok))
            this.setOnclickListenerRight {
                secondStepBinding.textInputEditLoginPassword.hasFocus()
                changeLoginButtonState(false)
            }
        }
    }

    override fun callMainLoggedScreen(password: ByteArray?) {
        UserPreferences.getInstance().saveCalledBiometricNotificationByLogin(true)
        startActivity<MainBottomNavigationActivity>(PASSWORD_EXTRA_PARAM to password)
        this.finish()
    }

    override fun showIdOnboarding() {
        binding.apply {
            mIDOnboardingRouter = IDOnboardingRouter(
                activity = this@NewLoginActivity,
                showLoadingCallback = {
                    frameNewLoginProgress.visible()
                    contentLayout.gone()
                },
                hideLoadingCallback = {
                    frameNewLoginProgress.gone()
                    contentLayout.visible()
                },
                isLogin = true
            ).showOnboarding()
        }
    }

    private fun showRecuperarEC(
        titleId: Int, messageId: Int, esqueciSenha: Boolean,
        buttonTitle: String
    ) {
        //GA
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Action.FORMULARIO),
            action = listOf(getScreenName()),
            label = listOf(Label.MENSAGEM, Label.INFO, getString(titleId)),
        )
        CieloAskQuestionDialogFragment
            .Builder()
            .title(getString(titleId))
            .message(getString(messageId))
            .positiveTextButton(getString(R.string.login_recuperar_duvida_ec))
            .cancelTextButton(getString(R.string.ok))
            .onCancelButtonClickListener {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Action.FORMULARIO),
                    action = listOf(getScreenName()),
                    label = listOf(
                        Label.BOTAO,
                        "${getString(titleId)} ${getString(R.string.login_recuperar_duvida_ok)}".toLowerCasePTBR()
                    )
                )
            }
            .onPositiveButtonClickListener {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, Action.FORMULARIO),
                    action = listOf(getScreenName()),
                    label = listOf(
                        Label.BOTAO,
                        "${getString(titleId)} ${getString(R.string.login_recuperar_duvida_ec)}".toLowerCasePTBR()
                    )
                )
                if (esqueciSenha) {
                    val usuario = binding.textInputEditNewLogin.text
                    startActivity<EsqueciUsuarioAndEstabelecimentoActivity>(
                        PrecisoAjudaActivity.SCREEN_NAME to getScreenName(),
                        PrecisoAjudaActivity.TOOLBAR_TITLE to buttonTitle,
                        PrecisoAjudaActivity.FIELD_EC to usuario,
                        PrecisoAjudaActivity.FIELD_USUARIO to usuario,
                        PrecisoAjudaActivity.BACK_TO to getScreenName()
                    )
                } else {
                    startActivity<PrecisoAjudaActivity>(
                        PrecisoAjudaActivity.ESTABLISHMENT to false,
                        PrecisoAjudaActivity.TOOLBAR_TITLE to buttonTitle,
                        PrecisoAjudaActivity.SCREEN_NAME to getScreenName()
                    )
                }
            }
            .build()
            .showAllowingStateLoss(
                this.supportFragmentManager,
                "CieloAskQuestionDialogFragment"
            )
    }

    private fun showFirstAccessScreen() {
        loginAnalytics.logFirstAccessButton()
        loginAnalytics.logLoginFirstAccessButtonGa()

        startActivity<FirstAccessNavigationFlowActivity>()
    }

    private fun showContentLayout() {
        binding.apply {
            Handler().postDelayed({
                contentLayout.layoutParams?.let { itLayoutParam ->
                    contentLayout.translationY =
                        contentLayout.measuredHeight.toFloat()
                    val va = ValueAnimator.ofInt(contentLayout.measuredHeight, ZERO)
                    va.duration = SIX_HUNDRED
                    va.interpolator = DecelerateInterpolator()
                    va.addUpdateListener { animation ->
                        contentLayout.translationY =
                            (animation.animatedValue as Int).toFloat()
                    }
                    va.start()
                }
            }, EIGHT_HUNDRED)
        }
    }

    private fun fillVersionApp() {
        firstStepBinding.tvVersionApp.text =
            "Versão ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    private fun gaSendFormInteraction(labelButton: String, interacao: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, labelButton),
            action = listOf(Action.FORMULARIO),
            label = listOf(Action.INTERACAO, interacao)
        )
    }

    override fun successCollectToken(result: String) {
        if (isSdkAllowMe) {
            doWhenResumed {
                presenter.registerDevice(faceIdToken, result)
                isSdkAllowMe = false
            }
        } else {
            val passworDecrypt: String? = decryptPass()
            if (authFinger) {
                passworDecrypt?.let {
                    this@NewLoginActivity.presenter.onLoginByFingerprint(it, result) }
            } else {
                performLogin(result)
            }
        }
    }

    private fun decryptPass(): String? {
        val EMPTY_ASSOCIATED_DATA = ByteArray(EMPTY_ASSOCIATED_DATA_VALUE)
        val aead = this@NewLoginActivity.createAead(
            BiometricNotificationBottomSheetFragment.KEY_NAME,
            BiometricNotificationBottomSheetFragment.MASTER_KEY_URI
        )
        var passworDecrypt: String? = null
        UserPreferences.getInstance().fingerprintData?.run {
            try {
                val dataDecrypted = aead.decrypt(this, EMPTY_ASSOCIATED_DATA)
                passworDecrypt = String(
                    dataDecrypted
                )
                if (passworDecrypt.isNullOrEmpty()) {
                    authFinger = false
                    UserPreferences.getInstance().cleanFingerprintData()
                }
            } catch (ex: GeneralSecurityException) {
                FirebaseCrashlytics.getInstance()
                    .log(getString(R.string.security_log, ex.message))
                UserPreferences.getInstance().cleanFingerprintData()
            }
        }
        return passworDecrypt
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        if (isSdkAllowMe){
            doWhenResumed {
                showSdkError(ONE)
                isSdkAllowMe = false
            }
        } else {
            if (mandatory) {
                showAlert(getString(R.string.text_title_error_fingerprint_allowme), errorMessage)
            } else {
                FirebaseCrashlytics.getInstance().log(errorMessage)
                val passworDecrypt: String? = decryptPass()
                if (authFinger) {
                    passworDecrypt?.let {
                        this@NewLoginActivity.presenter.onLoginByFingerprint(
                            it, null
                        )
                    }
                } else {
                    performLogin(result)
                }
            }
        }
    }

    override fun showInvite(invite: PendingInviteItem, onboardingRequired: Boolean) {
        startActivity<LegacyUserInviteReceiveNavigationActivity>(
            INVITE_ARGS to invite,
            ONBOARDING_REQUIRED_ARGS to onboardingRequired
        )
    }

    override fun isAttached(): Boolean {
        return true
    }

    companion object {
        private const val EMPTY_ASSOCIATED_DATA_VALUE = 0
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        allowMePresenter.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return this.supportFragmentManager
    }

    override fun showNewDeviceDetected(foreign: Boolean) {
        isForeign = foreign
        showCustomBottomSheet(
            image = R.drawable.img_10_erro,
            title = getString(R.string.new_device_detected_bs_title),
            subtitle = getString(R.string.new_device_detected_bs_subtitle),
            bt1Title = getString(R.string.new_device_detected_bs_button_try_later),
            bt2Title = getString(R.string.continuar),
            btCloseVisibility = false,
            isCancelable = false,
            bt1Callback = {
                presenter.resetNewDevice()
                false
            },
            bt2Callback = {
                openSelfieChallenge(foreign)
                false
            },
            closeCallback = {
                presenter.resetNewDevice()
            }
        )
    }

    private fun registerSDK() {
        selfieChallengeLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    it.data?.let { intent ->
                        selfieChallengeSuccess(intent)?.let { selfieChallengeResult ->
                            faceIdToken = selfieChallengeResult.faceIdToken
                            isSdkAllowMe = true
                            allowMePresenter.collect(
                                mAllowMeContextual = mAllowMeContextual,
                                this@NewLoginActivity,
                                mandatory = false
                            )
                        }
                    }
                }

                else -> {
                    it.data?.let { intent ->
                        selfieChallengeError(intent)?.let { selfieError ->
                            when (selfieError.type) {
                                SelfieErrorEnum.CAMERA_CLOSED_MANUALLY -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.new_login_bs_close_sdk_title),
                                        subtitle  = getString(R.string.new_login_bs_close_sdk_message),
                                        bt1Title = getString(R.string.new_device_detected_bs_button_try_later),
                                        bt2Title = getString(R.string.new_login_bs_close_sdk_button2),
                                        bt2Callback = {
                                            openSelfieChallenge(isForeign)
                                            false
                                        },
                                        bt1Callback = {
                                            presenter.resetNewDevice()
                                            false
                                        },
                                        closeCallback = {
                                            presenter.resetNewDevice()
                                        }
                                    )
                                }

                                SelfieErrorEnum.SDK_SELFIE_CHALLENGE_GENERIC_ERROR -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.new_login_bs_generic_error_sdk_title),
                                        subtitle = getString(R.string.new_login_bs_generic_error_sdk_message),
                                        bt2Title = getString(R.string.new_login_bs_generic_error_sdk_button),
                                        bt2Callback = {
                                            openSelfieChallenge(isForeign)
                                            false
                                        }
                                    )
                                }

                                SelfieErrorEnum.USER_DENIED_CAMERA_PERMISSION -> {
                                    showCustomBottomSheet(
                                        image = R.drawable.img_10_erro,
                                        title = getString(R.string.new_login_bs_permission_error_sdk_title),
                                        subtitle = getString(R.string.new_login_bs_permission_error_sdk_message),
                                        bt2Title = getString(R.string.entendi),
                                        bt2Callback = {
                                            presenter.resetNewDevice()
                                            false
                                        }
                                    )
                                }

                                SelfieErrorEnum.SEND_SELFIE_ERROR -> {
                                    if (selfieError.errorCode == FACEID_1X1_REJECT){
                                        showCustomBottomSheet(
                                            image = R.drawable.img_selfie_correta,
                                            title = getString(R.string.new_login_bs_selfie_rejected_title),
                                            subtitle = getString(R.string.new_login_bs_selfie_rejected_message),
                                            bt2Title = getString(R.string.new_login_bs_selfie_rejected_button),
                                            bt2Callback = {
                                                presenter.resetNewDevice()
                                                false
                                            }
                                        )
                                    } else {
                                        showCustomBottomSheet(
                                            image = R.drawable.img_10_erro,
                                            title = getString(R.string.new_login_bs_generic_api_error_sdk_title),
                                            subtitle = getString(R.string.new_login_bs_generic_api_error_sdk_message),
                                            bt2Title = getString(R.string.entendi),
                                            bt2Callback = {
                                                presenter.resetNewDevice()
                                                false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openSelfieChallenge(foreign: Boolean) {
        val intent = Intent(this, SelfieChallengeActivity::class.java).apply {
            val selfieChallengeParams = SelfieChallengeParams(
                isForeign = foreign,
                operation = SelfieOperation.NEW_DEVICE
            )
            putExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_PARAMS,
                selfieChallengeParams as Serializable
            )
        }
        selfieChallengeLauncher.launch(intent)
    }

    private fun selfieChallengeSuccess(intent: Intent): SelfieChallengeResult? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS,
                SelfieChallengeResult::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_SUCCESS) as SelfieChallengeResult?
        }
    }

    private fun selfieChallengeError(intent: Intent): SelfieChallengeError? {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            intent.getSerializableExtra(
                SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR,
                SelfieChallengeError::class.java
            )
        } else {
            intent.getSerializableExtra(SelfieChallengeConstants.SELFIE_CHALLENGE_ERROR) as SelfieChallengeError?
        }
    }

    override fun successRegisterDevice(loginResponse: LoginResponse) {
        showCustomBottomSheet(
            image = R.drawable.img_10_erro,
            title = getString(R.string.new_device_detected_bs_success_title),
            subtitle = getString(R.string.new_device_detected_bs_success_message),
            bt2Title = getString(R.string.entendi),
            btCloseVisibility = false,
            isCancelable = false,
            bt2Callback = {
                continueLogin(loginResponse)
                false
            },
            closeCallback = {
                continueLogin(loginResponse)
            }
        )
    }

    private fun continueLogin(loginResponse: LoginResponse) {
        binding.apply {
            secondStepBinding.apply {
                presenter.continueLogin(
                    loginResponse,
                    textInputEditNewLogin.text,
                    textInputEditLoginUsername.text,
                    textInputEditLoginPassword.text,
                    checkBoxSaveUserData.isChecked,
                )
            }
        }
    }

    override fun showSdkError(id: Int) {

        val title = getString(
            when(id) {
                ONE -> R.string.new_device_allow_me_error_title_1
                TWO -> R.string.new_device_allow_me_error_title_2
                THREE -> R.string.new_device_allow_me_error_title_3
                else -> R.string.new_device_allow_me_error_title_1
            }
        )

        showCustomBottomSheet(
            image = R.drawable.img_10_erro,
            title = title,
            subtitle = getString(R.string.new_device_allow_me_error_subtitle).fromHtml().toString(),
            bt2Title = getString(R.string.entendi),
            isCancelable = false,
            bt2Callback = {
                presenter.resetNewDevice()
                false
            },
            closeCallback = {
                presenter.resetNewDevice()
            }
        )
    }

    private fun showCustomBottomSheet(
        @DrawableRes image: Int? = null,
        title: String? = null,
        subtitle: String? = null,
        bt1Title: String? = null,
        bt2Title: String? = null,
        btCloseVisibility: Boolean = true,
        isFullScreen: Boolean = false,
        isCancelable: Boolean = true,
        bt1Callback: (() -> Boolean)? = null,
        bt2Callback: (() -> Boolean)? = null,
        closeCallback: (() -> Unit)? = null) {
        bottomSheetGenericFlui(
            image = image ?: R.drawable.ic_generic_error_image,
            title = title ?: getString(R.string.generic_error_title),
            subtitle = subtitle ?: getString(R.string.error_generic),
            nameBtn1Bottom = bt1Title ?: EMPTY,
            nameBtn2Bottom = bt2Title ?: getString(R.string.ok),
            statusBtnClose = btCloseVisibility,
            isFullScreen = isFullScreen,
            isCancelable = isCancelable,
            statusBtnFirst = bt1Title != null,
            isPhone = false,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLACK,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE
        ).apply {
            onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                    override fun onBtnFirst(dialog: Dialog) {
                        if (bt1Callback?.invoke() != true) dismiss()
                    }

                    override fun onBtnSecond(dialog: Dialog) {
                        if (bt2Callback?.invoke() != true) dismiss()
                    }

                    override fun onSwipeClosed() {
                        closeCallback?.invoke()
                    }

                    override fun onCancel() {
                        closeCallback?.invoke()
                    }
                }
        }.show(
            supportFragmentManager,
            getString(R.string.bottom_sheet_generic)
        )
    }
}