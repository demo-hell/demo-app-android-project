package br.com.mobicare.cielo.interactbannersoffers.termoAceite

import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class TermoAceitePresenter(
    private val view: TermoAceiteContract.View,
    private val repository: InteractBannerRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : TermoAceiteContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun submitTermoAceite(bannerId: Int) {
        disposable.add(
            repository.postTermoAceite(bannerId)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() == 200)
                        view.showSuccess()
                    else
                        view.showError()
                }, {
                    view.showError()
                })
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onStop() {
        disposable.dispose()
    }
}