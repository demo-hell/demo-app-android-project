package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations.PixTrustedDestinationsRepositoryContract
import br.com.mobicare.cielo.pix.enums.PixServicesGroupEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixMyLimitsTrustedDestinationsPresenter(
    private val view: PixMyLimitsTrustedDestinationsContract.View,
    private val repository: PixTrustedDestinationsRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : PixMyLimitsTrustedDestinationsContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getTrustedDestinations() {
        disposable.add(
            repository.getTrustedDestinations(PixServicesGroupEnum.PIX.name)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .doFinally {
                    view.hideLoading()
                }
                .subscribe({
                    if (it.isNullOrEmpty())
                        view.onNoTrustedDestinations()
                    else
                        view.onSuccessTrustedDestinations(it)
                }, { error ->
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}