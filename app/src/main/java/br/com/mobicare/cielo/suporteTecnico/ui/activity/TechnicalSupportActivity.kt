package br.com.mobicare.cielo.suporteTecnico.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.configureItemDecoration
import br.com.mobicare.cielo.suporteTecnico.TechnicalSupportContract
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalSupportAdapter
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalSupportItems
import br.com.mobicare.cielo.suporteTecnico.ui.presenter.TechnicalSupportPresenter
import kotlinx.android.synthetic.main.activity_technical_support.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class TechnicalSupportActivity : BaseLoggedActivity(), TechnicalSupportContract.View,
        BaseActivity.AnimationListener, BaseActivity.OnBackButtonListener {

    private val presenter: TechnicalSupportPresenter by inject {
        parametersOf(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_technical_support)

        setupToolbar(toolbarTechnicalSupport as Toolbar, getString(R.string.text_technical_suppport_title))

        presenter.loadView(this)
        presenter.loadItems()

        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        this.animationListener = this
        this.onBackButtonListener = this
    }

    override fun onResume() {
        super.onResume()
        sendGaScreenView()
    }

    override fun loadTechnicalSupportItems(support: List<SupportItem>) {

        recyclerTechnicalSupportMenu.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerTechnicalSupportMenu.layoutManager = linearLayoutManager
        val technicalSupportAdapter = TechnicalSupportAdapter(TechnicalSupportItems(support),
                object : TechnicalSupportAdapter.OnClickListener {

                    override fun onClick(supportItem: SupportItem) {
                        sendGaButton(supportItem.categoryName ?: "")

                        val intent = Intent(this@TechnicalSupportActivity,
                                TechnicalSupportProblemActivity::class.java)
                        intent.putExtra(TechnicalSupportProblemActivity
                                .TECHNICAL_SUPPORT_ITEM_KEY, supportItem)
                        startActivity(intent)
                    }
                })

        recyclerTechnicalSupportMenu.adapter = technicalSupportAdapter
        recyclerTechnicalSupportMenu.configureItemDecoration(this,
                linearLayoutManager,
                R.drawable.shape_item_technical)
    }

    override fun systemError(error: ErrorMessage) {
        showErrorMessage(error, getString(R.string.text_technical_suppport_title))
    }

    override fun showLoading() {
        progressTechnical.visibility = View.VISIBLE
        recyclerTechnicalSupportMenu.visibility = View.GONE
    }

    override fun hideLoading() {
        progressTechnical.visibility = View.GONE
        recyclerTechnicalSupportMenu.visibility = View.VISIBLE
    }

    override fun userError(error: ErrorMessage) {
        showErrorMessage(error, getString(R.string.text_technical_suppport_title))
    }

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

    private fun sendGaScreenView() {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = "/suporte-tecnico",
                screenClass = this.javaClass
            )
        }
    }

    private fun sendGaButton(label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "central de ajuda"),
            action = listOf("suporte-tecnico"),
            label = listOf(label)
        )
    }

    //endregion
}