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
import br.com.mobicare.cielo.databinding.FragmentMigrationOfferConfirmationBinding
import br.com.mobicare.cielo.databinding.LayoutMigrationFooterBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationConfirmationState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationOfferState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.MigrationOfferViewModel
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.RaD1MigrationEffectiveTimeViewModel
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MigrationOfferConfirmationFragment : BaseFragment() {

    private val viewModel: MigrationOfferViewModel by sharedViewModel()
    private val effectiveTimeViewModel: RaD1MigrationEffectiveTimeViewModel by sharedViewModel()

    private var binding: FragmentMigrationOfferConfirmationBinding? = null
    private var footerBinding: LayoutMigrationFooterBinding? = null

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       LayoutMigrationFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentMigrationOfferConfirmationBinding.inflate(inflater, container, false).apply {
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
                text = getString(R.string.receive_auto_hire_plan)
                setOnClickListener {
                    viewModel.postContractUserDecision(true)
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
                UiMigrationConfirmationState.AcceptSuccess -> showSuccess()
                is UiMigrationConfirmationState.Error -> showError()
                UiMigrationConfirmationState.RejectSuccess -> {}
            }
        }
    }

    private fun showError() {
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_10_erro,
            buttonLabel = R.string.entendi,
            messageParameter = effectiveTimeViewModel.effectiveTimeLiveData.value
        )

    }

    private fun showSuccess() {
        showBottomSheetGoToHome(
            title = R.string.receive_auto_migration_success_title,
            message = R.string.receive_auto_migration_success_message,
            messageParameter = effectiveTimeViewModel.effectiveTimeLiveData.value,
            image = R.drawable.img_14_estrelas,
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
        @DrawableRes image: Int,
        @StringRes buttonLabel: Int,
        messageParameter: String?,
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