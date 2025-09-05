package br.com.mobicare.cielo.suporteTecnico.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.configureItemDecoration
import br.com.mobicare.cielo.databinding.ActivityTechnicalSupportProblemBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.suporteTecnico.domain.entities.Problem
import br.com.mobicare.cielo.suporteTecnico.domain.entities.ProblemSolution
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalProblemItems
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalSupportProblemsAdapter
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class TechnicalSupportProblemActivity : BaseLoggedActivity(),
        BaseActivity.AnimationListener, BaseActivity.OnBackButtonListener {

    private lateinit var binding: ActivityTechnicalSupportProblemBinding
    private var page : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTechnicalSupportProblemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supportItem: SupportItem? = intent.getParcelableExtra(TECHNICAL_SUPPORT_ITEM_KEY)

        supportItem?.let {
            setupToolbar(binding.toolbarTechnicalSupportProblem.toolbarMain, it.categoryName ?: EMPTY)
            setupItems(it)
            page = it.categoryName?.replace(ONE_SPACE, SIMPLE_LINE) ?: EMPTY
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            this.animationListener = this
            this.onBackButtonListener = this
        }
    }

    override fun onResume() {
        super.onResume()
        sendGaScreenView()
        logScreenView()
    }

    private fun setupItems(supportItem: SupportItem) {
        binding.recyclerTechnicalSupportProblemsMenu.apply {
            layoutManager = LinearLayoutManager(context).also {
                configureItemDecoration(
                    context,
                    it,
                    R.drawable.shape_item_technical
                )
            }
            adapter = TechnicalSupportProblemsAdapter(
                TechnicalProblemItems(supportItem.problems),
                object : TechnicalSupportProblemsAdapter.OnClickListener {
                    override fun onClick(problem: Problem) = onItemClick(problem)
                }
            )
            setHasFixedSize(true)
        }
    }

    private fun onItemClick(problem: Problem) {
        sendGaButton(problem.name)
        logItemClick(problem.name)

        val intent = Intent(
            this@TechnicalSupportProblemActivity,
            TechnicalSupportSolutionActivity::class.java
        )

        intent.putExtra(TechnicalSupportSolutionActivity.TECHNICAL_SUPPORT_PROBLEM_KEY, problem)

        if (isSolution(problem)) {
            intent.putExtra(
                TechnicalSupportSolutionActivity.TECHNICAL_SUPPORT_SOLUTION_KEY,
                ProblemSolution(problem.name, problem.idProblem.toString())
            )
        }

        intent.putExtra(
            TechnicalSupportSolutionActivity.TECHNICAL_SUPPORT_WEB_VIEW_KEY,
            isSolution(problem)
        )

        startActivityForResult(intent, TechnicalSupportSolutionActivity.REQUEST_CODE)
    }

    private fun isSolution(problem: Problem) = problem.solutions.isNullOrEmpty()

    override fun whenClose() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onBackTouched() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendGaButton("voltando")
    }

    //region GaFirebase

    private fun logScreenView() {
        GA4.logScreenView(GA4.buildScreenViewPath(suffix = page))
    }

    private fun logItemClick(problemName: String) {
        page?.let {
            GA4.logSelectContent(
                screenName = GA4.buildScreenViewPath(suffix = page),
                contentComponent = it,
                contentName = problemName
            )
        }
    }

    @Deprecated("Antigo GA")
    private fun sendGaScreenView() {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = "/suporte-tecnico/$page",
                screenClass = this.javaClass
            )
        }
    }

    @Deprecated("Antigo GA")
    private fun sendGaButton(label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "central de ajuda"),
            action = listOf("suporte-tecnico"),
            label = listOf(label)
        )
    }

    //endregion

    companion object {
        const val TECHNICAL_SUPPORT_ITEM_KEY = "br.com.cielo.suporteTecnico.technicalSupportProblemItem"
    }

}