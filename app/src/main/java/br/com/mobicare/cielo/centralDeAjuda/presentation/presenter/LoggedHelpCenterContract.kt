package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter

import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionDetailModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter

interface LoggedHelpCenterContract {

    interface Presenter : CommonPresenter {
        fun getFrequentQuestions(accessToken: String)
    }

    interface QuestionAndAnswerPresenter : CommonPresenter {

        fun likeQuestion(accessToken: String,
                         question: QuestionRequestModelView)

        fun dislikeQuestion(accessToken: String,
                            question: QuestionRequestModelView)


        fun getQuestionAndAnswer(accessToken: String,
                                 isToFetchUserQuestion: Boolean = false,
                                 question: QuestionRequestModelView?)
    }

    interface FrequentQuestionsView : BaseView {
        fun showFrequentQuestionsList(frequentQuestions: List<FrequentQuestionsModelView>)
    }

    interface QuestionAndAnswerView : BaseView {

        fun showPropertyQuestionAndAnswer()
        fun showRemoteQuestionAndAnswer(questionData: QuestionDetailModelView)

        fun showLikesOrDislikes()

        fun showUserQuestionReactionLoading()
        fun hideUserQuestionReactionLoading()

        fun showQuestionLikedReaction()
        fun showQuestionDislikedReaction()

        fun showQuestionReactionError()

    }

}