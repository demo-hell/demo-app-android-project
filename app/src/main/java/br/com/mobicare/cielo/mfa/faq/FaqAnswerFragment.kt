package br.com.mobicare.cielo.mfa.faq

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
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionDetailModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.LoggedHelpCenterContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.QuestionAndAnswerPresenter
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.presentation.ui.ChatDialog
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.open
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_faq_answer.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class FaqAnswerFragment : BaseFragment(),
        LoggedHelpCenterContract.QuestionAndAnswerView,
        CieloNavigationListener {

    private var isToFetchUserQuestion: Boolean? = false
    private var userQuestion: FrequentQuestionsModelView? = null
    private lateinit var questionsModelView: QuestionRequestModelView
    private val disposableHandler = CompositeDisposableHandler()
    private var cieloNavigation: CieloNavigation? = null

    private val questionAndAnswerPresenter: QuestionAndAnswerPresenter by inject {
        parametersOf(this)
    }

    override fun onResume() {
        super.onResume()
        disposableHandler.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableHandler.destroy()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_faq_answer,
                container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
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

    private fun init() {
        arguments?.let {
            this.questionsModelView = it.getParcelable("QUESTION_EXTRAS")!!
            this.isToFetchUserQuestion = true
        }
        configureCieloNavigation()
    }

    private fun configureCieloNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.showHelpButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
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
        this.cieloNavigation?.showContent(true)
        this.cieloNavigation?.showHelpButton(false)
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
            this.cieloNavigation?.showLoading(true)
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            this.cieloNavigation?.showLoading(false)
        }
        this.cieloNavigation?.showHelpButton(false)
    }

    override fun onRetry() {
        callQuestionDetails()
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                this.cieloNavigation?.showError(error)
            }
        }
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

        UserPreferences.getInstance().token?.run userLogged@{
            if (!isToFetchUserQuestion!!) {
                userQuestion?.run userQuestion@{
                    funcToCall(this@userLogged,
                            QuestionRequestModelView("",
                                    this@userQuestion.faqId,
                                    this@userQuestion.subcategoryId,
                                    this@userQuestion.id))
                }
            } else {
                questionsModelView?.run questionModelView@{

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
        linearVideoContent.gone()
        webviewQuestionAndAnswerVideo.gone()
    }

    override fun showQuestionReactionError() {
        if (isAttached()) {
            linearLikeOrDislike.gone()
        }
    }
}