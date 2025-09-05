package br.com.mobicare.cielo.openFinance.presentation.manager.newShare

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.stepper.model.Step
import br.com.cielo.libflue.stepper.util.StatusStep
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.OpenFinanceFlowNewShareActivityBinding
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.TYPE_SHARE

class OpenFinanceFlowNewShareActivity : BaseLoggedActivity(), CieloNavigation {
    private var navigationListener: CieloNavigationListener? = null
    private var _binding: OpenFinanceFlowNewShareActivityBinding? = null
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OpenFinanceFlowNewShareActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initializeCollapsingToolbarLayout()
        checkFlowType()
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }

    override fun configureCollapsingToolbar(configurator: CieloCollapsingToolbarLayout.Configurator) {
        binding?.cieloCollapsingToolbarLayout?.configure(configurator)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return binding?.cieloCollapsingToolbarLayout?.onCreateOptionsMenu(menu)
            ?: super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return binding?.cieloCollapsingToolbarLayout?.onOptionsItemSelected(item)
            ?: super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeCollapsingToolbarLayout() {
        binding?.cieloCollapsingToolbarLayout?.initialize(this)
    }

    private fun checkFlowType() {
        when (intent.getIntExtra(TYPE_SHARE, ZERO)) {
            ZERO -> {
                binding?.compSteps?.defineSteps(listStepsNewShare)
            }
            else -> {
                binding?.compSteps?.defineSteps(listStepsChangeOrRenew)
            }
        }
    }

    private val listStepsNewShare = listOf(
        Step(
            ONE,
            "Instituição financeira",
            R.layout.open_finance_manager_fragment,
            StatusStep.ACTIVE
        ),
        Step(
            TWO,
            "Revisão dos dados",
            R.layout.open_finance_new_share_fragment,
            StatusStep.UNCOMPLETED
        ),
        Step(
            THREE,
            "Redirecionamento",
            R.layout.open_finance_redirect_fragment,
            StatusStep.UNCOMPLETED
        ),
        Step(
            FOUR,
            "Conclusão",
            R.layout.open_finance_conclusion_fragment,
            StatusStep.UNCOMPLETED
        )
    )

    private val listStepsChangeOrRenew = listOf(
        Step(
            ONE,
            "Revisão dos dados",
            R.layout.open_finance_new_share_fragment,
            StatusStep.ACTIVE
        ),
        Step(
            TWO,
            "Redirecionamento",
            R.layout.open_finance_redirect_fragment,
            StatusStep.UNCOMPLETED
        ),
        Step(
            THREE,
            "Conclusão",
            R.layout.open_finance_conclusion_fragment,
            StatusStep.UNCOMPLETED
        )
    )
}