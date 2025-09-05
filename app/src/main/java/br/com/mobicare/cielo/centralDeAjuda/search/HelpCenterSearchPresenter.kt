package br.com.mobicare.cielo.centralDeAjuda.search

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import io.reactivex.Scheduler

class HelpCenterSearchPresenter(
    val view: HelpCenterSearchContract.View,
    val uiScheduler: Scheduler,
    val ioScheduler: Scheduler,
    val repository: CentralAjudaLogadoRepository,
    val userPreferences: UserPreferences? = UserPreferences.getInstance()
) : HelpCenterSearchContract.Presenter {

    private val disposableHandler = CompositeDisposableHandler()
    private var searchTerm: String? = null

    override fun search(term: String) {
        val typedTerm = term.take(MAX_TERM_SIZE).trim().replace(Regex(" +")," ")
        if (typedTerm != searchTerm) {
            searchTerm = typedTerm

            CentralAjudaLogadoRepository.allQuestionsList.run {
                if (this.isNullOrEmpty()) {
                    downloadQuestions()
                } else {
                    filterQuestions(this)
                }
            }
        }
    }

    override fun onPause() {
        disposableHandler.destroy()
    }

    override fun onResume() {
        disposableHandler.start()
    }

    private fun filterQuestions(list: List<FrequentQuestionsModelView>) {
        val resultMutableList = mutableListOf<FrequentQuestionsModelView>()
        searchTerm?.let { term ->
            val filterList = list.toMutableList()
            resultMutableList.addAll(filterList.filter {
                it.question?.contains(term) == true
            })

            filterList.removeAll(resultMutableList)

            resultMutableList.addAll(filterList.filter { it.answer?.contains(term) == true })
        } ?: resultMutableList.addAll(list)

        view.hideLoading()
        view.onSearchResult(resultMutableList.toList())
    }

    private fun downloadQuestions() {
        userPreferences?.token?.let { accessToken ->
            disposableHandler.compositeDisposable
                .add(repository.getFrequentQuestions(accessToken)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe { view.showLoading() }
                    .subscribe({ response ->
                        val list = FrequentQuestionsModelView.mapListFromQuestionResponse(response)
                        CentralAjudaLogadoRepository.allQuestionsList = list
                        filterQuestions(list)
                    }, { error ->
                        searchTerm = null
                        view.hideLoading()
                        view.showError(APIUtils.convertToErro(error))
                    })
                )
        } ?: run {
            searchTerm = null
            view.showError(ErrorMessage().apply {
                message = TOKEN_ERROR_MESSAGE
                errorCode = TOKEN_ERROR_MESSAGE
                httpStatus = TOKEN_ERROR_HTTP_STATUS
            })
        }
    }

    companion object {
        const val TOKEN_ERROR_MESSAGE = "Token de acesso inválido"
        const val TOKEN_ERROR_HTTP_STATUS = 401
        const val MAX_TERM_SIZE = 80
    }
}