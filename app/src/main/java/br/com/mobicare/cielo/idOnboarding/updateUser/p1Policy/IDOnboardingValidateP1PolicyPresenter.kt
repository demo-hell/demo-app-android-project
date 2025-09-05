package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

private const val ERROR_CODE_USER_NOT_FOUND = "USER_NOT_FOUND"

class IDOnboardingValidateP1PolicyPresenter(
    val repository: IDOnboardingRepository,
    val view: IDOnboardingValidateP1PolicyContract.View,
    val userPreferences: UserPreferences,
) : IDOnboardingValidateP1PolicyContract.Presenter {

    private var retryCallback: (() -> Unit)? = null
    private var disposable = CompositeDisposable()

    fun requestP1PolicyValidation() {
        onResume()
        retryCallback = { requestP1PolicyValidation() }

        repository.requestP1PolicyValidation()
            .configureIoAndMainThread()
            .subscribe({
                userStatus.onboardingStatus = it

                view.onPolicyP1Requested()
            }, {
                view.showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
    }

    fun checkP1PolicyResult() {
        onResume()
        retryCallback = { checkP1PolicyResult() }

        repository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .subscribe({
                userStatus.onboardingStatus = it

                val validated = when {
                    it.p1Flow?.p1Validation?.responseOn.isNullOrBlank() -> null
                    else -> it.p1Flow?.p1Validation?.validated == true
                }

                view.onPolicyP1StatusArrived(validated, userStatus.onboardingStatus?.role)
            }, {
                val error = ErrorMessage.fromThrowable(it)
                when (error.errorCode) {
                    ERROR_CODE_USER_NOT_FOUND -> view.showErrorUserNotFound(userStatus.onboardingStatus?.role)
                    else -> view.showError(error)
                }
            }).addTo(disposable)
    }

    fun updateStatusAndCallIfSucceeded(userRole: String? = null, callback: () -> Unit) {
        repository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .subscribe({ onboardingStatus ->
                userStatus.onboardingStatus = onboardingStatus
                callback.invoke()
            }, {
                if (ErrorMessage.fromThrowable(it).httpStatus == HTTP_UNAUTHORIZED) {
                    view.showErrorUserNotFound(userRole)
                } else {
                    view.showError(ErrorMessage.fromThrowable(it))
                }
            }).addTo(disposable)
    }

    fun saveNewCpfToShowOnLogin() {
        userPreferences.apply {
            keepUserName(userStatus.cpf?.removeNonNumbers())
            keepLogin(true, EMPTY, userStatus.cpf)
            isStepTwo(true)
        }
        view.onLogout()
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