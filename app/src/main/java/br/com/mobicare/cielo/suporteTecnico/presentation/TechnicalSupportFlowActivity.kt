package br.com.mobicare.cielo.suporteTecnico.presentation

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.Navigation
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.ActivityTechnicalSupportFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import com.google.firebase.crashlytics.FirebaseCrashlytics

class TechnicalSupportFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var _binding: ActivityTechnicalSupportFlowBinding? = null
    private val binding get() = _binding
    private var navigation: CieloNavigationListener? = null
    private var isBack = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTechnicalSupportFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onSupportNavigateUp(): Boolean {
        if (Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp().not())
            this.finish()

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> null
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }

        return true
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigation = listener
    }

    override fun goToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    override fun startHelpCenter(tagKey: String) {
        Router.navigateTo(this, Menu(
            Router.APP_ANDROID_HELP_CENTER, EMPTY, listOf(),
            getString(R.string.text_body_dirf_02), false, EMPTY,
            listOf(), show = false, showItems = false, menuTarget = MenuTarget(
                false,
                type = EMPTY, mail = EMPTY, url = EMPTY
            )
        ), object : Router.OnRouterActionListener {

            override fun actionNotFound(action: Menu) {
                FirebaseCrashlytics
                    .getInstance()
                    .recordException(Throwable(HomeAnalytics.ERROR_ON_OPEN_HELP_CENTER_TAG))
            }
        })
    }

    override fun setupToolbar(
        title: String,
        isCollapsed: Boolean,
    ) {
        binding?.apply {
            imvBack.setOnClickListener {
                navigation?.onBackPressed()
                onBackPressed()
            }
            imvHelp.setOnClickListener {
                startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_ARV)
                navigation?.onHelpButtonClicked()
            }
            imvClose.setOnClickListener {
                navigation?.onCloseButtonClicked()
            }
        }
    }

    override fun showHelpButton(isShow: Boolean) {
        binding?.imvHelp.visible(isShow)
    }

    override fun showCloseButton(show: Boolean) {
        binding?.imvClose.visible(show)
    }

    override fun showBackButton(isShow: Boolean) {
        binding?.imvBack.visible(isShow)
    }

    override fun showToolbar(isShow: Boolean) {
        binding?.apply {
            if (isShow)
                containerToolbar.visible()
            else
                containerToolbar.gone()
        }
    }
}