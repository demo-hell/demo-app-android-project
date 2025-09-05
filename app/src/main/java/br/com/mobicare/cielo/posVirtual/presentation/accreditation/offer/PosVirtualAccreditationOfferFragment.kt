package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.cardbutton.CieloCardButton
import br.com.cielo.libflue.flextag.CieloFlexTag
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE_CALL_CENTER
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.FragmentPosVirtualAccreditationOfferBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.CONTENT_COMPONENT_BOTTOM_SHEET_GENERIC_TITLE
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualProductTypeEnum
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationState
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PosVirtualAccreditationOfferFragment :
    BaseFragment(),
    CieloNavigationListener {
    private val viewModel: PosVirtualAccreditationOfferViewModel by viewModel()

    private var binding: FragmentPosVirtualAccreditationOfferBinding? = null
    private var navigation: CieloNavigation? = null

    private val ga4: PosVirtualAnalytics by inject()
    private val screenPath: String get() = PosVirtualAnalytics.SCREEN_VIEW_ACCREDITATION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPosVirtualAccreditationOfferBinding
        .inflate(
            inflater,
            container,
            false,
        ).also { binding = it }
        .root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setMinimumHeight()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        viewModel.resume()
        setupNavigation()
        setupListeners()
        enableButtonContinue()
        logScreenView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                configureCollapsingToolbar(setupConfiguratorToolbar())
                showButton(false)
            }
        }
    }

    private fun setupConfiguratorToolbar(): CollapsingToolbarBaseActivity.Configurator =
        CollapsingToolbarBaseActivity.Configurator(
            toolbarTitle = getString(R.string.pos_virtual_accreditation_title),
            toolbarTitleAppearance =
                CollapsingToolbarBaseActivity.ToolbarTitleAppearance(
                    collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                    expanded = R.style.CollapsingToolbar_Expanded_BlackBold,
                ),
            toolbarMenu =
                CollapsingToolbarBaseActivity.ToolbarMenu(
                    menuRes = R.menu.menu_help,
                    onOptionsItemSelected = {
                        if (it.itemId == R.id.menuActionHelp) {
                            requireActivity().openFaq(
                                tag = ConfigurationDef.TAG_HELP_CENTER_POS_VIRTUAL,
                                subCategoryName = getString(R.string.pos_virtual),
                            )
                        }
                    },
                ),
        )

    private fun setupViewProducts() {
        setupViewCardCieloTap()
        setupViewCardSuperLink()
        setupViewCardQRCodePix()
    }

    private fun setupViewCardCieloTap() =
        binding?.apply {
            ccbCieloTap.apply {
                visible(viewModel.isShowCardCieloTap)
                firstSubtitle =
                    getString(
                        R.string.pos_virtual_accreditation_label_card_rates_debit,
                        viewModel.debitRateCieloTap,
                    )
                secondSubtitle =
                    getString(
                        R.string.pos_virtual_accreditation_label_card_rates_credit,
                        viewModel.creditRateCieloTap,
                    )
                setTagList(
                    listOf(
                        CieloCardButton.Tag(
                            icon = R.drawable.ic_cielo_machine_cielo_receba_rapido_success_500_16_dp,
                            text =
                                getString(
                                    if (viewModel.acceptAutomaticReceiverIsChecked.value == true) {
                                        R.string.pos_virtual_accreditation_label_tag_card_rates_receive_two_days
                                    } else {
                                        R.string.pos_virtual_accreditation_label_tag_card_rates_receive_thirty_days
                                    },
                                ),
                            tagType = CieloFlexTag.Type.SUCCESS,
                        ),
                    ),
                )
            }
        }

    private fun setupViewCardSuperLink() =
        binding?.apply {
            ccbSuperLink.apply {
                visible(viewModel.isShowCardSuperLink)
                firstSubtitle =
                    getString(
                        R.string.pos_virtual_accreditation_label_card_rates_debit,
                        viewModel.debitRateSuperLink,
                    )
                secondSubtitle =
                    getString(
                        R.string.pos_virtual_accreditation_label_card_rates_credit,
                        viewModel.creditRateSuperLink,
                    )
                setTagList(
                    listOf(
                        CieloCardButton.Tag(
                            icon = R.drawable.ic_cielo_machine_cielo_receba_rapido_success_500_16_dp,
                            text =
                                getString(
                                    if (viewModel.acceptAutomaticReceiverIsChecked.value == true) {
                                        R.string.pos_virtual_accreditation_label_tag_card_rates_receive_two_days_with_asterisk
                                    } else {
                                        R.string.pos_virtual_accreditation_label_tag_card_rates_receive_thirty_days
                                    },
                                ),
                            tagType = CieloFlexTag.Type.SUCCESS,
                        ),
                    ),
                )
            }
        }

    private fun setupViewCardQRCodePix() =
        binding?.apply {
            ccbQRCodePix.apply {
                visible(viewModel.isShowCardQRCodePix)
                firstSubtitle =
                    getString(
                        R.string.pos_virtual_accreditation_label_card_rates_rate,
                        viewModel.debitRateQRCodePix,
                    )
                setTagList(
                    listOf(
                        CieloCardButton.Tag(
                            icon = R.drawable.ic_cielo_machine_cielo_receba_rapido_success_500_16_dp,
                            text = getString(R.string.pos_virtual_accreditation_label_tag_card_rates_receive_on_time),
                            tagType = CieloFlexTag.Type.SUCCESS,
                        ),
                    ),
                )
            }
        }

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun navigateToRatesDetails(
        typeProduct: PosVirtualProductTypeEnum,
        labelButton: String,
    ) {
        doWhenResumed {
            viewModel.acceptAutomaticReceiverIsChecked.value?.let {
                logClickButton(typeProduct.labelGa4, labelButton)

                findNavController().safeNavigate(
                    PosVirtualAccreditationOfferFragmentDirections.actionPosVirtualAccreditationOfferToPosVirtualAccreditationRatesDetails(
                        it,
                        typeProduct,
                        viewModel.getBrands(typeProduct),
                    ),
                )
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            ccbCieloTap.setOnClickListener {
                navigateToRatesDetails(
                    PosVirtualProductTypeEnum.CIELO_TAP,
                    ccbCieloTap.labelButton.orEmpty(),
                )
            }

            ccbSuperLink.setOnClickListener {
                navigateToRatesDetails(
                    PosVirtualProductTypeEnum.PAYMENT_LINK,
                    ccbSuperLink.labelButton.orEmpty(),
                )
            }

            tvBtnReloadOffers.setOnClickListener { viewModel.reloadAccreditationOffer() }
            llAcceptAutomaticReceipt.setOnClickListener { onTapAcceptAutomaticReceiver() }
            btnContinue.setOnClickListener { navigateToAccreditationHire() }
        }
    }

    private fun onTapAcceptAutomaticReceiver() {
        if (viewModel.offerState.value is UIPosVirtualAccreditationState.Success) {
            viewModel.checkAutomaticReceiver()
            viewModel.reloadAccreditationOffer()
        }
    }

    private fun setupObservers() {
        setupObserveLoadingOfferState()
        setupObserverAcceptAutomaticReceiver()
    }

    private fun setupObserveLoadingOfferState() {
        viewModel.offerState.observe(viewLifecycleOwner) {
            when (it) {
                is UIPosVirtualAccreditationState.ShowLoading -> showLoadingOffer(true)
                is UIPosVirtualAccreditationState.HideLoading -> showLoadingOffer(false)
                is UIPosVirtualAccreditationState.Success -> setupViewProducts()
                is UIPosVirtualAccreditationState.Error -> handleErrorLoadingOffer(it)
            }
            enableButtonContinue()
        }
    }

    private fun setupObserverAcceptAutomaticReceiver() {
        viewModel.acceptAutomaticReceiverIsChecked.observe(viewLifecycleOwner) {
            binding?.apply {
                cbAcceptAutomaticReceipt.isChecked = it
                llAcceptAutomaticReceipt.contentDescription =
                    getString(
                        if (it) {
                            R.string.pos_virtual_accreditation_content_description_check_box_automatic_receipt_checked
                        } else {
                            R.string.pos_virtual_accreditation_content_description_check_box_automatic_receipt_not_checked
                        },
                    )
            }
        }
    }

    private fun handleErrorLoadingOffer(state: UIPosVirtualAccreditationState.Error) {
        logException(state.error)
        when (state) {
            is UIPosVirtualAccreditationState.GenericError -> {
                showErrorOffer()
            }
            is UIPosVirtualAccreditationState.SuspectError -> {
                showBottomSheetSuspectError()
            }
            is UIPosVirtualAccreditationState.UnavailableError -> {
                showBottomSheetUnavailableError()
            }
            is UIPosVirtualAccreditationState.AntiFraudError -> {
                showBottomSheetGenericError()
            }
            is UIPosVirtualAccreditationState.RequiredDataFieldError -> {
                showBottomSheetRequiredDataFieldError()
            }
        }
    }

    private fun showLoadingOffer(isShow: Boolean) {
        if (isShow) {
            startShimmerCards()
        } else {
            stopShimmerCards()
        }

        showViewsOffer(isShow)
        showGroupErrorGetOffers(false)
    }

    private fun startShimmerCards() =
        binding?.apply {
            shimmerCieloTap.startShimmer()
            shimmerSuperLink.startShimmer()
            shimmerQRCodePix.startShimmer()
            shimmerCieloTap.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
        }

    private fun stopShimmerCards() =
        binding?.apply {
            shimmerCieloTap.stopShimmer()
            shimmerSuperLink.stopShimmer()
            shimmerQRCodePix.stopShimmer()
        }

    private fun showViewsOffer(isShow: Boolean) =
        binding?.apply {
            shimmerCieloTap.visible(isShow)
            shimmerSuperLink.visible(isShow)
            shimmerQRCodePix.visible(isShow)
            ccbCieloTap.visible(isShow.not())
            ccbSuperLink.visible(isShow.not())
            ccbQRCodePix.visible(isShow.not())
        }

    private fun showErrorOffer() {
        binding?.apply {
            ccbCieloTap.gone()
            ccbSuperLink.gone()
            ccbQRCodePix.gone()
        }

        showGroupErrorGetOffers(true)
    }

    private fun showGroupErrorGetOffers(isShow: Boolean) {
        binding?.apply {
            tvTitleErrorOffers.visible(isShow)
            tvMessageErrorOffers.visible(isShow)
            tvBtnReloadOffers.visible(isShow)
        }
    }

    private fun showBottomSheetGenericError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(R.string.commons_generic_error_title),
                message = getString(R.string.pos_virtual_error_message_generic),
                labelSecondButton = getString(R.string.pos_virtual_accreditation_label_button_understand),
                isShowButtonClose = false,
                callbackSecondButton = {
                    requireActivity().finish()
                },
                callbackBack = {
                    requireActivity().finish()
                },
            )
        }
    }

    private fun showBottomSheetSuspectError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.ic_90_celular_atencao,
                title = getString(R.string.commons_generic_error_title),
                message = getString(R.string.pos_virtual_accreditation_bs_suspect_error_message),
                labelFirstButton = getString(R.string.go_to_initial_screen),
                labelSecondButton = getString(R.string.text_call_center_action),
                isShowButtonClose = false,
                isShowFirstButton = true,
                callbackFirstButton = {
                    requireActivity().finish()
                },
                callbackSecondButton = {
                    callCenter()
                    requireActivity().finish()
                },
                callbackBack = {
                    requireActivity().finish()
                },
            )
        }
    }

    private fun showBottomSheetUnavailableError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                title = getString(R.string.pos_virtual_accreditation_bs_unavailable_error_title),
                message = getString(R.string.pos_virtual_accreditation_bs_unavailable_error_message),
                labelSecondButton = getString(R.string.pos_virtual_accreditation_label_button_understand),
                isShowButtonClose = false,
                callbackSecondButton = {
                    requireActivity().finish()
                },
                callbackBack = {
                    requireActivity().finish()
                },
            )
        }
    }

    private fun showBottomSheetRequiredDataFieldError() {
        doWhenResumed {
            navigation?.showCustomHandlerView(
                contentImage = R.drawable.ic_90_celular_atencao,
                title = getString(R.string.tap_on_phone_bs_required_data_field_title),
                message = getString(R.string.tap_on_phone_bs_required_data_field_description),
                labelFirstButton = getString(R.string.tap_on_phone_bs_required_data_field_label_button_secondary),
                labelSecondButton = getString(R.string.text_call_center_action),
                isShowButtonClose = false,
                isShowFirstButton = true,
                callbackFirstButton = {
                    requireActivity().finish()
                },
                callbackSecondButton = {
                    callCenter()
                    requireActivity().finish()
                },
                callbackBack = {
                    requireActivity().finish()
                },
            )
        }
    }

    private fun callCenter() {
        logClickButton(
            CONTENT_COMPONENT_BOTTOM_SHEET_GENERIC_TITLE,
            getString(R.string.text_call_center_action),
        )
        Utils.openCall(requireActivity(), PHONE_CALL_CENTER)
    }

    private fun enableButtonContinue() {
        binding?.btnContinue?.isEnabled =
            viewModel.offerState.value == UIPosVirtualAccreditationState.Success
    }

    private fun navigateToAccreditationHire() {
        doWhenResumed {
            viewModel.acceptAutomaticReceiverIsChecked.value?.let { isAcceptAutomaticReceiver ->
                logAddPaymentInfo()

                findNavController().safeNavigate(
                    PosVirtualAccreditationOfferFragmentDirections.actionPosVirtualAccreditationOfferToPosVirtualAccreditationHire(
                        viewModel.offerID,
                        isAcceptAutomaticReceiver,
                        viewModel.agreements.toTypedArray(),
                        viewModel.products.toTypedArray(),
                        viewModel.itemsConfigurations.toTypedArray(),
                        viewModel.required
                    ),
                )
            }
        }
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logException(error: NewErrorMessage? = null) = ga4.logException(screenPath, error)

    private fun logClickButton(
        component: String,
        labelButton: String,
    ) {
        ga4.logClick(
            screenPath,
            component,
            GoogleAnalytics4Values.BUTTON,
            labelButton,
        )
    }

    private fun logAddPaymentInfo() {
        ga4.logAddPaymentInfoHire(viewModel.contractedProducts)
    }
}
