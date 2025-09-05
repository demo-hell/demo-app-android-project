package br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.main.domain.MenuTarget
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.activity_balcao_recebiveis_extrato.*

class BalcaoRecebiveisExtratoActivity : BaseLoggedActivity(), CieloNavigation {

    private val negotiation: Negotiations? by lazy { intent?.extras?.getParcelable(RECEBIVEIS_ARGS) }
    private val dateInit: String? by lazy { intent?.extras?.getString(DATE_INIT_ARGS) }
    private val dateEnd: String? by lazy { intent?.extras?.getString(DATE_END_ARGS) }
    private var bundle: Bundle? = null
    private var cieloNavigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balcao_recebiveis_extrato)
        setupToolbar(toolbarRecebiveisDetail as Toolbar, "")

        bundle = savedInstanceState
        setNavigationGraph()
    }

    private fun setNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_balcao_recebiveis)
        navGraph.setStartDestination(R.id.fluxoInicialExtratoDetail)

        val bundle = bundleOf(
            NEGOTIATIONS_ARGS to negotiation,
            DATE_INIT_ARGS to dateInit,
            DATE_END_ARGS to dateEnd
        )
        navController.setGraph(
            navGraph,
            bundle
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()) {
            this.finish()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        this.bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
    }

    override fun showLoading(isShow: Boolean) {
        this.progressView?.visible(isShow)
        if (isShow) {
            this.errorView?.gone()
            this.mainContentLayout?.gone()
        }
    }

    override fun showContent(isShow: Boolean) {
        this.errorView?.gone()
        this.progressView?.gone()
        this.mainContentLayout?.visible()
    }

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView?.text = title
        }
    }

    override fun showError(error: ErrorMessage?) {
        hideSoftKeyboard()
        mainContentLayout.gone()
        progressView.gone()
        errorView.visible()
        errorView.cieloErrorMessage = getString(R.string.text_message_generic_error)
        errorView.errorButton?.setText(getString(R.string.text_try_again_label))
        errorView.cieloErrorTitle = getString(R.string.text_title_generic_error)
        errorView.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
        errorView.configureActionClickListener {
            cieloNavigationListener?.onRetry()
        }
    }

    override fun showError(
        title: String,
        message: String,
        textButton: String,
        idRes: Int,
        listener: View.OnClickListener?
    ) {
        hideSoftKeyboard()
        mainContentLayout.gone()
        progressView.gone()
        errorView.visible()
        errorView.cieloErrorTitle = title
        errorView.cieloErrorMessage = message
        errorView.errorButton?.setText(textButton)
        errorView.errorHandlerCieloViewImageDrawable = idRes
        listener?.let {
            errorView.configureActionClickListener(it)
        }
    }

    override fun showAlert(title: String?, message: String) {
        AlertDialogCustom.Builder(this, title)
            .setMessage(message)
            .setBtnRight(getString(android.R.string.ok))
            .show()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.cieloNavigationListener = listener
    }

    override fun showIneligibleUser(message: String) {
        this.showError(
            getString(R.string.text_title_service_unavailable),
            message,
            getString(R.string.ok),
            R.drawable.img_ineligible_user
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (this.isShowHelpMenu) {
            menuInflater.inflate(R.menu.menu_common_faq, menu)
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                Router.navigateTo(this, br.com.mobicare.cielo.main.domain.Menu(
                    Router.APP_ANDROID_HELP_CENTER, "", listOf(),
                    getString(R.string.text_body_dirf_02), false, "",
                    listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                        false,
                        type = "", mail = "", url = ""
                    )
                ), object : Router.OnRouterActionListener {

                    override fun actionNotFound(action: br.com.mobicare.cielo.main.domain.Menu) {
                        FirebaseCrashlytics
                            .getInstance()
                            .recordException(Throwable("Acao não encontrada || action_help"))
                    }

                })
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }


    override fun onBackPressed() {
        this.cieloNavigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setTextButton(text: String) {}
    override fun showButton(isShow: Boolean) {}
    override fun enableButton(isEnabled: Boolean) {}
    override fun saveData(bundle: Bundle) {}
    override fun getSavedData() = this.bundle
    override fun showMFAStatusPending() {}
    override fun showMFAStatusErrorPennyDrop() {}

    companion object {
        private const val RECEBIVEIS_ARGS = "RECEBIVEIS_ARGS"
        const val NEGOTIATIONS_ARGS = "NegotiationsArgs"
        const val INITIAL_DATE_ARGS = "INITIAL_DATE_ARGS"
        const val FINAL_DATE_ARGS = "FINAL_DATE_ARGS"
        const val TYPE_NEGOCIATION = "TYPE_NEGOCIATION"
        const val NEGOTIATIONS_ITEMS_ARGS = "NegotiationsItemsArgs"
        const val DATE_INIT_ARGS = "DATE_INIT_ARGS"
        const val DATE_END_ARGS = "DATE_END_ARGS"

        fun start(context: Context, dateInit: String, dateEnd: String, negotiation: Negotiations?) {
            val intent = Intent(context, BalcaoRecebiveisExtratoActivity::class.java).apply {
                putExtra(RECEBIVEIS_ARGS, negotiation)
                putExtra(DATE_INIT_ARGS, dateInit)
                putExtra(DATE_END_ARGS, dateEnd)
            }
            context.startActivity(intent)
        }
    }
}