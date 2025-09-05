package br.com.mobicare.cielo.commons.ui.help

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.recebaMais.domain.HelpCenter
import kotlinx.android.synthetic.main.activity_help_receba_mais.*
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.toolbar_dialog.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class HelpMainActivity : BaseLoggedActivity(), HelpMainContract.View {

    private val presenter: HelpMainPresenter by inject { parametersOf(this) }
    private lateinit var helpMain: HelpMainFragment

    private var mIdHelp: String = ""

    companion object {
        const val HELP_TITLE = "help_title"
        const val HELP_ID = "help_id"

        fun create(activity: Activity, title: String, id: String) {
            val i = Intent(activity, HelpMainActivity::class.java)
            i.putExtra(HELP_TITLE, title)
            i.putExtra(HELP_ID, id)
            activity.startActivityForResult(i, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_receba_mais)

        configIntentExtra()
        configBackButton()
        configExitButton()
        configUpdateButton()
        configInitScreen()

    }

    override fun onResume() {
        super.onResume()
        presenter.setView(this)
        if (mIdHelp == PPL_HELP_ID)
            Analytics.trackScreenView(
                screenName = "/central-de-ajuda/pagamento-por-link",
                screenClass = this.javaClass
            )
    }

    override fun onBackPressed() {
        if (isAttached()) {
            if (supportFragmentManager.fragments.isEmpty() || supportFragmentManager.fragments.last() !is HelpDetailsFragment) {
                exitHelp()
            } else {
                btnLeft.visibility = View.GONE
                txtTitle.visibility = View.VISIBLE
                helpMain.addWithAnimation(supportFragmentManager, R.id.view_frame_content, true)
            }
        }
    }

    //region HelpMainContract.View
    override fun showError(error: ErrorMessage?) {
        if (isAttached())
            include_error.visibility = View.VISIBLE
    }

    override fun logout(msg: ErrorMessage?) {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun helpsSuccess(helpCenter: HelpCenter) {
        if (isAttached()) {
            helpMain = HelpMainFragment.create(helpCenter) {
                btnLeft.visibility = View.VISIBLE
                txtTitle.visibility = View.GONE
                sendGaLink(it.title)
                HelpDetailsFragment.create(it).addWithAnimation(supportFragmentManager, R.id.view_frame_content, false)
            }

            helpMain.addInFrame(supportFragmentManager, R.id.view_frame_content)
        }
    }

    override fun showLoading() {
        (isAttached())
        progress_main.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        (isAttached())
        progress_main.visibility = View.GONE
    }
    //endregion

    //region Local Functions

    private fun exitHelp() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun configBackButton() {
        btnLeft.setOnClickListener {
            onBackPressed()
        }
    }

    private fun configExitButton() {
        btnRight.setOnClickListener {
            exitHelp()
        }
    }

    private fun configIntentExtra() {
        intent?.extras?.let {
            val title = it.getString(HELP_TITLE)
            title.let {
                txtTitle.text = title
            }

            val help = it.getString(HELP_ID)
            help?.let {
                mIdHelp = it
            }
        }
    }

    private fun configUpdateButton() {
        buttonUpdate.setOnClickListener {
            showLoading()
            include_error.visibility = View.GONE
            presenter.loadHelps(mIdHelp)
        }
    }


    private fun configInitScreen() {
        btnLeft.visibility = View.GONE

        presenter.setView(this)
        presenter.loadHelps(mIdHelp)
    }

    //endregion


    private fun sendGaLink(label: String) {
        if (mIdHelp == PPL_HELP_ID)
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, "central de ajuda"),
                action = listOf("clique:pagamento por link"),
                label = listOf(label)
            )
    }
}