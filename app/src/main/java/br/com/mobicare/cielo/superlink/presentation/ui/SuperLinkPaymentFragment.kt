package br.com.mobicare.cielo.superlink.presentation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.HOME_FAZER_UMA_VENDA_VIEW
import br.com.mobicare.cielo.commons.analytics.Label.CRIAR_NOVO_LINK
import br.com.mobicare.cielo.commons.constants.HelpCenter.HELP_CENTER
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.SEPARATOR
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.RouterActionsFragment
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.ui.widget.CallToActionView
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.databinding.FragmentSuperLinkPaymentBinding
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivosResumo.PaymentLinkListAtivosResumoFragment
import br.com.mobicare.cielo.posVirtual.presentation.PosVirtualNavigationFlowActivity
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkAF
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.NEW_PAYMENT
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SCREEN_VIEW_PAYMENT_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LINK_PAYMENT_SCREEN
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.NOT_ELIGIBLE_SCREEN
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.SOLICITAR_AGORA
import br.com.mobicare.cielo.superlink.presentation.viewmodel.SuperLinkViewModel
import br.com.mobicare.cielo.superlink.utils.UiSuperLinkState
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SuperLinkPaymentFragment : BaseFragment(), CieloNavigationListener {
    private var navigation: CieloNavigation? = null

    private val viewModel: SuperLinkViewModel by viewModel()
    private val analytics: SuperLinkAnalytics by inject()

    private var _binding: FragmentSuperLinkPaymentBinding? = null
    private val binding get() = _binding!!

    private val ga4: PaymentLinkGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentSuperLinkPaymentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupObservers()
        createLogScreen()
        verifyUserLinkEligibility()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().moveToHome()
        return true
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation =
                (requireActivity() as CieloNavigation).also {
                    it.setTextToolbar(getString(R.string.text_super_link))
                    it.showButton(false)
                    it.showHelpButton(true)
                    it.setNavigationListener(this)
                    it.showContent(true)
                }
        }
    }

    private fun setupObservers() {
        viewModel.paymentLinkActiveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiSuperLinkState.Loading -> showLoading()
                is UiSuperLinkState.ErrorNotEligible -> goToAccreditationView()
                is UiSuperLinkState.Error -> showError()
                is UiSuperLinkState.Success -> showCreateLink()
            }
        }
    }

    private fun setupListeners() {
        binding.callToActionNewLink.onCallToActionClickListener =
            object :
                CallToActionView.OnCallToActionClickListener {
                override fun onClick() {
                    if (isAttached()) {
                        logClickBtnCreateNewLink()
                        if (findNavController().currentDestination?.id == R.id.linkPaymentFragment) {
                            findNavController().navigate(
                                SuperLinkPaymentFragmentDirections
                                    .actionLinkPaymentFragmentToTipoVendaPagamentoPorLinkFragment(),
                            )
                        }
                    }
                }
            }

        binding.linearErrorRequest.buttonLoanSimulationErrorRetry.setOnClickListener {
            verifyUserLinkEligibility()
        }
    }

    private fun verifyUserLinkEligibility() = viewModel.isPaymentLinkActive()

    override fun onRetry() = verifyUserLinkEligibility()

    override fun onHelpButtonClicked() {
        if (isAttached()) {
            analytics.gaSendButtonTooltip(HELP_CENTER, PAGAMENTO_POR_LINK)
        }

        HelpMainActivity.create(
            requireActivity(),
            getString(R.string.text_pg_lk_help_title),
            PPL_HELP_ID,
        )
    }

    private fun showLoading() {
        navigation?.showLoading(true)
    }

    private fun hideLoading() {
        navigation?.apply {
            showLoading(false)
            showContent(true)
        }
    }

    private fun showError() {
        hideLoading()
        navigation?.showError(null)
    }

    private fun showCreateLink() {
        hideLoading()
        PaymentLinkListAtivosResumoFragment.create().addInFrame(
            childFragmentManager,
            R.id.frameLastActiveLinks,
        )
    }

    private fun goToAccreditationView() {
        hideLoading()
        if (isAttached()) {
            binding.linearLinkPaymentContent.gone()
            binding.linearErrorRequest.root.gone()
            binding.frameCustomerNotEligible.visible()
        }
        showAccreditationBottomSheet()
    }

    private fun showAccreditationBottomSheet() {
        analytics.apply {
            sendGaScreenView(NOT_ELIGIBLE_SCREEN)
            logShowBottomSheetNotEligible()
        }
        PaymentLinkAF.logAccreditationScreenView()

        HandlerViewBuilderFluiV2.Builder(requireContext()).apply {
            illustration = R.drawable.img_77_link_pagamento_servicos
            title = getString(R.string.superlink_not_eligible_title)
            message = getString(R.string.superlink_not_eligible_message)
            labelPrimaryButton = getString(R.string.superlink_not_eligible_label_primary_button)
            labelSecondaryButton = getString(R.string.superlink_not_eligible_label_secondary_button)

            onPrimaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        startBrowser()
                    }
                }

            onSecondaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        showAccreditationKnowMore()
                    }
                }

            onIconButtonEndHeaderClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        requireActivity().finish()
                    }
                }

            onDismiss = ::onDismissShowAccreditation
        }.build().show(childFragmentManager, tag)
    }

    private fun startBrowser() {
        analytics.logClickSuperLinkRequest(SOLICITAR_AGORA)
        Utils.openBrowser(requireActivity(), URL_AUTO_REGISTRATION_LINK_PAYMENT)
        requireActivity().finish()
    }

    private fun onDismissShowAccreditation(dialog: Dialog?) {
        requireActivity().finish()
    }

    private fun showAccreditationKnowMore() {
        HandlerViewBuilderFluiV2.Builder(requireContext()).apply {
            illustration = R.drawable.img_152_vendas
            title = getString(R.string.payment_link_accreditation_know_more_title)
            message = getString(R.string.payment_link_accreditation_know_more_message)
            labelPrimaryButton =
                getString(R.string.payment_link_accreditation_know_more_label_primary_button)
            isShowBackButton = true

            onPrimaryButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        onAccessCieloTapClicked()
                    }
                }

            onBackButtonClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                    }
                }

            onIconButtonEndHeaderClickListener =
                object : HandlerViewBuilderFluiV2.HandlerViewListener {
                    override fun onClick(dialog: Dialog?) {
                        dialog?.dismiss()
                    }
                }
        }.build().show(childFragmentManager, tag)
    }

    private fun onAccessCieloTapClicked() {
        requireActivity().apply {
            startActivity<PosVirtualNavigationFlowActivity>()
            finish()
        }
    }

    private fun createLogScreen() {
        listOf(
            HOME_FAZER_UMA_VENDA_VIEW,
            RouterActionsFragment.SUPER_LINK
                .toLowerCasePTBR()
                .replace(ONE_SPACE, SIMPLE_LINE),
        ).joinToString(SEPARATOR).let { screenName ->
            analytics.sendGaNewPaymentButton(screenName)
        }
    }

    private fun logScreenView() {
        analytics.sendGaScreenView(LINK_PAYMENT_SCREEN)
        ga4.logScreenView(SCREEN_VIEW_PAYMENT_LINK)
    }

    private fun logClickBtnCreateNewLink() {
        analytics.sendGaNewPaymentButton(CRIAR_NOVO_LINK)
        ga4.logClickButtonHome(CRIAR_NOVO_LINK, NEW_PAYMENT)
    }

    companion object {
        const val URL_AUTO_REGISTRATION_LINK_PAYMENT = "https://onboarding.cielo.com.br/app-gestao"
    }
}
