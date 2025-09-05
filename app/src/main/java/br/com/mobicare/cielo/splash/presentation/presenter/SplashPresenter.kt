package br.com.mobicare.cielo.splash.presentation.presenter

import android.content.Intent
import android.net.Uri
import android.os.Handler
import br.com.mobicare.cielo.commons.constants.Deeplink.DEEPLINK_PATH
import br.com.mobicare.cielo.commons.constants.Deeplink.MAIL_URL_PARAM_TOKEN
import br.com.mobicare.cielo.commons.constants.HolderDeeplink.OPEN_FINANCE_CONCLUSION_SHARE_PATH
import br.com.mobicare.cielo.commons.constants.HolderDeeplink.OPEN_FINANCE_FLOW_PAYMENT
import br.com.mobicare.cielo.commons.constants.HolderDeeplink.OPEN_FINANCE_SCOPE_OPEN_ID_VALIDATION
import br.com.mobicare.cielo.commons.constants.MainConstants.TOKEN_MAIL_VALIDATION_NAME
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.getInstance
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.firstAccessOnboarding.FirstInstallOnboardingActivity
import br.com.mobicare.cielo.newLogin.NewLoginActivity
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.CODE
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.ERROR_DESCRIPTION
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.ID_TOKEN
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.INTENT_ID
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.REDIRECT_URI
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.STATE
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.splash.data.managers.SplashRepository
import br.com.mobicare.cielo.splash.presentation.ui.SplashContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.Calendar

class SplashPresenter(
    private val mView: SplashContract.View,
    private val repository: SplashRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val configurationPreference: ConfigurationPreference,
    private val featureTogglePreference: FeatureTogglePreference,
) :
    SplashContract.Presenter {
    private var firstTime: Long = ZERO.toLong()

    private var disposable = CompositeDisposable()

    override fun callAPI() {
        firstTime = Calendar.getInstance().timeInMillis
        disposable.add(
            repository.getConfig()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe { mView.showProgress() }
                .doFinally { mView.hideProgress() }
                .subscribe({ response ->
                    configurationPreference.saveConfig(response)
                },
                    {
                        val error = APIUtils.convertToErro(it)
                        mView.showError(error.message)
                        callNextActivity()
                    })
        )
    }

    override fun callNextActivity() {
        val diff = Calendar.getInstance().timeInMillis - firstTime
        if (diff < NEXT_ACTIVITY_DELAY_MILLIS) {
            Handler().postDelayed({ managerNextActivity() }, NEXT_ACTIVITY_DELAY_MILLIS - diff)
        } else {
            managerNextActivity()
        }
    }

    /**
     * Verificar qual a próxima tela
     */
    private fun managerNextActivity() {
        val firstUse = getInstance().firstUse
        val hasSessionValid = getInstance().isAuthenticated
        val requestIdOPFValid = getInstance().requestIdOPF.isNullOrEmpty().not()
        when {
            firstUse -> mView.changeActivity(FirstInstallOnboardingActivity::class.java)
            hasSessionValid && requestIdOPFValid -> mView.changeActivityToFlowOPF()
            else -> mView.changeActivity(NewLoginActivity::class.java)
        }
    }

    override fun checkDeepLink(intent: Intent?, openDeepLink: Boolean?): Boolean {
        if (intent != null) {
            val dataUri = intent.data
            val actionName = intent.action
            if (actionName != null && dataUri != null) {
                Timber.d(actionName)
                Timber.d(dataUri.toString())
                val mailUrlParam = dataUri.query
                if (isAnEmailDeeplink(actionName, mailUrlParam)) {
                    val token: String =
                        mailUrlParam?.split(MAIL_URL_PARAM_TOKEN)?.toTypedArray()?.get(ONE)
                            .toString()
                    if (openDeepLink == true) mView.startDeepLinkActivity(token)
                    return true
                } else {
                    checkDeeplinkUrl(dataUri)
                }
            }
        }
        return false
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
    private fun saveParamsOpenFinanceDeeplink(dataUri: Uri) {
        dataUri.getQueryParameter(INTENT_ID)
            ?.let { intentId -> getInstance().saveHolderIntentId(intentId) }
        dataUri.getQueryParameter(REDIRECT_URI)
            ?.let { redirectUri -> getInstance().saveHolderRedirectUri(redirectUri) }
    }

    private fun isAnEmailDeeplink(actionName: String, mailUrlParam: String?): Boolean {
        return actionName == Intent.ACTION_VIEW &&
                mailUrlParam != null &&
                mailUrlParam.contains(TOKEN_MAIL_VALIDATION_NAME)
    }

    private fun checkDeeplinkUrl(dataUri: Uri) {
        when {
            isDeeplinkPath(dataUri) -> {
                val deepLink = DeepLinkModel.generateDeepLinkModel(dataUri)
                deepLink?.let { saveDeeplinkModel(it) }
            }
            isOpenFinanceScopeAndFlow(dataUri) -> {
                handleOpenFinanceDeeplink(dataUri)
            }
            isOpenFinanceConclusionShare(dataUri) -> {
                saveParamsToFlowConclusionShareOPF(dataUri)
            }
            else -> {
                getInstance().saveMktExternalDeeplink(dataUri.toString())
            }
        }
    }

    private fun isDeeplinkPath(dataUri: Uri): Boolean {
        return dataUri.path == DEEPLINK_PATH
    }

    private fun isOpenFinanceScopeAndFlow(dataUri: Uri): Boolean {
        return dataUri.query?.contains(OPEN_FINANCE_SCOPE_OPEN_ID_VALIDATION) == true &&
                dataUri.query?.contains(OPEN_FINANCE_FLOW_PAYMENT) == true
    }

    private fun handleOpenFinanceDeeplink(dataUri: Uri) {
        if (featureTogglePreference.isActivate(FeatureTogglePreference.OPEN_FINANCE_DEEPLINK_DETAINER)) {
            saveParamsOpenFinanceDeeplink(dataUri)
        }
    }

    private fun saveDeeplinkModel(model: DeepLinkModel) {
        getInstance().saveDeepLinkModel(model)
    }

    private fun isOpenFinanceConclusionShare(dataUri: Uri): Boolean {
        return dataUri.path?.contains(OPEN_FINANCE_CONCLUSION_SHARE_PATH) == true
    }

    private fun saveParamsToFlowConclusionShareOPF(dataUri: Uri){
        val uriTransform = Uri.parse(dataUri.toString().replace("#","?"))
        uriTransform.getQueryParameter(STATE)?.let { state -> getInstance().saveRequestIdOPF(state) }
        uriTransform.getQueryParameter(ID_TOKEN)?.let { idToken -> getInstance().saveIdTokenOPF(idToken) }
        uriTransform.getQueryParameter(CODE)?.let { code ->
            getInstance().saveAuthorizationCodeOPF(code)
        } ?: uriTransform.getQueryParameter(ERROR_DESCRIPTION)?.let { error ->
            getInstance().saveErrorDescriptionOPF(error)
        }
    }

    companion object {
        private const val NEXT_ACTIVITY_DELAY_MILLIS = 5000
    }
}