package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.detail

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations.PixTrustedDestinationsRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixDeleteTrustedDestinationRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class PixMyLimitsTrustedDestinationsDetailPresenter(
    private val view: PixMyLimitsTrustedDestinationsDetailContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixTrustedDestinationsRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixMyLimitsTrustedDestinationsDetailContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun deleteTrustedDestination(otp: String?, id: String?) {
        disposable.add(
            repository.deleteTrustedDestination(otp, PixDeleteTrustedDestinationRequest(id))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                        view.onSuccessDeleteTrustedDestination()
                    else
                        showError()
                }, { error ->
                    showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun showError(errorMessage: ErrorMessage? = null) {
        view.onErrorDeleteTrustedDestination(onGenericError = {
            view.showError(errorMessage)
        })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}