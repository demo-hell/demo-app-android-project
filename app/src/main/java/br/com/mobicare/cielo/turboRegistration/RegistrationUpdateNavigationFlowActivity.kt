package br.com.mobicare.cielo.turboRegistration

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.THREE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.RegistrationUpdateFlowActivityBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationUpdateNavigationFlowActivity : BaseLoggedActivity(), CieloNavigation {

    private var navigationListener: CieloNavigationListener? = null
    private var _binding: RegistrationUpdateFlowActivityBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: RegistrationUpdateViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RegistrationUpdateFlowActivityBinding.inflate(layoutInflater).also { _binding = it }
        setContentView(binding.root)
        viewModel

        val destination = when (UserPreferences.getInstance().turboRegistrationErrorStep) {
            RegistrationStepError.UNDEFINED.ordinal -> R.id.nav_address
            RegistrationStepError.ADDRESS.ordinal -> R.id.nav_address
            RegistrationStepError.MONTHLY_INCOME.ordinal -> R.id.nav_monthly_income
            RegistrationStepError.BUSINESS_SECTOR.ordinal -> R.id.nav_line_business
            RegistrationStepError.BANK.ordinal -> R.id.nav_bank_data
            else -> R.id.nav_address
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostFragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_registration_update)
        navGraph.setStartDestination(destination)
        navController.setGraph(navGraph, null)

        changeStatusBarColor()
    }

    override fun showBackButton(isShow: Boolean) {
        binding.backIcon.visible(isShow)
    }

    override fun setupToolbar(title: String, isCollapsed: Boolean, subtitle: String?) {
        binding.apply {
            registrationUpdateTitle.text = title
            if (!subtitle.isNullOrEmpty()) {
                root.getConstraintSet(R.id.start)?.let { startConstraintSet ->
                    startConstraintSet.visible(registrationUpdateSubtitle.id)
                }
                registrationUpdateSubtitle.text = subtitle
            } else {
                root.getConstraintSet(R.id.start)?.let { startConstraintSet ->
                    startConstraintSet.gone(registrationUpdateSubtitle.id)
                }
            }
            backIcon.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            if (isCollapsed) {
                root.transitionToEnd()
            } else {
                root.transitionToStart()
            }
        }
    }

    override fun onAdjustSoftInput(softInputMode: Int) {
        super.onAdjustSoftInput(softInputMode)
        window.setSoftInputMode(softInputMode)
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun onDestroy() {
        super.onDestroy()
        navigationListener = null
        _binding = null
    }

    override fun onStepChanged(currentStep: Int, showNumber: Boolean) {
        super.onStepChanged(currentStep, showNumber)

        val steps = if (UserPreferences.getInstance().isLegalEntity) THREE else FOUR
        if (currentStep < ONE) {
            binding.pbSteps.gone()
            binding.currentStep.gone()
        } else if (currentStep <= steps) {
            binding.pbSteps.visible()
            binding.currentStep.visible(showNumber)
            binding.currentStep.text = getString(R.string.step, currentStep.toString(), steps.toString())
            binding.pbSteps.progress = (currentStep * ONE_HUNDRED) / steps
        }
    }
}