package br.com.mobicare.cielo.recebaMais.presentation.ui.activity

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
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_receba_mais.*

class FluxoNavegacaoRecebaMaisActivity : BaseLoggedActivity(), CieloNavigation {

    private var cieloNavigationListener: CieloNavigationListener? = null
    private var bundle: Bundle? = null
    private var isShowHelpMenu: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fluxo_navegacao_receba_mais)
        setup()
        bundle = savedInstanceState
    }

    private fun setup() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_receba_mais, intent.extras)

        setupToolbar(toolbar as Toolbar, "")
        configureListeners()

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

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) {
            val toolbarTitleTextView = findViewById<AppCompatTextView>(R.id.textToolbarMainTitle)
            toolbarTitleTextView?.text = title
        }
    }

    private fun configureListeners() {
        this.nextButton?.setOnClickListener {
            this.cieloNavigationListener?.onButtonClicked(nextButton.getText())
        }
    }

    override fun onBackPressed() {
        this.cieloNavigationListener?.onBackButtonClicked()
        super.onBackPressed()
    }

    override fun setTextButton(text: String) {
        this.nextButton?.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        this.cieloNavigationListener = listener
    }

    override fun showButton(isShow: Boolean) {
        this.buttonLayout?.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        this.nextButton?.isEnabled = isEnabled
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

    override fun showError(error: ErrorMessage?) {
        this.hideSoftKeyboard()
        this.mainContentLayout?.gone()
        this.progressView?.gone()
        this.errorView?.visible()
        this.errorView?.cieloErrorMessage = getString(R.string.text_message_generic_error)
        this.errorView?.errorButton?.setText(getString(R.string.text_try_again_label))
        this.errorView?.cieloErrorTitle = getString(R.string.text_title_generic_error)
        this.errorView?.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
        this.errorView?.configureActionClickListener {
            this.cieloNavigationListener?.onRetry()
        }
    }

    override fun showAlert(title: String?, message: String) {
        AlertDialogCustom.Builder(this, "")
                .setMessage(message)
                .setBtnRight(getString(android.R.string.ok))
                .show()
    }

    override fun saveData(bundle: Bundle) {
        this.bundle = bundle
    }

    override fun getSavedData() = this.bundle

    override fun showError(title: String, message: String, textButton: String, idRes: Int, listener: View.OnClickListener?) {
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

    override fun showIneligibleUser(message: String) {
        this.showError(
                getString(R.string.text_title_service_unavailable),
                message,
                getString(R.string.ok),
                R.drawable.img_ineligible_user
        )
    }

    override fun showHelpButton(isShow: Boolean) {
        this.isShowHelpMenu = isShow
        this.invalidateOptionsMenu()
    }



    override fun showMFAStatusPending() = Unit

    override fun showMFAStatusErrorPennyDrop() = Unit
}