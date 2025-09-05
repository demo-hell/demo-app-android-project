package br.com.mobicare.cielo.transparentLogin.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.databinding.ActivityTransparentLoginBinding
import br.com.mobicare.cielo.extensions.goToAndFinishCurrentActivity
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4.Companion.TRANSPARENT_LOGIN_ERROR
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4.Companion.TRANSPARENT_LOGIN_ERROR_ACTION
import br.com.mobicare.cielo.transparentLogin.analytics.TransparentLoginGA4.Companion.UNAVAILABLE_SERVICE
import br.com.mobicare.cielo.transparentLogin.utils.CPF_PARAM
import br.com.mobicare.cielo.transparentLogin.utils.EC_PARAM
import br.com.mobicare.cielo.transparentLogin.utils.PASSWORD_PARAM
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.ManualLogin
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.Success
import br.com.mobicare.cielo.transparentLogin.utils.TransparentLoginUiState.TransparentLogin
import com.akamai.botman.CYFMonitor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class TransparentLoginActivity : AppCompatActivity(), AllowMeContract.View {
    private var binding: ActivityTransparentLoginBinding? = null
    private val viewModel: TransparentLoginViewModel by viewModel()
    private val transparentLoginGA4: TransparentLoginGA4 by inject()
    private lateinit var allowMeContextual: AllowMeContextual
    private val datadogEvent: DatadogEvent by inject()
    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val cpf: String?
        get() = intent?.getStringExtra(CPF_PARAM)

    private val password: String?
        get() = intent?.getStringExtra(PASSWORD_PARAM)

    private val ec: String?
        get() = intent?.getStringExtra(EC_PARAM)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:Login transparente inicio do fluxo",
            key = "onCreate",
            value = "onCreate iniciado"
        )
        binding = ActivityTransparentLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        SessionExpiredHandler.sessionCalled = false
        allowMeContextual = allowMePresenter.init(this)

        checkFeatureToggle()
        showLoading()
        setupMonitor()
        setupListener()
        setupObserver()
    }

    private fun logLoadingScreenView() {
        transparentLoginGA4.logScreenView(TransparentLoginGA4.TRANSPARENT_LOGIN_LOADING)
    }

    private fun logErrorScreenView() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: logErrorScreenView",
            key = "logErrorScreenView",
            value = "logErrorScreenView iniciado"
        )
        transparentLoginGA4.logScreenView(TRANSPARENT_LOGIN_ERROR)
    }

    private fun checkFeatureToggle() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:Login transparente checkFeatureToggle",
            key = "checkFeatureToggle",
            value = "checkFeatureToggle iniciado"
        )
        viewModel.checkFeatureToggle()
    }

    private fun showLoading() {
        binding?.loading?.startAnimation(
            message = R.string.loading_message,
            false
        )
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(application, BuildConfig.HOST_API)
    }

    private fun setupListener() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:Login transparente setupListener",
            key = "setupListener",
            value = "setupListener iniciado"
        )
        binding?.error?.btFinish?.setOnClickListener {
            logButtonClick()
            goToAndFinishCurrentActivity<SplashActivity>()
        }
    }

    private fun logButtonClick() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:Login transparente logButtonClick",
            key = "logButtonClick",
            value = "logButtonClick iniciado"
        )
        transparentLoginGA4.logButtonClick(screenName = TRANSPARENT_LOGIN_ERROR, contentName = TRANSPARENT_LOGIN_ERROR_ACTION)
    }

    private fun setupObserver() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento:Login transparente setupObserver",
            key = "setupObserver",
            value = "setupObserver iniciado"
        )
        viewModel.loginState.observe(this@TransparentLoginActivity) { state ->
            datadogEvent.LoggerInfo(
                message = "Novo Credenciamento: Início da observação do estado de login transparente",
                key = "LoginStateObservation",
                value = "Iniciado"
            )
            when (state) {
                is Success -> {
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Login transparente: Sucesso",
                        key = "LoginState",
                        value = "Successo  no login"
                    )
                    logLoadingScreenView()
                    goToAndFinishCurrentActivity<MainBottomNavigationActivity>()
                }
                is ManualLogin -> {
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Login transparente: ManualLogin",
                        key = "LoginState",
                        value = "ManualLogin"
                    )
                    goToAndFinishCurrentActivity<SplashActivity>()
                }
                is TransparentLogin -> {
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Login transparente: TransparentLogin",
                        key = "LoginState",
                        value = "TransparentLogin"
                    )
                    collectFingerprint()
                }
                is Error -> {
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Login transparente: Erro - ${state.message}",
                        key = "LoginState",
                        value = "Error"
                    )
                    showError(state.message)
                }
                else -> {
                    datadogEvent.LoggerInfo(
                        message = "Novo Credenciamento:Login transparente: Estado desconhecido",
                        key = "LoginState",
                        value = "Unknown"
                    )
                    showError()
                }
            }
        }
    }

    private fun collectFingerprint() {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: collectFingerprint",
            key = "collectFingerprint",
            value = "collectFingerprint iniciado"
        )
        allowMePresenter.collect(
            mAllowMeContextual = allowMeContextual,
            context = this,
            mandatory = false,
            askLocalizationPermission = false
        )
    }

    private fun showError(errorMessage: String? = null) {
        binding?.apply {
            error.root.visible()
            loading.gone()
        }

        logErrorScreenView()
        logExceptionError(errorMessage)
    }

    private fun logExceptionError(errorMessage: String?) {
        transparentLoginGA4.logException(
            screenPath = TRANSPARENT_LOGIN_ERROR,
            errorDescription = errorMessage ?: UNAVAILABLE_SERVICE
        )
    }

    override fun successCollectToken(result: String) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: successCollectTokenAllowme",
            key = "successCollectToken",
            value = "successCollectToken $result"
        )
        login(result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: errorCollectTokenAllowme",
            key = "errorCollectToken",
            value = "errorCollectToken $result, errorMessage $errorMessage, mandatory $mandatory"
        )
        login(result)
    }

    private fun login(fingerprint: String?) {
        datadogEvent.LoggerInfo(
            message = "Novo Credenciamento Login transparente: Iniciando Login Nativo",
            key = "loginTransparente",
            value = "Login, cpf: $cpf, ec: $ec, fingerprint: $fingerprint"
        )
        viewModel.login(
            LoginRequest(
                username = cpf,
                password = password,
                merchant = ec,
                fingerprint = fingerprint
            )
        )
    }

    override fun getSupportFragmentManagerInstance() = this.supportFragmentManager

    override fun isAttached() = true

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}