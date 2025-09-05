package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter

import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionDetailModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import io.reactivex.Scheduler

class QuestionAndAnswerPresenter(val view: LoggedHelpCenterContract.QuestionAndAnswerView,
                                 val uiScheduler: Scheduler,
                                 val ioScheduler: Scheduler,
                                 val repository: CentralAjudaLogadoRepository) :
        LoggedHelpCenterContract.QuestionAndAnswerPresenter {


    private val disposableHandler = CompositeDisposableHandler()

    override fun onResume() {
        disposableHandler.start()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
    }


    override fun getQuestionAndAnswer(accessToken: String,
                                      isToFetchUserQuestion: Boolean,
                                      question: QuestionRequestModelView?) {

        if (!isToFetchUserQuestion) {
            view.showPropertyQuestionAndAnswer()
        } else {
            question?.run {
                disposableHandler.compositeDisposable.add(repository.getQuestionDetail(accessToken,
                        this)
                        .subscribeOn(ioScheduler)
                        .observeOn(uiScheduler)
                        .doOnSubscribe { view.showLoading() }
                        .subscribe({ response ->

                            var videoLink: String? = null

                            response.video?.run {
                                videoLink = "${this.link}/${this.code}"
                            }
                            view.hideLoading()
                            view.showRemoteQuestionAndAnswer(
                                    QuestionDetailModelView(response.question, response.answer,
                                            response.id, videoLink))
                        }, { error ->
                            view.hideLoading()
                            view.showError(APIUtils.convertToErro(error))
                        }))
            }
        }
    }

    override fun likeQuestion(accessToken: String,
                              question: QuestionRequestModelView) {
        disposableHandler.compositeDisposable.add(repository.likeQuestion(accessToken, question)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    view.showUserQuestionReactionLoading()
                }
                .subscribe({
                    view.hideUserQuestionReactionLoading()
                    view.showQuestionLikedReaction()

                }, {

                    view.hideUserQuestionReactionLoading()
                    view.showQuestionReactionError()
                }))
    }

    override fun dislikeQuestion(accessToken: String,
                                 question: QuestionRequestModelView) {

        disposableHandler.compositeDisposable.add(repository.dislikeQuestion(accessToken, question)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    view.showUserQuestionReactionLoading()
                }
                .subscribe({

                    view.hideUserQuestionReactionLoading()
                    view.showQuestionDislikedReaction()

                }, {

                    view.hideUserQuestionReactionLoading()
                    view.showQuestionReactionError()
                }))
    }


}