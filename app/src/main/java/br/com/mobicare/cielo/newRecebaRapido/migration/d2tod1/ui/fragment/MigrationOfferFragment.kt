package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.databinding.FragmentMigrationOfferBinding
import br.com.mobicare.cielo.databinding.LayoutMigrationFooterBinding
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.MigrationD2toD1NavigationFlowActivity
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationOfferState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.MigrationOfferViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MigrationOfferFragment : BaseFragment() {

    private val viewModel: MigrationOfferViewModel by sharedViewModel()

    private var binding: FragmentMigrationOfferBinding? = null
    private var footerBinding: LayoutMigrationFooterBinding? = null

    private var navigation: CieloNavigation? = null

    private val flowData by lazy { (navigation as? MigrationD2toD1NavigationFlowActivity)?.getData() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       LayoutMigrationFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentMigrationOfferBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupVM()
        setupListeners()
        setupValues()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.receive_auto_migration_plan_change),
                        showBackButton = true,
                        menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_help,
                            onOptionsItemSelected = ::onMenuOptionSelected
                        )
                    ),
                    footerView = footerBinding?.root
                )
            )
        }
    }

    private fun setupVM() {
        flowData?.let {
            viewModel.updateMigrationOfferState(it.migrationOffer)
        }
    }

    private fun setupValues() {
        viewModel.migrationOfferState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiMigrationOfferState.Both -> {
                    setupCredit(state.creditOffer)
                    setupInstallment(state.installmentOffer)
                }
                is UiMigrationOfferState.Credit -> {
                    setupCredit(state.creditOffer)
                    setupInstallment(null)
                }
                is UiMigrationOfferState.Installment -> {
                    setupCredit(null)
                    setupInstallment(state.installmentOffer)
                }

                UiMigrationOfferState.NoOfferError -> {
                    setupCredit(null)
                    setupInstallment(null)
                }
            }
        }
    }

    private fun setupCredit(creditOffer: Double?) {
        binding?.tvCreditFee?.apply {
            creditOffer?.let {
                text = getString(R.string.receive_auto_migration_credit_fee, it.formatRate()).fromHtml()
            } ?: gone()
        }
    }

    private fun setupInstallment(installmentOffer: Double?) {
        binding?.tvInstallmentFee?.apply {
            installmentOffer?.let {
                text = getString(R.string.receive_auto_migration_installment_fee, it.formatRate()).fromHtml()
            } ?: gone()
        }
    }

    private fun setupListeners() {
        footerBinding?.apply {
            btIDoWant.apply {
                text = getString(R.string.receive_auto_migration_i_want_1_day)
                setOnClickListener {
                    findNavController().safeNavigate(
                        MigrationOfferFragmentDirections.actionMigrationOfferFragmentToMigrationOfferConfirmationFragment()
                    )
                }
            }
            btIDoNotWant.apply {
                text = getString(R.string.receive_auto_migration_keep_plan)
                setOnClickListener {
                    findNavController().safeNavigate(MigrationOfferFragmentDirections.actionMigrationOfferFragmentToMigrationOfferKeepCurrentPlanFragment())
                }
            }
        }
    }

    private fun onMenuOptionSelected(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.menuActionHelp) {
            requireActivity().openFaq(
                tag = ConfigurationDef.TAG_HELP_CENTER_RECEBIMENTO_AUTOMATICO,
                subCategoryName = EMPTY_VALUE,
                notCameFromHelpCenter = true
            )
        }
    }

}