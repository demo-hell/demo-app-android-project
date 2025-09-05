package br.com.mobicare.cielo.mfa.merchantstatus

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics.Companion.USER_NOT_ELEGIBLE
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_merchant_status_mfa.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject

class FluxoNavegacaoMerchantStatusMfaActivity : BaseLoggedActivity(), CieloNavigation {

    companion object {
        const val MERCHANT_STATUS_MFA = "MERCHANT_STATUS_MFA"
    }

    private var cieloNavigationListener: CieloNavigationListener? = null
    private var isShowHelpMenu: Boolean = true
    private var merchantStatusMFA: String? = null

    private val analytics: MfaAnalytics by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fluxo_navegacao_merchant_status_mfa)
        init()
    }

    private fun init() {
        setupToolbar(toolbar as Toolbar, "")
        merchantStatusMFA = intent.extras?.getString(MERCHANT_STATUS_MFA)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_merchant_status_mfa, intent.extras)
        configureListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (!Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp()) {
            this.finish()
        }
        return true
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
                this.cieloNavigationListener?.onHelpButtonClicked()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setTextButton(text: String) {
        nextButton.setText(text)
    }

    override fun onBackPressed() {
        this.cieloNavigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }


    override fun showButton(isShow: Boolean) {
        this.buttonLayout?.visible(isShow)
    }

    override fun showLoading(isShow: Boolean) {
        this.progressView?.visible(isShow)
        if (isShow) {
            this.errorView?.gone()
            this.mainContentLayout?.gone()
        }
    }

    override fun showContent(isShow: Boolean) {
        this.showHelpButton(true)
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

    override fun enableButton(isEnabled: Boolean) {
        this.nextButton?.isEnabled = isEnabled
    }

    private fun configureListeners() {
        this.nextButton?.setOnClickListener {
            this.cieloNavigationListener?.onButtonClicked()
        }
    }

    override fun showError(error: ErrorMessage?) {
        this.showHelpButton(false)
        this.hideSoftKeyboard()

        mainContentLayout?.gone()
        progressView?.gone()
        errorView?.visible()

        this.errorView?.configureActionClickListener {
            this.cieloNavigationListener?.onRetry()
        }
    }

    override fun showError(title: String, message: String, textButton: String, idRes: Int, listener: View.OnClickListener?) {
        this.showHelpButton(false)
        this.hideSoftKeyboard()
        this.mainContentLayout?.gone()
        this.progressView?.gone()
        this.errorView?.visible()
        this.errorView?.cieloErrorTitle = title
        this.errorView?.cieloErrorMessage = message
        this.errorView?.errorButton?.setText(textButton)
        this.errorView?.errorHandlerCieloViewImageDrawable = idRes
        listener?.let {
            this.errorView?.configureActionClickListener(it)
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.cieloNavigationListener = listener
    }

    override fun showIneligibleUser(message: String) {
        analytics.logMFACallbackError(null, USER_NOT_ELEGIBLE)
        analytics.logMFAShowBottomSheet(USER_NOT_ELEGIBLE)
        this.showError(
            getString(R.string.text_title_service_unavailable),
            getString(R.string.text_message_service_ineligible),
            getString(R.string.text_lgpd_saiba_mais),
            R.drawable.img_ineligible_user,
            listener = View.OnClickListener {
                analytics.logMFAClickBottomSheet(
                    USER_NOT_ELEGIBLE,
                    getString(R.string.text_lgpd_saiba_mais)
                )
                startHelpCenter()
            }
        )
    }

    override fun showMFAStatusPending() {
        this.showError(
                getString(R.string.text_mfa_status_pending_title),
                getString(R.string.text_mfa_status_pending_subtitle),
                getString(R.string.ok),
                R.drawable.ic_37,
                listener = View.OnClickListener {
                    this.finish()
                }
        )
    }

    override fun showMFAStatusErrorPennyDrop() {
        this.showError(
                getString(R.string.text_mfa_status_error_penny_drop_title),
                getString(R.string.text_mfa_status_error_penny_drop_subtitle),
                getString(R.string.text_lgpd_saiba_mais),
                R.drawable.ic_42,
                listener = View.OnClickListener {
                    startHelpCenter()
                }
        )
    }

    private fun startHelpCenter(){
        startActivity<CentralAjudaSubCategoriasEngineActivity>(
                ConfigurationDef.TAG_KEY_HELP_CENTER to ConfigurationDef.TAG_HELP_CENTER_MFA,
                ARG_PARAM_SUBCATEGORY_NAME to getString(R.string.text_token),
                CentralAjudaSubCategoriasEngineActivity.NOT_CAME_FROM_HELP_CENTER to true)
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }



    override fun getSavedData(): Bundle? = null
    override fun showAlert(title: String?, message: String) = Unit
    override fun saveData(bundle: Bundle) = Unit
}