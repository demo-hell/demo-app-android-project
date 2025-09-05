package br.com.mobicare.cielo.openFinance.presentation.resume

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCNPJ
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCPF
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.databinding.FragmentResumeHolderBinding
import br.com.mobicare.cielo.databinding.LayoutOpenFinanceConfirmCancellationBinding
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.presentation.utils.FlowReturnHolder.flowReturnHolder
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateApproveConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateRejectConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateResumeDetainer
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowPixKey
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.REJECTED_USER
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.REJECTED_USER_DETAIL
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResumePaymentHolderFragment : BaseFragment(), CieloNavigationListener {
    private var binding: FragmentResumeHolderBinding? = null
    private var navigation: CieloNavigation? = null
    private val resumePaymentHolderVM: ResumePaymentHolderViewModel by viewModel()
    private val handlerValidationToken: HandlerValidationToken by inject()
    private var companyName: String? = null
    private var versionAPI: String = OpenFinanceConstants.V2

    private val toolbarDefault
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.txt_title_confirm_payment_open_finance),
                showBackButton = false,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_close_blue,
                    onOptionsItemSelected = {
                        finishScreen()
                        flowReturnHolder(requireActivity())
                    }
                )
            )
        )

    private val toolbarBlank get() = CieloCollapsingToolbarLayout
        .Configurator(layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentResumeHolderBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resumePaymentHolderVM.getDetainer()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        showResumeDetainer()
        observePayment()
        observeReject()
        observePixKey()
        setListeners()
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarDefault)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun verifyToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) =
                    resumePaymentHolderVM.paymentIsEligible(token)

                override fun onError() = onErrorToken()
            }
        )
    }

    private fun onErrorToken(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() {
                    verifyToken()
                }
            }
        )
    }

    private fun showResumeDetainer() {
        resumePaymentHolderVM.getResumeDetainerLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateResumeDetainer.Success -> {
                    hideLoadingShimmer()
                    uiState.data?.let { detainerResponse ->
                        setInformationDetainer(detainerResponse)
                    }
                }

                is UIStateResumeDetainer.InvalidPaymentAlreadyAuthorized -> {
                    hideLoadingShimmer()
                    showHandlerPaymentAlreadyAuthorized()
                }

                is UIStateResumeDetainer.PaymentRequestRejected -> {
                    hideLoadingShimmer()
                    showHandlerPaymentRequestRejected()
                }

                is UIStateResumeDetainer.PaymentTimeOver -> {
                    hideLoadingShimmer()
                    showHandlerPaymentTimeOver()
                }

                is UIStateResumeDetainer.Error -> {
                    hideLoadingShimmer()
                    showErrorHandler()
                }

                is UIStateResumeDetainer.WithoutAccess -> {
                    hideLoadingShimmer()
                    showHandlerWithoutAccess()
                }

                is UIStateResumeDetainer.Loading -> {
                    showLoadingShimmer()
                }
            }
        }
    }

    private fun observePayment() {
        resumePaymentHolderVM.approveConsentLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateApproveConsent.Success -> {
                    hideLoadingConsent()
                    showHandlerSuccessPaymentInProgress()
                }

                is UIStateApproveConsent.ErrorPaymentInProgress -> {
                    hideLoadingConsent()
                    showHandlerErrorPaymentInProgress()
                }

                is UIStateApproveConsent.Loading -> {
                    hideToolbar()
                    showLoadingConsent()
                }
            }
        }
    }

    private fun observeReject() {
        resumePaymentHolderVM.rejectConsentLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateRejectConsent.Success -> {
                    hideLoadingConsent()
                    showSuccessHandlerCancellation()
                }

                is UIStateRejectConsent.Error -> {
                    hideLoadingConsent()
                    showErrorHandlerCancellation()
                }

                is UIStateRejectConsent.Loading -> {
                    hideToolbar()
                    showLoadingConsent()
                }
            }
        }
    }

    private fun setInformationDetainer(detainer: DetainerResponse) {
        companyName = detainer.companyName
        versionAPI = detainer.apiVersion
        resumePaymentHolderVM.showOrHidePixKey(detainer.payment.detail.proxy)
        changeMerchant()
        setProperties(detainer)
    }

    private fun setProperties(detainer: DetainerResponse){
        binding?.apply {
            tvValuePayment.text = detainer.payment.amount.toPtBrRealString()
            tvPaymentForm.text = detainer.payment.type
            tvPaymentDate.text = detainer.payment.date.formatterDate(
                SIMPLE_DATE_INTERNATIONAL,
                SIMPLE_DT_FORMAT_MASK
            )
            tvNameAccountDate.text = resumePaymentHolderVM.getPayer()
            tvCNPJ.text = resumePaymentHolderVM.getCNPJ()
            balanceAccount.text =
                resumePaymentHolderVM.getCardsBalanceLiveData.value?.amount?.toPtBrRealString()
            tvDestinationPayment.text = detainer.creditor.name
            tvDocument.text = applyMasks(detainer.creditor.document.identification)
            tvPixKey.text = applyMasks(detainer.payment.detail.proxy)
            tvWarnigDetainer.text = HtmlCompat.fromHtml(
                getString(R.string.txt_warning_detainer_open_finance, companyName),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        }
    }

    private fun changeMerchant() {
        binding?.containerChangeMerchant?.setOnClickListener {
            hideToolbar()
            findNavController().navigate(
                ResumePaymentHolderFragmentDirections.actionResumePaymentHolderFragmentToOpenFinanceHomeFragment(),
                NavOptions.Builder()
                    .setPopUpTo(R.id.openFinanceHomeFragment, true)
                    .build()
            )
        }
    }

    private fun applyMasks(text: String):String{
        return when {
            isCNPJ(text) -> addMaskCPForCNPJ(text, getString(R.string.mask_cnpj_step4))
            isCPF(text) -> addMaskCPForCNPJ(text, getString(R.string.mask_cpf_step4))
            else -> text
        }
    }

    private fun showBottomSheetConfirmPayment() {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.txt_confirm_payment_open_finance),
                showCloseButton = true
            ),
            contentLayoutRes = R.layout.layout_open_finance_confirm_payment,
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.txt_payment_open_finance),
                onTap = {
                    verifyToken()
                    it.dismiss()
                }
            ),
            secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.change_ec_btn_back),
                onTap = {
                    it.dismiss()
                }
            ),
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun showBottomSheetConfirmCancellation() {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.txt_confirm_cancel_payment_open_finance),
                showCloseButton = true
            ),
            contentLayoutRes = R.layout.layout_open_finance_confirm_cancellation,
            onContentViewCreated = { view, _ ->
                LayoutOpenFinanceConfirmCancellationBinding.bind(view).tvDescPayment.text =
                    getString(R.string.txt_confirm_cancellation_open_finance, companyName)
            },
            mainButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.text_pix_transfer_cancel),
                onTap = {
                    resumePaymentHolderVM.rejectConsent(
                        REJECTED_USER_DETAIL,
                        REJECTED_USER
                    )
                    it.dismiss()
                }
            ),
            secondaryButtonConfigurator = CieloBottomSheet.ButtonConfigurator(
                title = getString(R.string.change_ec_btn_back),
                onTap = {
                    it.dismiss()
                }
            ),
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun showErrorHandler() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.id_onboarding_bs_title_error_generic),
            message = getString(R.string.commons_generic_error_message),
            labelSecondButton = getString(R.string.entendi),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerWithoutAccess() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.txt_without_access_open_finance),
            message = getString(R.string.txt_without_desc_access_open_finance),
            labelSecondButton = getString(R.string.txt_pix_open_finance_back_institution),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerPaymentAlreadyAuthorized() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_cash_card_block,
            title = getString(R.string.txt_request_payment_invalid_open_finance),
            message = getString(R.string.txt_request_payment_invalid_desc_open_finance),
            labelSecondButton = getString(R.string.txt_pix_open_finance_back_institution),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerPaymentRequestRejected() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_cash_card_block,
            title = getString(R.string.txt_request_payment_invalid_open_finance),
            message = getString(R.string.txt_request_payment_invalid_reject_open_finance),
            labelSecondButton = getString(R.string.txt_pix_open_finance_back_institution),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerPaymentTimeOver() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.img_aguarde,
            title = getString(R.string.txt_time_try_again_open_finance),
            message = getString(R.string.txt_time_try_desc_again_open_finance),
            labelSecondButton = getString(R.string.button_bottom_sheet_error_webview),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerErrorPaymentInProgress() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_transaction_error,
            title = getString(R.string.txt_payment_progress_open_finance),
            message = HtmlCompat.fromHtml(
                getString(R.string.txt_payment_progress_desc_open_finance, companyName),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString(),
            labelSecondButton = getString(R.string.txt_go_now_open_finance),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showHandlerSuccessPaymentInProgress() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_confirm_flow,
            title = getString(R.string.txt_payment_progress_open_finance),
            message = HtmlCompat.fromHtml(
                getString(R.string.txt_payment_progress_success_open_finance, companyName),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString(),
            labelSecondButton = getString(R.string.txt_go_now_open_finance),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showErrorHandlerCancellation() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.txt_title_cancel_payment_open_finance),
            message = HtmlCompat.fromHtml(
                getString(R.string.txt_desc_cancel_payment_open_finance, companyName),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString(),
            labelSecondButton = getString(R.string.entendi),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun showSuccessHandlerCancellation() {
        navigation?.showCustomHandlerView(
            contentImage = R.drawable.ic_transaction_error,
            title = getString(R.string.txt_title_success_cancellation_payment_open_finance),
            message = HtmlCompat.fromHtml(
                getString(R.string.txt_desc_cancel_payment_open_finance, companyName),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ).toString(),
            labelSecondButton = getString(R.string.entendi),
            isShowFirstButton = false,
            isShowSecondButton = true,
            callbackSecondButton = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackBack = {
                finishScreen()
                flowReturnHolder(requireActivity())
            },
            callbackClose = {
                finishScreen()
                flowReturnHolder(requireActivity())
            }
        )
    }

    private fun finishScreen() {
        activity?.finish()
    }

    private fun setListeners() {
        binding?.apply {
            btnConfirmSolicitation.setOnClickListener {
                showBottomSheetConfirmPayment()
            }
            btnCancel.setOnClickListener {
                showBottomSheetConfirmCancellation()
            }
        }
    }

    private fun showLoadingShimmer() {
        binding?.apply {
            containerResume.gone()
            shimmerLifecycleIndicator.visible()
        }
    }

    private fun hideLoadingShimmer() {
        binding?.apply {
            containerResume.visible()
            shimmerLifecycleIndicator.gone()
        }
    }

    private fun showLoadingConsent() {
        binding?.apply {
            progressBar.visible()
            containerResume.gone()
        }
    }

    private fun hideLoadingConsent() {
        binding?.progressBar.gone()
    }

    private fun hideToolbar() {
        navigation?.configureCollapsingToolbar(toolbarBlank)
    }

    private fun observePixKey() {
        resumePaymentHolderVM.pixKey.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateShowPixKey.HidePixKey -> {
                    binding?.containerPixKey.gone()
                }

                is UIStateShowPixKey.ShowPixKey -> {
                    uiState.data?.let {
                        binding?.iconPix?.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                it
                            )
                        )
                    }
                }
            }

        }
    }
}