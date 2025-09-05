package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.activities

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.QuestionAndResponseFragment
import br.com.mobicare.cielo.commons.listener.DefaultLogoutListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import kotlinx.android.synthetic.main.activity_answer_and_question.*

class QuestionAndAnswerActivity : BaseLoggedActivity() {

    private val userQuestion: FrequentQuestionsModelView? by lazy {
        intent?.getParcelableExtra<FrequentQuestionsModelView?>(USER_QUESTION)
    }


    private val isToFetchUserQuestion: Boolean? by lazy {
        intent?.getBooleanExtra(FETCH_USER_QUESTION, false)
    }

    private val isFlowSearch: Boolean? by lazy {
        intent?.getBooleanExtra(FLOW_SEARCH, false)
    }

    companion object {
        const val USER_QUESTION = "br.com.cielo.centralDeAjuda.userQuestion"
        const val FLOW_SEARCH = "br.com.cielo.centralDeAjuda.flow.search"
        const val FETCH_USER_QUESTION = "br.com.cielo.centralDeAjuda.isToFetchUserQuestion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_and_question)
        val title = getString(if (isFlowSearch == true) R.string.menu_central_ajuda
        else R.string.text_frequent_questions_title)
        setupToolbar(toolbarAnswerAndQuestion as Toolbar, title)

        QuestionAndResponseFragment.create(isToFetchUserQuestion,
                userQuestion).apply {
            logoutListener = DefaultLogoutListener {
                SessionExpiredHandler
                        .userSessionExpires(this@QuestionAndAnswerActivity,
                                true)
            }
        }.addInFrame(supportFragmentManager, R.id.frameAnswerAndResponse)


    }

}