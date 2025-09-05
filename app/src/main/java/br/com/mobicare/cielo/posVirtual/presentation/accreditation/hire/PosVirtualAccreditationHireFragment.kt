package br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.CieloNavLinksBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.enum.CieloBankIcons
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.registerForActivityResultCustom
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Agreement
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Product
import br.com.mobicare.cielo.component.requiredDataField.presentation.RequiredDataFieldFlowActivity
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.databinding.FragmentPosVirtualAccreditationHireBinding
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationBsChangeBankBinding
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationBsConfirmBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.CONFIRM_HIRE
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.CONTENT_COMPONENT_BANK
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.DESCRIPTION_SELECT_BANK
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.DESCRIPTION_TERMS_AND_CONDITIONS
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics.Companion.TERMS_AND_CONDITIONS
import br.com.mobicare.cielo.posVirtual.domain.model.BankUI
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationCreateOrderState
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationState
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PosVirtualAccreditationHireFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: PosVirtualAccreditationHireViewModel by viewModel()

    private var binding: FragmentPosVirtualAccreditationHireBinding? = null
    private val handlerValidationToken: HandlerValidationToken by inject()
    private var navigation: CieloNavigation? = null
    private var requiredDataFieldFlowActivity: ActivityResultLauncher<Intent>? = null

    private val args: PosVirtualAccreditationHireFragmentArgs by navArgs()

    private val offerID by lazy {
        args.posvirtualofferidargs
    }

    private val isAcceptAutomaticReceipt by lazy {
        args.posvirtualisacceptautomaticreceiptargs
    }

    private val agreements: List<Agreement> by lazy {
        args.posvirtualagreementsargs.toList()
    }

    private val products: List<Product> by lazy {
        args.posvirtualproductsargs.toList()
    }

    private val itemsConfigurations by lazy {
        args.posvirtualitemsconfigurationsargs.toList()
    }

    private val required by lazy {
        args.posvirtualrequiredargs
    }

    private val ga4: PosVirtualAnalytics by inject()
    private val screenPath: String get() = PosVirtualAnalytics.SCREEN_VIEW_ACCREDITATION_BANKING_DOMICILE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPosVirtualAccreditationHireBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerRequiredDataFieldFlowActivity()
        setupStart()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupView()
        setupListeners()

        logScreenView()
    }

    override fun onButtonClicked(labelButton: String) {
        logClickButton(CONTENT_COMPONENT_BANK, labelButton)
        showBottomSheetConfirmHire()
    }

    private fun registerRequiredDataFieldFlowActivity() {
        requiredDataFieldFlowActivity =
            registerForActivityResultCustom(::callbackResultRequiredDataFieldFlowActivity)
    }

    private fun callbackResultRequiredDataFieldFlowActivity(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            requireActivity().finish()
        }
    }

    private fun setupStart() {
        viewModel.start(
            offerID,
            agreements,
            products,
            itemsConfigurations,
            required
        )
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                configureCollapsingToolbar(setupConfiguratorToolbar())
                showButton(true)
                setTextButton(getString(R.string.pos_virtual_accreditation_label_button_hire))
                setNavigationListener(this@PosVirtualAccreditationHireFragment)
            }
        }
    }

    private fun setupConfiguratorToolbar(): CollapsingToolbarBaseActivity.Configurator {
        return CollapsingToolbarBaseActivity.Configurator(
            toolbarTitle = getString(R.string.pos_virtual_accreditation_title),
            toolbarTitleAppearance = CollapsingToolbarBaseActivity.ToolbarTitleAppearance(
                collapsed = R.style.CollapsingToolbar_Collapsed_BlackBold,
                expanded = R.style.CollapsingToolbar_Expanded_BlackBold,
            ),
            toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                menuRes = R.menu.menu_help,
                onOptionsItemSelected = {
                    if (it.itemId == R.id.menuActionHelp) {
                        requireActivity().openFaq(
                            tag = ConfigurationDef.TAG_HELP_CENTER_POS_VIRTUAL,
                            subCategoryName = getString(R.string.pos_virtual)
                        )
                    }
                }
            )
        )
    }

    private fun setupView() {
        binding?.includeErrorChangeBank?.tvErrorMessage?.text =
            getString(R.string.pos_virtual_accreditation_error_message_card_change_bank)
    }

    private fun setupListeners() {
        binding?.apply {
            ccbChangeBank.setOnClickListener {
                showBottomSheetChangeBank(ccbChangeBank.labelButton.orEmpty())
            }

            tvBtnOpenTerms.setOnClickListener {
                logClickButton(CONTENT_COMPONENT_BANK, tvBtnOpenTerms.text.toString())
                showBottomSheetTerms()
            }

            includeErrorChangeBank.tvBtnReload.setOnClickListener {
                viewModel.reloadGetBanks()
            }
        }
    }

    private fun setupObservers() {
        setupObserveLoadingBankStart()
        setupObserveBankSelected()
        setupObserveLoadingCreateOrder()
    }

    private fun setupObserveLoadingBankStart() {
        viewModel.loadingBanksState.observe(viewLifecycleOwner) {
            when (it) {
                is UIPosVirtualAccreditationState.ShowLoading -> showLoadingChangeBank(true)
                is UIPosVirtualAccreditationState.HideLoading -> showLoadingChangeBank(false)
                is UIPosVirtualAccreditationState.Error -> showErrorChangeBank(it.error)
            }
            enableButtonConfirm()
        }
    }

    private fun setupObserveBankSelected() {
        viewModel.bankSelected.observe(viewLifecycleOwner) {
            if (it != null) setupChangeBank(it)
        }
    }

    private fun setupObserveLoadingCreateOrder() {
        viewModel.loadingCreateOrderState.observe(viewLifecycleOwner) {
            when (it) {
                is UIPosVirtualAccreditationCreateOrderState.HideLoading -> onHideLoading()
                is UIPosVirtualAccreditationCreateOrderState.Success -> onSuccessConfirmHere(it.orderID)
                is UIPosVirtualAccreditationCreateOrderState.OpenRequiredDataField -> showRequiredDataField()
                is UIPosVirtualAccreditationCreateOrderState.GenerateOTPCode -> generateOTPCode()
                is UIPosVirtualAccreditationCreateOrderState.TokenError -> onErrorToken(it.error)
                is UIPosVirtualAccreditationCreateOrderState.GenericError -> onErrorGenericConfirmHere(
                    it.error
                )
                is UIPosVirtualAccreditationCreateOrderState.InvalidBankError -> onErrorChangeBankConfirmHere(
                    it.error
                )
            }
        }
    }

    private fun showLoadingChangeBank(isShow: Boolean) {
        binding?.apply {
            if (isShow) shimmerChangeBank.startShimmer()
            else shimmerChangeBank.stopShimmer()

            shimmerChangeBank.visible(isShow)
            ccbChangeBank.visible(isShow.not())
            includeErrorChangeBank.root.gone()
        }
    }

    private fun showRequiredDataField() {
        viewModel.required?.let {
            val uiRequiredDataField =
                UiRequiredDataField(it, viewModel.generateOrderRequest().order)
            val intent = RequiredDataFieldFlowActivity.launch(requireContext(), uiRequiredDataField)
            requiredDataFieldFlowActivity?.launch(intent)
        }
    }

    private fun showErrorChangeBank(error: NewErrorMessage? = null) {
        logException(error)

        binding?.apply {
            ccbChangeBank.gone()
            includeErrorChangeBank.root.visible()
        }
    }

    private fun setupChangeBank(bankSelected: BankUI) {
        val bankIcon = CieloBankIcons.getBankFromCode(bankSelected.code.orEmpty())
        binding?.ccbChangeBank?.apply {
            title = bankIcon.bankName.orEmpty()
            icon = bankIcon.icon
            firstSubtitle = getString(
                R.string.pos_virtual_accreditation_label_change_bank_agency_number,
                bankSelected.agency
            )
            secondSubtitle = getString(
                R.string.pos_virtual_accreditation_label_change_bank_account_number,
                bankSelected.account
            )
            showLabelButton = viewModel.banks.size > ONE
        }
    }

    private fun showBottomSheetChangeBank(labelButton: String) {
        var bankSelected: BankUI? = null

        logClickButton(CONTENT_COMPONENT_BANK, labelButton)
        logDisplayContentBottomSheetSelectBank()

        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.pos_virtual_accreditation_title_bs_change_bank)
            ),
            layoutItemRes = R.layout.layout_pos_virtual_accreditation_bs_change_bank,
            data = viewModel.banks,
            initialSelectedItem = viewModel.bankSelected.value,
            onViewBound = { bank, isSelected, view ->
                setupOnViewBoundBSChangeBank(bank, isSelected, view)
            },
            onItemClicked = { bank, position, bottomSheet ->
                bankSelected = bank
                bottomSheet.updateSelectedPosition(position)
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.pos_virtual_accreditation_label_button_confirm),
                onTap = {
                    bankSelected?.let { itBank ->
                        viewModel.setBankSelected(itBank)
                    }
                    logClickButton(
                        viewModel.bankNameSelected,
                        getString(R.string.pos_virtual_accreditation_label_button_confirm)
                    )
                    it.dismiss()
                }
            )
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun setupOnViewBoundBSChangeBank(bank: BankUI, isSelected: Boolean, view: View) {
        val bankIcon = CieloBankIcons.getBankFromCode(bank.code ?: EMPTY)

        LayoutPosVirtualAccreditationBsChangeBankBinding.bind(view).apply {
            ivIconBank.setImageResource(bankIcon.icon)
            tvBankName.text = bankIcon.bankName
            tvBankAgency.fromHtml(
                R.string.pos_virtual_accreditation_label_change_bank_agency_number,
                bank.agency
            )
            tvBankAccount.fromHtml(
                R.string.pos_virtual_accreditation_label_change_bank_account_number,
                bank.account
            )
            ivRadioButton.isSelected = isSelected
            root.isSelected = isSelected
        }
    }

    private fun showBottomSheetTerms() {
        val links = viewModel.terms.map {
            CieloNavLinksBottomSheet.Link(
                icon = R.drawable.ic_arrows_arrow_circle_right_brand_400_24_dp,
                url = it.url,
                label = it.label,
                callback = ::logClickLinkTerms
            )
        }

        logDisplayContentBottomSheetTermsAccreditation()

        CieloNavLinksBottomSheet.create(
            title = getString(R.string.pos_virtual_accreditation_title_bs_terms),
            links = links
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun showBottomSheetConfirmHire() {
        if (viewModel.showBSConfirmTerms) {
            logDisplayContentBottomSheetConfirmHire()

            CieloContentBottomSheet.create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                    title = getString(R.string.pos_virtual_accreditation_title_bs_confirm),
                    showCloseButton = true
                ),
                contentLayoutRes = R.layout.layout_pos_virtual_accreditation_bs_confirm,
                onContentViewCreated = { view, _ ->
                    setupOnContentViewCreatedBSConfirmHere(view)
                },
                mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                    title = getString(R.string.pos_virtual_accreditation_label_button_confirm),
                    onTap = {
                        logClickButton(
                            CONFIRM_HIRE,
                            getString(R.string.pos_virtual_accreditation_label_button_confirm)
                        )
                        confirmHire()
                        it.dismiss()
                    }
                )
            ).show(requireActivity().supportFragmentManager, EMPTY)
        } else {
            confirmHire()
        }
    }

    private fun setupOnContentViewCreatedBSConfirmHere(view: View) {
        LayoutPosVirtualAccreditationBsConfirmBinding.bind(view).apply {
            tvCieloTap.setupTextItemBSConfirmHire(
                R.string.pos_virtual_accreditation_content_bs_confirm_cielo_tap_is_accepted_rr,
                R.string.pos_virtual_accreditation_content_bs_confirm_cielo_tap_is_not_accept_rr,
                viewModel.showBSConfirmTermsTap
            )

            tvSuperLink.setupTextItemBSConfirmHire(
                R.string.pos_virtual_accreditation_content_bs_confirm_super_link_is_accepted_rr,
                R.string.pos_virtual_accreditation_content_bs_confirm_super_link_is_not_accept_rr,
                viewModel.showBSConfirmTermsSuperLink
            )

            tvQRCodePix.setupTextItemBSConfirmHire(
                R.string.pos_virtual_accreditation_content_bs_confirm_qr_code_pix_is_accepted_rr,
                R.string.pos_virtual_accreditation_content_bs_confirm_qr_code_pix_is_not_accept_rr,
                viewModel.showBSConfirmTermsPix
            )
        }
    }

    private fun TextView.setupTextItemBSConfirmHire(
        @StringRes acceptedResId: Int,
        @StringRes notAcceptedResId: Int,
        isVisible: Boolean
    ) {
        fromHtml(if (isAcceptAutomaticReceipt) acceptedResId else notAcceptedResId)
        visible(isVisible)
    }

    private fun enableButtonConfirm() {
        navigation?.enableButton(viewModel.loadingBanksState.value is UIPosVirtualAccreditationState.Success)
    }

    private fun confirmHire() {
        viewModel.toHire()
    }

    private fun generateOTPCode() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) =
                    viewModel.createOrder(token)

                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onHideLoading() {
        handlerValidationToken.hideAnimation(
            isDelay = false,
            callbackStopAnimation = object : HandlerValidationToken.CallbackStopAnimation {}
        )
    }

    private fun onSuccessConfirmHere(protocolNumber: String) {
        handlerValidationToken.playAnimationSuccess(callbackAnimationSuccess =
        object : HandlerValidationToken.CallbackAnimationSuccess {
            override fun onSuccess() {
                doWhenResumed {
                    logScreenViewSuccessAcceptOffer()
                    logPurchase()

                    navigation?.showCustomHandlerView(
                        contentImage = R.drawable.img_14_estrelas,
                        title = getString(R.string.pos_virtual_accreditation_bs_success_confirm_hire_title),
                        message = getString(
                            R.string.pos_virtual_accreditation_bs_success_confirm_hire_message,
                            protocolNumber
                        ),
                        labelSecondButton = getString(R.string.go_to_initial_screen),
                        isShowButtonClose = false,
                        callbackSecondButton = {
                            requireActivity().finish()
                        },
                        callbackBack = {
                            requireActivity().finish()
                        }
                    )
                }
            }
        })
    }

    private fun onErrorGenericConfirmHere(error: NewErrorMessage? = null) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                doWhenResumed {
                    logException(error)

                    navigation?.showCustomHandlerView(
                        title = getString(R.string.commons_generic_error_title),
                        message = getString(R.string.pos_virtual_error_message_generic),
                        labelFirstButton = getString(R.string.back),
                        labelSecondButton = getString(R.string.text_try_again_label),
                        isShowFirstButton = true,
                        isShowButtonClose = true,
                        callbackSecondButton = {
                            generateOTPCode()
                        },
                        callbackClose = {
                            requireActivity().finish()
                        }
                    )
                }
            }
        })
    }

    private fun onErrorChangeBankConfirmHere(error: NewErrorMessage? = null) {
        handlerValidationToken.hideAnimation(callbackStopAnimation =
        object : HandlerValidationToken.CallbackStopAnimation {
            override fun onStop() {
                doWhenResumed {
                    logException(error)

                    navigation?.showCustomHandlerView(
                        title = getString(R.string.commons_generic_error_title),
                        message = getString(R.string.pos_virtual_accreditation_bs_error_confirm_hire_change_bank_message),
                        labelSecondButton = getString(R.string.text_try_again_label),
                        isShowButtonClose = false,
                    )
                }
            }
        })
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        logException(error)

        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    generateOTPCode()
                }
            }
        )
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logScreenViewSuccessAcceptOffer() =
        ga4.logScreenView(PosVirtualAnalytics.SCREEN_VIEW_ACCREDITATION_SUCCESS_ACCEPT_OFFER)

    private fun logException(error: NewErrorMessage? = null) = ga4.logException(screenPath, error)

    private fun logDisplayContentBottomSheetSelectBank() =
        ga4.logDisplayContentAccreditationBankingDomicile(
            DESCRIPTION_SELECT_BANK,
            CONTENT_COMPONENT_BANK
        )

    private fun logDisplayContentBottomSheetTermsAccreditation() =
        ga4.logDisplayContentAccreditationBankingDomicile(DESCRIPTION_TERMS_AND_CONDITIONS)

    private fun logDisplayContentBottomSheetConfirmHire() =
        ga4.logDisplayContentAccreditationBankingDomicile(CONFIRM_HIRE)

    private fun logClickLinkTerms(link: CieloNavLinksBottomSheet.Link) {
        ga4.logClick(
            screenPath,
            TERMS_AND_CONDITIONS,
            GoogleAnalytics4Values.BUTTON,
            link.label
        )
    }

    private fun logClickButton(component: String, labelButton: String) {
        ga4.logClick(
            screenPath,
            component,
            GoogleAnalytics4Values.BUTTON,
            labelButton
        )
    }

    private fun logPurchase() =
        ga4.logPurchaseSuccessHirePOS(viewModel.bankNameSelected, viewModel.contractedProducts)

}