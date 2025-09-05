package br.com.mobicare.cielo.pix.ui.mylimits.timemanagement

import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class PixMyLimitsTimeManagementPresenter(
    private val view: PixMyLimitsTimeManagementContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixTimeManagementRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixMyLimitsTimeManagementContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun getNightTime() {
        disposable.add(
            repository.getNightTime()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .doFinally {
                    view.hideLoading()
                }
                .subscribe({
                    view.onSuccessGetNightTime(it)
                }, {
                    val error = APIUtils.convertToErro(it)
                    if (error.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                            OTP
                        )
                    )
                        view.onErrorGetNightTime(error)
                })
        )
    }

    override fun onUpdateNightTime(
        otp: String?,
        nightTimeStart: String?
    ) {
        disposable.add(
            repository.updateNightTime(otp, PixTimeManagementRequest(nightTimeStart))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                        view.onSuccessUpdateNightTime()
                    else
                        showError(APIUtils.convertToErro(it))
                }, { error ->
                    showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun showError(error: ErrorMessage? = null) {
        view.onErrorUpdateNightTime(onGenericError = {
            if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                    OTP
                )
            )
                view.showError(error)
        })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}