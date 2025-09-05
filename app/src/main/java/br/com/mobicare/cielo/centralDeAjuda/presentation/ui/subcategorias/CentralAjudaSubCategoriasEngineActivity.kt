package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.QuestionRequestModelView
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.QuestionAndResponseFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas.CentralAjudaPerguntasFragment
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUESTION_REQUEST
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.interactbannersoffers.router.InteractBannerRouter
import kotlinx.android.synthetic.main.sub_categorias_activity.*

class CentralAjudaSubCategoriasEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener {

    private var sequence = 0
    private var bundles = HashMap<Int, Bundle>()
    private var indexBySequence = 0

    companion object{
        const val NOT_CAME_FROM_HELP_CENTER = "NOT_CAME_FROM_HELP_CENTER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_categorias_activity)
        setupToolbar(this.toolbar_subcategorias as Toolbar, getString(R.string.sub_categorias_toolbar_title))
        goToFirstFragment()
    }

    private fun goToFirstFragment() {
        intent?.extras?.let {
            if (it.getBoolean(NOT_CAME_FROM_HELP_CENTER)) {
                indexBySequence = 1
                sequence = indexBySequence
            }
            bundles[indexBySequence] = it
        }

        setFragment(false, intent.extras)
    }

    override fun onBackPressed() {
        if (isAttached()) {
            if (sequence <= indexBySequence) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                sequence--
                setFragment(true, this.bundles[this.sequence])
            }
        }
    }

    override fun onNextStep(isFinish: Boolean, bundle: Bundle?) {
        if (isAttached())
            if (isFinish) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                sequence++
                bundle?.let { itBundle ->
                    this.bundles[this.sequence] = itBundle
                }
                setFragment(false, bundle)
            }
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        if (isAttached())
            setupToolbar(toolbar_subcategorias as Toolbar, title)
    }

    fun setFragment(isBackAnimation: Boolean, bundle: Bundle? = null) {
        when (sequence) {
            0 -> goToSelectSubCategorias(isBackAnimation, bundle)
            1 -> goToSelectQuestion(isBackAnimation, bundle)
            2 -> goToQuestionAnswerDetail(isBackAnimation, bundle)
        }
    }

    private fun goToSelectSubCategorias(isBackAnimation: Boolean, bundle: Bundle?) {
        bundle?.let { itBundle ->
            CentralAjudaSubCategoriasFragment.newInstance(itBundle)
                    .addWithAnimation(supportFragmentManager, R.id.mainframe, isBackAnimation)
        }
    }

    private fun goToSelectQuestion(isBackAnimation: Boolean, bundle: Bundle?) {
        bundle?.let { itBundle ->
            CentralAjudaPerguntasFragment.newInstance(itBundle)
                    .addWithAnimation(supportFragmentManager, R.id.mainframe, isBackAnimation)
        }
    }

    private fun goToQuestionAnswerDetail(isBackAnimation: Boolean, bundle: Bundle?) {
        bundle?.let { itBundle ->
            val questionRequest = itBundle.getParcelable<QuestionRequestModelView>(ARG_PARAM_QUESTION_REQUEST)
            QuestionAndResponseFragment.create(true, null, questionRequest)
                    .addWithAnimation(supportFragmentManager, R.id.mainframe, isBackAnimation)
        }
    }
}