package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import io.reactivex.Scheduler

class FrequentQuestionHelpCenterPresenter(val view: LoggedHelpCenterContract.FrequentQuestionsView,
                                          val uiScheduler: Scheduler,
                                          val ioScheduler: Scheduler,
                                          val repository: CentralAjudaLogadoRepository) :
        LoggedHelpCenterContract.Presenter {

    private val disposableHandler = CompositeDisposableHandler()


    override fun onResume() {
        disposableHandler.start()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
    }

    override fun getFrequentQuestions(accessToken: String) {
        CentralAjudaLogadoRepository.allQuestionsList.run {
            if (this.isNullOrEmpty()) {
                downloadFrequentQuestions(accessToken)
            } else {
                view.hideLoading()
                view.showFrequentQuestionsList(this)
            }
        }
    }

    private fun downloadFrequentQuestions(accessToken: String) {
        disposableHandler.compositeDisposable
            .add(repository.getFrequentQuestions(accessToken)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe { view.showLoading() }
                .subscribe({ response ->
                    val list = FrequentQuestionsModelView.mapListFromQuestionResponse(response)
                    CentralAjudaLogadoRepository.allQuestionsList = list
                    view.hideLoading()
                    view.showFrequentQuestionsList(list)
                }, { error ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                }))
    }

}