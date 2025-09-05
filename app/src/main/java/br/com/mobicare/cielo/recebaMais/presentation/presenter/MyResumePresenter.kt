package br.com.mobicare.cielo.recebaMais.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.recebaMais.managers.MyResumeRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class MyResumePresenter(private val view: MyResumeContract.View,
                        private val repository: MyResumeRepository,
                        private val userPreferences: UserPreferences,
                        private val uiScheduler: Scheduler,
                        private val ioScheduler: Scheduler) : MyResumeContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onStart() {
        if (disposable.isDisposed)
            disposable = CompositeDisposable()
    }

    override fun loadDetails() {
        disposable.add(
                repository.getContractsDetails(userPreferences.token)
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            view.showLoading()
                        }
                        .subscribe({
                            view.hideLoading()
                            if (it.contracts.isNullOrEmpty()) {
                                view.showError()
                            } else {
                                val installment = it.contracts[0].installments.asReversed()
                                it.contracts[0].installments = installment
                                view.showContract(it.contracts[0])
                            }
                        }, {
                            view.hideLoading()
                            view.showError()
                        }))
    }

    override fun onDestroy() {
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }
}