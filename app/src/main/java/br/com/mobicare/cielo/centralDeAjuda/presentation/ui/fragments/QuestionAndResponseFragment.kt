package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_ANSWERS
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionDetailModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.LoggedHelpCenterContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.QuestionAndAnswerPresenter
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.presentation.ui.ChatDialog
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.getFirstTenCharacters
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.utils.open
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_question_and_response.*
import kotlinx.android.synthetic.main.linear_loan_simulation_error.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class QuestionAndResponseFragment : BaseFragment(),
        LoggedHelpCenterContract.QuestionAndAnswerView {

    private var isToFetchUserQuestion: Boolean? = false
    private var userQuestion: FrequentQuestionsModelView? = null
    private var questionsModelView: QuestionRequestModelView? = null

    private val disposableHandler = CompositeDisposableHandler()

    var logoutListener: LogoutListener? = null

    private val questionAndAnswerPresenter: QuestionAndAnswerPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(isToFetchUserQuestion: Boolean?,
                   userQuestion: FrequentQuestionsModelView?,
                   questionRequestModelView: QuestionRequestModelView? = null): QuestionAndResponseFragment {
            return QuestionAndResponseFragment().apply {
                this.isToFetchUserQuestion = isToFetchUserQuestion
                this.userQuestion = userQuestion
                this.questionsModelView = questionRequestModelView
            }
        }
    }

    override fun onResume() {
        super.onResume()
        disposableHandler.start()
        trackScreenView()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableHandler.destroy()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_question_and_response,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callQuestionDetails()

        disposableHandler.compositeDisposable.add(
                Observable.timer(3000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showLikesOrDislikes()
                        }, { error ->
                            FirebaseCrashlytics.getInstance().recordException(error)
                        })
        )
    }

    private fun callQuestionDetails() {
        UserPreferences.getInstance().token?.run {
            questionAndAnswerPresenter.getQuestionAndAnswer(this,
                    isToFetchUserQuestion ?: false, question = questionsModelView)
        }
    }


    override fun showPropertyQuestionAndAnswer() {
        userQuestion?.run {
            textFrequentUserAsk.text = SpannableStringBuilder.valueOf(this.question)
            textFrequentQuestionAnswer.text = SpannableStringBuilder.valueOf(this.answer)

            if (videoLink == null) {
                hideVideo()
            } else {
                startWebviewWithLink(videoLink)
            }

        }
    }

    private fun startWebviewWithLink(link: String) {
        if (isAttached()) {
            webviewQuestionAndAnswerVideo.open(link, object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    showVideoWebviewLoading()
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    hideViewWebviewLoading()
                    super.onPageFinished(view, url)
                }

                private fun hideViewWebviewLoading() {
                    if (isAttached()) {
                        frameWebviewLoging.visibility = View.GONE
                        webviewQuestionAndAnswerVideo.visibility = View.VISIBLE
                    }
                }

                private fun showVideoWebviewLoading() {
                    if (isAttached()) {
                        webviewQuestionAndAnswerVideo.visibility = View.GONE

                        frameWebviewLoging.visibility = View.VISIBLE
                    }
                }

                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                    view?.handleSslError(handler, requireContext())
                }
            })
        }
    }


    override fun showRemoteQuestionAndAnswer(questionData: QuestionDetailModelView) {
        textFrequentUserAsk.text = SpannableStringBuilder.valueOf(questionData.question)
        textFrequentQuestionAnswer.text = SpannableStringBuilder.valueOf(questionData.answer)

        if (questionData.videoLink == null) {
            hideVideo()
        } else {
            startWebviewWithLink(questionData.videoLink)
        }

    }


    override fun showLoading() {
        if (isAttached()) {
            linearQuestionAndResponseContent.visibility = View.GONE
            linearQuestionAndResponseError.visibility = View.GONE
            frameQuestionAndResponseLoading.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            frameQuestionAndResponseLoading.visibility = View.GONE
            linearQuestionAndResponseError.visibility = View.GONE
            linearQuestionAndResponseContent.visibility = View.VISIBLE
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            trackError(
                errorCode = error?.code.orEmpty(),
                errorMessage = error?.message.orEmpty(),
            )
            linearQuestionAndResponseContent.visibility = View.GONE
            frameQuestionAndResponseLoading.visibility = View.GONE
            linearQuestionAndResponseError.visibility = View.VISIBLE

            buttonLoanSimulationErrorRetry.setOnClickListener {
                callQuestionDetails()
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        logoutListener?.onLogout()
    }

    override fun showLikesOrDislikes() {

        if (isAttached()) {

            linearLikeOrDislike.visibility = View.VISIBLE

            val translateAnimation = TranslateAnimation(0f, 0f,
                    linearLikeOrDislike.height.toFloat(),
                    0f)

            translateAnimation.fillAfter = true
            translateAnimation.duration = 3000
            linearLikeOrDislike.startAnimation(translateAnimation)

            buttonNegativeQuestion.setOnClickListener {

                callLikeWithCorrectDataObject(questionAndAnswerPresenter::dislikeQuestion)

            }

            buttonPositiveQuestion.setOnClickListener {

                callLikeWithCorrectDataObject(questionAndAnswerPresenter::likeQuestion)

            }
        }
    }

    private fun callLikeWithCorrectDataObject(funcToCall: (token: String,
                                                           questionModelView:
                                                           QuestionRequestModelView) -> Unit) {

        UserPreferences.getInstance().token?.run userLogged@ {


            if (!isToFetchUserQuestion!!) {
                userQuestion?.run userQuestion@ {
                    funcToCall(this@userLogged,
                            QuestionRequestModelView("",
                            this@userQuestion.faqId,
                            this@userQuestion.subcategoryId,
                            this@userQuestion.id))
                }
            } else {
                questionsModelView?.run questionModelView@ {

                    funcToCall(this@userLogged, this)

                }
            }

        }
    }


    override fun showUserQuestionReactionLoading() {
        if (isAttached()) {
            linearLikeOrDislikeContent.visibility = View.GONE
            frameLikeOrDislikeLoading.visibility = View.VISIBLE
        }
    }

    override fun hideUserQuestionReactionLoading() {
        if (isAttached()) {
            frameLikeOrDislikeLoading.visibility = View.GONE
            linearLikeOrDislikeContent.visibility = View.VISIBLE
        }
    }

    override fun showQuestionLikedReaction() {
        if (isAttached()) {
            linearUserGoodReaction.visibility = View.VISIBLE
            linearUserBadReaction.visibility = View.GONE
            linearLikeOrDislikeContent.visibility = View.GONE
        }
    }

    override fun showQuestionDislikedReaction() {

        if (isAttached()) {
            linearUserBadReaction.visibility = View.VISIBLE
            linearUserGoodReaction.visibility = View.GONE
            linearLikeOrDislikeContent.visibility = View.GONE


            linearUserBadReaction.setOnClickListener {

                UserPreferences.getInstance().token?.run {
                    ChatDialog.showDialog(requireActivity(), EnumFeatures.CHAT,
                            UserPreferences.getInstance().numeroEC, this, "Central de ajuda")
                }
            }
        }

    }

    private fun hideVideo() {
        linearVideoContent.visibility = View.GONE
        webviewQuestionAndAnswerVideo.visibility = View.GONE
    }

    override fun showQuestionReactionError() {
        if (isAttached()) {
            linearLikeOrDislike.visibility = View.GONE
        }
    }

    private fun trackScreenView()  {
        if (isAttached()) {
            GA4.logScreenView(
                screenName = GA4.buildScreenViewPath(
                    screenName = HELP_CENTER_ANSWERS,
                    userQuestion?.answer?.getFirstTenCharacters(),
                ),
            )
        }
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER_ANSWERS, errorCode, errorMessage)
        }
    }
}