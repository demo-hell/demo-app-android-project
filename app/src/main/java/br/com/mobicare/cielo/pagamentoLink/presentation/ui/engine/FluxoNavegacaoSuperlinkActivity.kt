package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.ActivityFluxoNavegacaoSuperlinkBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.superlink.utils.SuperLinkNavStartRouter
import org.koin.android.ext.android.inject

class FluxoNavegacaoSuperlinkActivity : BaseLoggedActivity(), CieloNavigation {

    private lateinit var binding: ActivityFluxoNavegacaoSuperlinkBinding

    private val navStartRouter: SuperLinkNavStartRouter by inject()

    private var navigationListener: CieloNavigationListener? = null
    private var bundle: Bundle? = null
    private var isShowHelpMenu: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFluxoNavegacaoSuperlinkBinding.inflate(layoutInflater)
        bundle = savedInstanceState

        setContentView(binding.root)
        setupToolbar(binding.toolbar.toolbarMain, EMPTY)

        setNavigationGraph()
        configureListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        Navigation.findNavController(this, R.id.nav_host_fragment).let { navController ->
            if (navController.navigateUp().not()) finish()
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        bundle?.let {
            outState.clear()
            outState.putAll(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isShowHelpMenu) menuInflater.inflate(R.menu.menu_common_faq, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> {
                navigationListener?.onHelpButtonClicked()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setTextToolbar(title: String) {
        if (title.isNotBlank()) binding.toolbar.textToolbarMainTitle.text = title
    }

    private fun setNavigationGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        intent?.getStringExtra(SuperLinkNavStartRouter.FlowStartArg.KEY)?.let {
            navStartRouter.setFlowStartOrigin(it)
        }

        navController.navInflater.inflate(R.navigation.nav_graph_superlink).let { navGraph ->
            navGraph.setStartDestination(navStartRouter.startDestinationResId)
            navController.graph = navGraph
        }
    }

    private fun configureListeners() {
        binding.nextButton.apply {
            setOnClickListener {
                navigationListener?.onButtonClicked(getText())
            }
        }
    }

    override fun onBackPressed() {
        if (navigationListener?.onBackButtonClicked() == false) super.onBackPressed()
    }

    override fun setTextButton(text: String) {
        binding.nextButton.setText(text)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun showButton(isShow: Boolean) {
        binding.buttonLayout.visible(isShow)
    }

    override fun enableButton(isEnabled: Boolean) {
        binding.nextButton.isEnabled = isEnabled
    }

    override fun showLoading(isShow: Boolean) {
        binding.apply {
            progressView.visible(isShow)
            if (isShow) {
                errorView.gone()
                mainContentLayout.gone()
            }
        }
    }

    override fun showContent(isShow: Boolean) {
        binding.apply {
            errorView.gone()
            progressView.gone()
            mainContentLayout.visible()
        }
    }

    override fun showError(error: ErrorMessage?) {
        hideSoftKeyboard()
        binding.apply {
            mainContentLayout.gone()
            progressView.gone()
            errorView.visible()
            errorView.cieloErrorMessage = getString(R.string.text_message_generic_error)
            errorView.errorButton?.setText(getString(R.string.text_try_again_label))
            errorView.cieloErrorTitle = getString(R.string.text_title_generic_error)
            errorView.errorHandlerCieloViewImageDrawable = R.drawable.ic_generic_error_image
            errorView.configureActionClickListener {
                navigationListener?.onRetry()
            }
        }
    }

    override fun showAlert(title: String?, message: String) {
        AlertDialogCustom.Builder(this, EMPTY)
                .setMessage(message)
                .setBtnRight(getString(android.R.string.ok))
                .show()
    }

    override fun saveData(bundle: Bundle) {
        this.bundle = bundle
    }

    override fun getSavedData() = bundle

    override fun getDataIntent(): Intent? = intent

    override fun showError(title: String, message: String, textButton: String, idRes: Int, listener: View.OnClickListener?) {
        hideSoftKeyboard()
        binding.apply {
            mainContentLayout.gone()
            progressView.gone()
            errorView.visible()
            errorView.cieloErrorTitle = title
            errorView.cieloErrorMessage = message
            errorView.errorButton?.setText(textButton)
            errorView.errorHandlerCieloViewImageDrawable = idRes
            listener?.let { errorView.configureActionClickListener(it) }
        }
    }

    override fun showIneligibleUser(message: String) {
        showError(
            getString(R.string.text_title_service_unavailable),
            message,
            getString(R.string.ok),
            R.drawable.img_ineligible_user
        )
    }

    override fun showHelpButton(isShow: Boolean) {
        isShowHelpMenu = isShow
        invalidateOptionsMenu()
    }

}