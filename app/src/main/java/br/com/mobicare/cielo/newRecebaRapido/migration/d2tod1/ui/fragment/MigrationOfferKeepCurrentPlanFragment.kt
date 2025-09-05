package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.databinding.FragmentMigrationOfferRefuseBinding
import br.com.mobicare.cielo.databinding.LayoutMigrationFooterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationConfirmationState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationOfferState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.MigrationOfferViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MigrationOfferKeepCurrentPlanFragment : BaseFragment() {

    private val viewModel: MigrationOfferViewModel by sharedViewModel()

    private var binding: FragmentMigrationOfferRefuseBinding? = null
    private var footerBinding: LayoutMigrationFooterBinding? = null

    private var navigation: CieloNavigation? = null

    @StringRes private var planConfigurationString: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       LayoutMigrationFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentMigrationOfferRefuseBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupValues()
        setupObservers()
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
                        title = getString(R.string.receive_auto_title),
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

    private fun setupValues() {
        viewModel.migrationOfferState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UiMigrationOfferState.Both -> {
                    setupMessages(R.string.receive_auto_fees_bs_title_cash_and_installment)
                }
                is UiMigrationOfferState.Credit -> {
                    setupMessages(R.string.receive_auto_fees_bs_title_installment)
                }
                is UiMigrationOfferState.Installment -> {
                    setupMessages(R.string.receive_auto_fees_bs_title_installment)
                }
                UiMigrationOfferState.NoOfferError -> {
                    setupMessages(null)
                }
            }
        }
    }

    private fun setupMessages(planConfigurationMessage: Int?) {
        binding?.tvMessage?.apply {
            planConfigurationMessage?.let {
                text = getString(R.string.receive_automatic_keep_plan_message, getString(it))
            } ?: gone()
        }

        planConfigurationString = planConfigurationMessage

    }

    private fun setupListeners() {
        footerBinding?.apply {
            btIDoWant.apply {
                text = getString(R.string.receive_auto_migration_keep_plan_button_label)
                setOnClickListener {
                    viewModel.postContractUserDecision(false)
                }
            }
            btIDoNotWant.apply {
                text = getString(R.string.label_back)
                setOnClickListener {
                    doWhenResumed { findNavController().navigateUp() }
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

    private fun setupObservers() {
        viewModel.migrationConfirmationState.observe(viewLifecycleOwner) { state ->
            when(state) {
                UiMigrationConfirmationState.ShowLoading -> showLoading()
                UiMigrationConfirmationState.HideLoading -> hideLoading()
                UiMigrationConfirmationState.AcceptSuccess -> {}
                is UiMigrationConfirmationState.Error -> showError()
                UiMigrationConfirmationState.RejectSuccess -> showSuccess()
            }
        }
    }

    private fun showError() {
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_10_erro,
            buttonLabel = R.string.entendi
        )

    }

    private fun showSuccess() {
        showBottomSheetGoToHome(
            title = R.string.receive_auto_migration_refuse_success_title,
            message = R.string.receive_auto_migration_refuse_success_message,
            messageParameter = planConfigurationString?.let { getString(it) },
            image = R.drawable.img_32_ok,
            buttonLabel = R.string.migration_button_conclude
        )
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun showBottomSheetGoToHome(
        @StringRes title: Int,
        @StringRes message: Int,
        messageParameter: String? = null,
        @DrawableRes image: Int,
         buttonLabel: Int,
    ) {
        navigation?.showCustomHandlerView(
            title = getString(title),
            message = getString(message, messageParameter),
            contentImage = image,
            labelSecondButton = getString(buttonLabel),
            callbackSecondButton = {
                goToHome()
            },
            callbackBack = ::goToHome,
            isShowButtonClose = true,
            callbackClose = ::goToHome
        )
    }

    private fun goToHome() {
        requireActivity().finish()
    }

}