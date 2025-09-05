package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class CentralAjudaPerguntasPresenter(private val repository: CentralAjudaLogadoRepository) : CentralAjudaPerguntasContract.Presenter {

    private var faqId: String = ""
    private var subcategoryId: String = ""
    private var view: CentralAjudaPerguntasContract.View? = null

    override fun setView(view: CentralAjudaPerguntasContract.View) {
        this.view = view
    }

    override fun resubmit() {
        this.loadQuestions(this.faqId, this.subcategoryId)
    }

    override fun loadQuestions(faqId: String, subcategoryId: String) {
        this.faqId = faqId
        this.subcategoryId = subcategoryId
        UserPreferences.getInstance().token?.let { itToken ->
            this.repository.faqQuestions(itToken, faqId, subcategoryId, object : APICallbackDefault<List<QuestionDataResponse>, String> {
                override fun onStart() {
                    this@CentralAjudaPerguntasPresenter.view?.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    if (error.logout) {
                        this@CentralAjudaPerguntasPresenter.view?.logout(error)
                    } else {
                        this@CentralAjudaPerguntasPresenter.view?.showError(error)
                    }
                }

                override fun onSuccess(response: List<QuestionDataResponse>) {
                    this@CentralAjudaPerguntasPresenter.view?.showQuestions(response)
                    this@CentralAjudaPerguntasPresenter.view?.hideLoading()
                }
            })
        }
    }

    override fun loadQuestionsByName(tagToApi: String?) {
        UserPreferences.getInstance().token?.let { itToken ->
            this.repository.getFaqQuestionsByName(tagToApi, itToken, object : APICallbackDefault<List<QuestionDataResponse>, String> {
                override fun onStart() {
                    this@CentralAjudaPerguntasPresenter.view?.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    if (error.logout) {
                        this@CentralAjudaPerguntasPresenter.view?.logout(error)
                    } else {
                        this@CentralAjudaPerguntasPresenter.view?.showError(error)
                    }
                }

                override fun onSuccess(response: List<QuestionDataResponse>) {
                    this@CentralAjudaPerguntasPresenter.view?.showQuestions(response)
                    this@CentralAjudaPerguntasPresenter.view?.hideLoading()
                }
            })
        }
    }

    override fun onQuestionSelected(question: QuestionDataResponse) {
        if (!question.faqId.isNullOrEmpty() && !question.subcategoryId.isNullOrEmpty()) {
            this.faqId = question.faqId
            this.subcategoryId = question.subcategoryId
        }
        this.view?.showQuestionAnswerDetail(
                QuestionRequestModelView(
                        question.question,
                        this.faqId,
                        this.subcategoryId,
                        question.id))
    }

}