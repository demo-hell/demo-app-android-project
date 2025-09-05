package br.com.mobicare.cielo.idOnboarding.router

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointUserCnpj.*
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class IDOnboardingRouterPresenter(
    private val repository: IDOnboardingRepository,
    private val view: IDOnboardingRouterContract.View,
    private val featureTogglePreference: FeatureTogglePreference
) : IDOnboardingRouterContract.Presenter {

    private var retryCallback: (() -> Unit)? = { getIdOnboardingStatus() }
    private var disposable = CompositeDisposable()

    fun canShowIdOnboarding(): Boolean {
        val featureToggle = featureTogglePreference.getFeatureToggleObject(
            FeatureTogglePreference.ID_ONBOARDING
        )
        return featureToggle?.show ?: false
    }

    override fun getIdOnboardingStatus() {
        view.showLoading()
        retryCallback = { getIdOnboardingStatus() }
        repository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .doFinally { view.hideLoading() }
            .subscribe({ onboardingStatus ->
                userStatus.onboardingStatus = onboardingStatus
                setOnboardingRoute(onboardingStatus)
            }, {
                view.showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
    }

    private fun setIdOnboardingStarted() {
        view.showLoading()
        retryCallback = { setIdOnboardingStarted() }
        repository.setIdOnboardingStarted()
            .configureIoAndMainThread()
            .doFinally { view.hideLoading() }
            .subscribe({ onboardingStatus ->
                userStatus.onboardingStatus = onboardingStatus
                setOnboardingRoute(onboardingStatus)
            }, {
                val error = ErrorMessage.fromThrowable(it)
                if (error.errorCode == ONBOARDING_NOT_ELIGIBLE) {
                    it.printStackTrace()
                    Firebase.crashlytics.recordException(it)
                } else {
                    view.showError(error)
                }
            }).addTo(disposable)
    }

    private fun setOnboardingRoute(onboardingStatus: IDOnboardingStatusResponse?) {
        when (IDOCheckpointUserCnpj.fromCode(onboardingStatus?.onboardingCheckpointCode)) {
            NONE ->
                if (onboardingStatus?.validationStartedOn == null)
                    setIdOnboardingStarted()
                else
                    view.showUpdateUserDataDialog()

            P1_VALIDATED -> view.showUserWithoutRole()
            USER_CNPJ_CHECKED -> view.showP2PicturesStart()
            P2_VALIDATED -> view.goToHome()
        }
    }

    fun updateStatusAndThen(callback: () -> Unit) {
        repository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .doFinally {
                callback.invoke()
            }
            .subscribe({ onboardingStatus ->
                userStatus.onboardingStatus = onboardingStatus
            }, {
                it.printStackTrace()
            }).addTo(disposable)
    }

    override fun retry() {
        retryCallback?.invoke()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    companion object {
        const val ONBOARDING_NOT_ELIGIBLE = "ONBOARDING_NOT_ELIGIBLE"
    }
}