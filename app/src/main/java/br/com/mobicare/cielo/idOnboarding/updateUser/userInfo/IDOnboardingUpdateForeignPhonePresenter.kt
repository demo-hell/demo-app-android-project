package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class IDOnboardingUpdateForeignPhonePresenter(
    private val view: IDOnboardingUpdateForeignPhoneContract.View,
    private val repository: IDOnboardingRepository
): IDOnboardingUpdateForeignPhoneContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun sendForeignCellphone(phone: String) {
        view.showLoading()

        repository.sendForeignCellphone(phone)
            .configureIoAndMainThread()
            .subscribe({
                IDOnboardingFlowHandler.userStatus.onboardingStatus = it

                view.hideLoading()
                view.successSendForeignCellphone()
            }, { error ->
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(error))
            }).addTo(disposable)
    }
}