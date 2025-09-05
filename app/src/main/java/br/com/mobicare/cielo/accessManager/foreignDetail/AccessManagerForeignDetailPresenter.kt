package br.com.mobicare.cielo.accessManager.foreignDetail

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerForeignDetailPresenter(
    private val view: AccessManagerForeignDetailContract.View,
    private val repository: AccessManagerRepository,
    private val userPreferences: UserPreferences
) : AccessManagerForeignDetailContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun getUsername(): String = userPreferences.userName

    override fun getForeignUserDetail(userId: String) {

        repository.getForeignUserDetail(userId)
            .configureIoAndMainThread()
            .subscribe({ response ->
                view.getDetailSuccess(response)
            }, {
                view.showGenericError()
            }).addTo(disposable)
    }

    override fun sendForeignUserDecision(
        userId: String,
        decision: String,
        otp: String
    ) {

        view.showLoading()
        repository.sendForeignUserDecision(otp, userId, decision)
            .configureIoAndMainThread()
            .subscribe({
                view.hideLoading()
                if (it.isSuccessful) {
                    view.decisionSuccess(decision)
                } else {
                    val error = APIUtils.convertToErro(it)
                    if (error.errorCode.contains(Text.OTP)) {
                        view.onErrorOTP()
                    } else {
                        view.decisionError()
                    }
                }
            }, {
                view.hideLoading()
                view.showError(APIUtils.convertToErro(it))
            }).addTo(disposable)

    }


}