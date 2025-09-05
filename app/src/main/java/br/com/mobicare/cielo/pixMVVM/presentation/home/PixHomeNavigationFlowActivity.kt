package br.com.mobicare.cielo.pixMVVM.presentation.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.parcelable
import br.com.mobicare.cielo.databinding.ActivityPixHomeNavigationFlowBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.setNavGraphStartDestination
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

class PixHomeNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {
    private var binding: ActivityPixHomeNavigationFlowBinding? = null
    private var bundle: Bundle? = null
    private var navigationListener: CieloNavigationListener? = null

    private val isOnBoardingViewed
        get() =
            intent.getBooleanExtra(
                NavArgs.IS_PIX_HOME_ONBOARDING_VIEWED,
                false,
            )

    val navArgsData: NavArgs.Data by lazy {
        NavArgs.Data(
            pixAccount = intent?.parcelable(NavArgs.PIX_ACCOUNT)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState
        binding = ActivityPixHomeNavigationFlowBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupNavGraph()
        initializeCollapsingToolbarLayout()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun getData() = navArgsData

    override fun onBackPressed() {
        val value = navigationListener?.onBackButtonClicked() ?: false
        if (value.not()) {
            returnToHome()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding?.cieloCollapsingToolbarLayout?.onCreateOptionsMenu(menu)
            ?: super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            binding?.cieloCollapsingToolbarLayout?.onOptionsItemSelected(item) ?: super.onOptionsItemSelected(item)
        }
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding?.cieloCollapsingToolbarLayout?.configure(configurator)
    }

    private fun setupNavGraph() {
        setNavGraphStartDestination(
            navHostFragmentId = R.id.nav_host_fragment,
            navGraphId = R.navigation.nav_graph_pix_home,
            startDestinationId = if (isOnBoardingViewed) R.id.pixHomeFragment else R.id.pixOnboardingHomeFragment,
        )
    }

    private fun initializeCollapsingToolbarLayout() {
        binding?.cieloCollapsingToolbarLayout?.initialize(this)
    }

    private fun returnToHome() {
        backToHome()
        finishAndRemoveTask()
    }

    object NavArgs {
        const val IS_PIX_HOME_ONBOARDING_VIEWED = "IS_PIX_HOME_ONBOARDING_VIEWED"
        const val PIX_ACCOUNT = "PIX_ACCOUNT"

        data class Data(
            val pixAccount: OnBoardingFulfillment.PixAccount?
        )
    }

}
