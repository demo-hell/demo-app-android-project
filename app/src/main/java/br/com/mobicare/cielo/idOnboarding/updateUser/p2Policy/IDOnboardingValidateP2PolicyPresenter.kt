package br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy

import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class IDOnboardingValidateP2PolicyPresenter(
    val repository: IDOnboardingRepository,
    val view: IDOnboardingValidateP2PolicyContract.View,
    val userPreferences: UserPreferences,
    private val mfaUserInformation: MfaUserInformation
) : IDOnboardingValidateP2PolicyContract.Presenter {

    private var retryCallback: (() -> Unit)? = null
    private var disposable = CompositeDisposable()

    fun sendAllowme(fingerprint: String) {
        view.showLoading()

        retryCallback = { sendAllowme(fingerprint) }

        repository.sendAllowme(fingerprint)
            .configureIoAndMainThread()
            .subscribe({
                IDOnboardingFlowHandler.userStatus.onboardingStatus = it
                view.onAllowMeDone()
            }, {
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
    }

    fun validateP2Policy() {
        view.showLoading()

        retryCallback = { validateP2Policy() }

        repository.validateP2Policy()
            .configureIoAndMainThread()
            .subscribe({
                IDOnboardingFlowHandler.userStatus.onboardingStatus = it
                refreshToken(false)
            }, {
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
    }

    private fun refreshToken(isShowLoading: Boolean = true) {
        retryCallback = { refreshToken() }

        repository.refreshToken(
            userPreferences.token,
            userPreferences.refreshToken
        )
            .configureIoAndMainThread()
            .doOnSubscribe {
                if (isShowLoading)
                    view.showLoading()
            }
            .doFinally {
                view.hideLoading()
            }
            .subscribe({
                updateData(it)
                view.showP2Success()
            }, {
                view.onErrorRefreshToken()
            }).addTo(disposable)
    }

    private fun updateData(loginResponse: LoginResponse) {
        userPreferences.saveToken(loginResponse.accessToken)
        userPreferences.saveRefreshToken(loginResponse.refreshToken)
        mfaUserInformation.cleanMfaRegisters()
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
}