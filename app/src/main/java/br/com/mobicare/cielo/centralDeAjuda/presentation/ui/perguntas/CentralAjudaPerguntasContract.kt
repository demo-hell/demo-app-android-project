package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface CentralAjudaPerguntasContract {

    interface Presenter : BasePresenter<View> {
        fun resubmit()
        fun loadQuestions(faqId: String, subcategoryId: String)
        fun onQuestionSelected(question: QuestionDataResponse)
        fun loadQuestionsByName(tagToApi: String? = null)
    }

    interface View : BaseView, IAttached {
        fun showQuestions(questions: List<QuestionDataResponse>)
        fun showQuestionAnswerDetail(obj: QuestionRequestModelView)
    }

}