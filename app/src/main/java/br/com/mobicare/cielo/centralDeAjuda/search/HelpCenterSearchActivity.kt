package br.com.mobicare.cielo.centralDeAjuda.search

import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.core.view.doOnLayout
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_SEARCH
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_SEARCH_RESULT
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.HELP_CENTER_SEARCH_WITHOUT_RESULT
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.activities.QuestionAndAnswerActivity
import br.com.mobicare.cielo.centralDeAjuda.search.analytics.HelpCenterSearchAnalytics
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_mfa.errorView
import kotlinx.android.synthetic.main.activity_help_center_search.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.concurrent.timerTask
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class HelpCenterSearchActivity: BaseLoggedActivity(), HelpCenterSearchContract.View,
    DefaultViewListAdapter.OnItemClickListener<FrequentQuestionsModelView> {

    private val presenter: HelpCenterSearchPresenter by inject { parametersOf(this) }
    private val analytics by lazy { HelpCenterSearchAnalytics() }

    private var questionsAdapter: DefaultViewListAdapter<FrequentQuestionsModelView>? = null
    private var listSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_center_search)

        setupToolbar(toolbar as Toolbar, getString(R.string.help_center))
        setupListeners()
        setupRecyclerView()

        tvResults?.gone()
        ivClearSearch?.gone()
        presenter.search()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()

        if (etQuestionSearch?.isLaidOut == false) {
            hideSoftKeyboard()
        }

        etQuestionSearch?.doOnLayout {
            it.requestFocusFromTouch()
            showSoftKeyboard(it)
        }
        trackScreenView(HELP_CENTER_SEARCH)
    }

    override fun onSearchResult(list: List<FrequentQuestionsModelView>) {
        val term = etQuestionSearch?.text
        tvResults?.visible(term?.isBlank() == false)

        val size = list.size
        if (list.isEmpty()) {
            trackScreenView(HELP_CENTER_SEARCH_WITHOUT_RESULT)
            val message = getString(R.string.help_center_search_found_zero, term)
            tvResults?.text = HtmlCompat.fromHtml(message, FROM_HTML_MODE_LEGACY)
        } else {
            trackScreenView(HELP_CENTER_SEARCH_RESULT)
            val result =
                if (list.size == ONE) {
                    getString(R.string.help_center_search_result)
                } else {
                    getString(R.string.help_center_search_results)
                }
            val message = getString(R.string.help_center_search_found_results, size, result)
            tvResults?.text = HtmlCompat.fromHtml(message, FROM_HTML_MODE_LEGACY)
        }

        listSize = size
        questionsAdapter?.updateList(list)
    }

    override fun onItemClick(item: FrequentQuestionsModelView) {
        if (isAttached()) {
            analytics.logSearchResultItemClick(etQuestionSearch?.text.toString(), item)
            startActivity<QuestionAndAnswerActivity>(
                    QuestionAndAnswerActivity.USER_QUESTION to item,
                    QuestionAndAnswerActivity.FLOW_SEARCH to true,
            )
        }
    }

    fun setupListeners() {
        etQuestionSearch?.inputType = InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE

        var typingTimer = Timer()
        etQuestionSearch?.doAfterTextChanged {
            typingTimer.cancel()
            typingTimer = Timer()
            typingTimer.schedule(timerTask {
                runOnUiThread {
                    presenter?.search(it.toString())
                }
            }, FINISH_TYPING_INTERVAL)

            val isEmpty = it?.isEmpty() ?: true
            if (isEmpty) {
                rvQuestions?.scrollToPosition(0)
            }
            ivClearSearch?.visible(isEmpty.not())
            tvResults?.visible(isEmpty.not())
        }

        etQuestionSearch?.setOnKeyListener { _, keyCode, keyEvent ->
            if ((keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER)
                && keyEvent.action == KeyEvent.ACTION_UP) {
                hideSoftKeyboard()
                true
            }
            false
        }

        ivClearSearch?.setOnClickListener {
            analytics.logSearchClearClick(etQuestionSearch?.text.toString(), listSize)
            etQuestionSearch?.text?.clear()
            showSoftKeyboard(etQuestionSearch)
        }
    }

    private fun setupRecyclerView() {
        questionsAdapter = DefaultViewListAdapter(
            listOf<FrequentQuestionsModelView>(),
            R.layout.item_help_center_search_question
        ).apply {
            onItemClickListener = this@HelpCenterSearchActivity

            setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<FrequentQuestionsModelView> {

                override fun onBind(
                    item: FrequentQuestionsModelView,
                    holder: DefaultViewHolderKotlin
                ) {
                    var needsDivider = true
                    val position = holder.adapterPosition

                    holder.itemView.setBackgroundResource(
                        when {
                            listSize == SINGLE -> {
                                needsDivider = false
                                R.drawable.bg_cardcontent_list_item_single
                            }
                            position == FIRST_ITEM -> R.drawable.bg_cardcontent_list_item_first
                            position == listSize - 1 -> {
                                needsDivider = false
                                R.drawable.bg_cardcontent_list_item_last
                            }
                            else -> R.drawable.bg_cardcontent_list_item_middle
                        }
                    )

                    val tvQuestion = holder.itemView
                        .findViewById<TypefaceTextView>(R.id.tvQuestion)
                    tvQuestion.text = SpannableStringBuilder
                        .valueOf(item.question)

                    val viewItemDivider = holder.itemView
                        .findViewById<View>(R.id.viewItemDivider)
                    viewItemDivider.visible(needsDivider)

                    holder.itemView.setOnClickListener {
                        onItemClickListener?.onItemClick(item)
                    }
                }
            })
        }

        rvQuestions?.layoutManager = LinearLayoutManager(this)
        rvQuestions?.setWillNotDraw(false)
        rvQuestions?.adapter = questionsAdapter
    }

    override fun showLoading() {
        this.progressView?.visible()
        this.container_view?.gone()
        this.errorView?.gone()
    }

    override fun hideLoading() {
        this.progressView?.gone()
        this.container_view?.visible()

        etQuestionSearch?.doOnLayout {
            it.requestFocusFromTouch()
            showSoftKeyboard(it)
        }
    }

    override fun showError(error: ErrorMessage?) {
        trackError(
            errorCode = error?.code.orEmpty(),
            errorMessage = error?.message.orEmpty(),
        )
        this.container_view?.gone()
        this.progressView?.gone()

        if (error?.message.isNullOrBlank())
            error?.message = getString(R.string.text_message_generic_error_information_not_loaded)

        analytics.logSearchError(error?.errorCode ?: "", error?.httpStatus.toString())
        with(this.errorView) {
            this?.visible()
            this?.cieloErrorMessage = messageError(error, this@HelpCenterSearchActivity)
            this?.errorButton?.setText(getString(R.string.text_try_again_label))
            this?.cieloErrorTitle = getString(R.string.text_title_generic_error)
            this?.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            this?.configureActionClickListener {
                onRetry()
            }
        }

        this.hideSoftKeyboard()
    }

    private fun onRetry() {
        presenter.search()
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onBackPressed() {
        if (etQuestionSearch?.text?.isEmpty() == false) {
            etQuestionSearch?.text?.clear()
        } else {
            super.onBackPressed()
        }
    }

    private fun trackScreenView(screenName: String){
        if (isAttached()) {
            GA4.logScreenView(screenName)
        }
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER_SEARCH, errorCode, errorMessage)
        }
    }

    companion object {
        const val FINISH_TYPING_INTERVAL = 300L
        const val FIRST_ITEM = 0
        const val SINGLE = 1
    }
}