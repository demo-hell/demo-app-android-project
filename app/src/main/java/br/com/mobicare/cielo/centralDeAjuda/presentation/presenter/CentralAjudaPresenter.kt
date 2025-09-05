package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter

import br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.CentralDeAjudaRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract
import br.com.mobicare.cielo.commons.data.utils.APIUtils.convertToErro
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class CentralAjudaPresenter(
    private val mView: CentralAjudaContract.View,
    private val repository: CentralDeAjudaRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : CentralAjudaContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun callAPI() {
        disposable.add(
            repository.registrationData()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe { mView.showProgress() }
                .doFinally { mView.hideProgress() }
                .subscribe({
                    mView.showContent(it)
                }, {
                    val errorMessage = convertToErro(it)
                    mView.showError(errorMessage.message)
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