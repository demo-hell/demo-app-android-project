package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.datePicker.CieloDatePicker
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.token.presentation.HandlerValidationToken
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel.PixQRCodePaymentViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixPaymentQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils
import br.com.mobicare.cielo.pixMVVM.presentation.refund.ui.dialog.PixRefundCancelBottomSheet
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui.dialog.PixTransferMessageBottomSheet
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.util.Calendar

class PixQRCodePaymentSummaryFragment :
    BaseFragment(),
    AllowMeContract.View {
    private val viewModel: PixQRCodePaymentViewModel by sharedViewModel()
    private val navArgs: PixQRCodePaymentSummaryFragmentArgs by navArgs()

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val handlerValidationToken: HandlerValidationToken by inject()

    private val toolbarConfigurator get() =
        CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar =
                CieloCollapsingToolbarLayout.Toolbar(
                    title =
                        getString(
                            pixDecodeQRCode?.let { PixQRCodeUtils.getTitleToolbar(it) }
                                ?: R.string.pix_qr_code_payment_summary_toolbar_title_default,
                        ),
                    menu =
                        CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_help,
                            onOptionsItemSelected = ::onClickOptionsItemMenuToolbar,
                        ),
                ),
        )

    private val pixDecodeQRCode by lazy { navArgs.pixdecodeqrcodemodelargs }

    private var navigation: CieloNavigation? = null

    private val onClickBackAndCloseButtonErrorHandler =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                requireActivity().finish()
            }
        }

    private val onClickBackAndCloseButtonSuccessHandler =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                requireActivity().toHomePix()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PixQRCodePaymentSummaryScreen(
                viewModel = viewModel,
                onClickChangePaymentAmount = ::onClickChangePaymentAmount,
                onClickChangeChangeAmount = ::onClickChangeChangeAmount,
                onClickChangeDatePayment = ::onClickChangeDatePayment,
                onClickWriteMessage = ::onClickWriteMessageButton,
                onClickCancelTransactionButton = ::onClickCancelTransactionButton,
                onClickToPayButton = ::onClickToPayButton,
            )
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        pixDecodeQRCode?.let { viewModel.setPixDecodeQRCode(it) }
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager = childFragmentManager

    override fun successCollectToken(result: String) {
        viewModel.setFingerprint(result)
        getToken()
    }

    override fun errorCollectToken(
        result: String?,
        errorMessage: String,
        mandatory: Boolean,
    ) {
        doWhenResumed {
            CieloAlertDialogFragment
                .Builder()
                .title(getString(R.string.dialog_title))
                .message(errorMessage)
                .closeTextButton(getString(R.string.dialog_button))
                .build()
                .showAllowingStateLoss(childFragmentManager, getString(R.string.text_cieloalertdialog))
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.also {
                it.showContent(true)
                it.configureCollapsingToolbar(toolbarConfigurator)
            }
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixPaymentQRCodeUIState.HideLoading -> onHideLoading()
                is PixPaymentQRCodeUIState.TransactionExecuted -> onSuccessTransactionExecuted(state.result, state.typeQRCode)
                is PixPaymentQRCodeUIState.TransactionScheduled -> onSuccessTransactionScheduled(state.result)
                is PixPaymentQRCodeUIState.TransactionProcessing -> onSuccessTransactionProcessing()
                is PixPaymentQRCodeUIState.TransactionFailed -> onTransactionFailed()
                is PixPaymentQRCodeUIState.FourHundredError -> onFourHundredError(state.error)
                is PixPaymentQRCodeUIState.GenericError -> onGenericError(state.error)
                is PixPaymentQRCodeUIState.TokenError -> onTokenError()
                is PixPaymentQRCodeUIState.DoNothing -> Unit
            }
        }
    }

    private fun startAllowMe() {
        allowMePresenter.collect(
            context = requireActivity(),
            mAllowMeContextual = allowMePresenter.init(requireContext()),
            mandatory = true,
        )
    }

    private fun getToken() {
        handlerValidationToken.getToken(
            childFragmentManager,
            object : HandlerValidationToken.CallbackToken {
                override fun onSuccess(token: String) = viewModel.toPay(token)

                override fun onError() = onTokenError()
            },
        )
    }

    private fun onClickShowReceipt(transferResult: PixTransferResult) =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                navigateToReceipt(transferResult)
            }
        }

    private fun onHideLoading(actionOnStop: () -> Unit = {}) {
        handlerValidationToken.hideAnimation(
            callbackStopAnimation =
                object : HandlerValidationToken.CallbackStopAnimation {
                    override fun onStop() {
                        actionOnStop.invoke()
                    }
                },
        )
    }

    private fun getMessageBSTransactionExecuted(typeQRCode: PixQrCodeOperationType): String {
        val paymentAmount =
            viewModel.paymentAmount.value
                ?.toPtBrRealString()
                .orEmpty()

        val changeAmount =
            viewModel.changeAmount.value
                ?.toPtBrRealString()
                .orEmpty()

        val finalAmount =
            viewModel.finalAmount.value
                ?.toPtBrRealString()
                .orEmpty()

        val receiverName =
            viewModel.pixDecodeQRCode.value
                ?.receiverName
                .orEmpty()

        return when (typeQRCode) {
            PixQrCodeOperationType.CHANGE ->
                getString(
                    R.string.pix_qr_code_payment_summary_payment_message_bs_success_change_executed,
                    paymentAmount,
                    changeAmount,
                    receiverName,
                )
            PixQrCodeOperationType.WITHDRAWAL ->
                getString(
                    R.string.pix_qr_code_payment_summary_payment_message_bs_success_withdrawal_executed,
                    finalAmount,
                    receiverName,
                )
            else ->
                getString(
                    R.string.pix_qr_code_payment_summary_payment_message_bs_success_transaction_executed,
                    finalAmount,
                    receiverName,
                )
        }
    }

    private fun onSuccessTransactionExecuted(
        transferResult: PixTransferResult,
        typeQRCode: PixQrCodeOperationType,
    ) {
        onSuccess {
            val message = getMessageBSTransactionExecuted(typeQRCode)

            showSuccessBottomSheet(
                illustration = R.drawable.img_14_estrelas,
                title = getString(R.string.pix_qr_code_payment_summary_payment_title_bs_success_transaction_executed),
                message = message,
                labelPrimaryButton = getString(R.string.pix_refund_create_show_receipt),
                labelSecondaryButton = getString(R.string.text_close),
                onPrimaryButtonClickListener = onClickShowReceipt(transferResult),
                onSecondaryButtonClickListener = onClickBackAndCloseButtonSuccessHandler,
                onBackButtonClickListener = onClickBackAndCloseButtonSuccessHandler,
                isShowIconCloseButton = false,
            )
        }
    }

    private fun onSuccessTransactionScheduled(transferResult: PixTransferResult) {
        onSuccess {
            showSuccessBottomSheet(
                illustration = R.drawable.img_14_estrelas,
                title = getString(R.string.pix_qr_code_payment_summary_payment_title_bs_success_transaction_scheduled),
                message =
                    getString(
                        R.string.pix_qr_code_payment_summary_payment_message_bs_success_transaction_scheduled,
                        viewModel.finalAmount.value
                            ?.toPtBrRealString()
                            .orEmpty(),
                        viewModel.pixDecodeQRCode.value
                            ?.receiverName
                            .orEmpty(),
                    ),
                labelPrimaryButton = getString(R.string.pix_refund_create_show_receipt),
                labelSecondaryButton = getString(R.string.text_close),
                onPrimaryButtonClickListener = onClickShowReceipt(transferResult),
                onSecondaryButtonClickListener = onClickBackAndCloseButtonSuccessHandler,
                onBackButtonClickListener = onClickBackAndCloseButtonSuccessHandler,
                isShowIconCloseButton = false,
            )
        }
    }

    private fun onSuccessTransactionProcessing() {
        onSuccess {
            showSuccessBottomSheet(
                illustration = R.drawable.img_44_aguarde,
                title = getString(R.string.pix_refund_create_status_pending_title),
                message =
                    getString(
                        R.string.pix_qr_code_payment_summary_payment_message_bs_success_transaction_processing,
                        viewModel.finalAmount.value
                            ?.toPtBrRealString()
                            .orEmpty(),
                        viewModel.pixDecodeQRCode.value
                            ?.receiverName
                            .orEmpty(),
                    ),
                labelPrimaryButton = getString(R.string.text_close),
                labelSecondaryButton = EMPTY_STRING,
                onPrimaryButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                onSecondaryButtonClickListener = null,
                onBackButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                isShowIconCloseButton = true,
            )
        }
    }

    private fun onTransactionFailed() {
        onHideLoading {
            doWhenResumed {
                val message =
                    getString(
                        R.string.pix_qr_code_payment_summary_payment_message_bs_error,
                        viewModel.finalAmount.value
                            ?.toPtBrRealString()
                            .orEmpty(),
                        viewModel.pixDecodeQRCode.value
                            ?.receiverName
                            .orEmpty(),
                    )

                navigation?.showHandlerViewV2(
                    illustration = R.drawable.img_107_transacao_erro,
                    title = getString(R.string.pix_qr_code_payment_summary_payment_title_bs_error),
                    message = message,
                    labelPrimaryButton = getString(R.string.text_close),
                    onPrimaryButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                    onBackButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                    onIconButtonEndHeaderClickListener = onClickBackAndCloseButtonErrorHandler,
                )
            }
        }
    }

    private fun onSuccess(actionOnSuccess: () -> Unit = {}) {
        handlerValidationToken.playAnimationSuccess(
            callbackAnimationSuccess =
                object : HandlerValidationToken.CallbackAnimationSuccess {
                    override fun onSuccess() {
                        doWhenResumed {
                            actionOnSuccess.invoke()
                        }
                    }
                },
        )
    }

    private fun onFourHundredError(error: NewErrorMessage?) {
        onHideLoading {
            doWhenResumed {
                val message =
                    error?.message.takeIf {
                        it.isNullOrBlank().not() || it != DEFAULT_ERROR_MESSAGE
                    } ?: getString(R.string.pix_qr_code_payment_summary_payment_default_message_bs_generic_error)

                showErrorBottomSheet(
                    message = message,
                    labelPrimaryButton = getString(R.string.text_close),
                    onPrimaryButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                    onBackButtonClickListener = onClickBackAndCloseButtonErrorHandler,
                )
            }
        }
    }

    private fun onGenericError(error: NewErrorMessage?) {
        onHideLoading {
            showErrorBottomSheet(
                message = getString(R.string.pix_qr_code_payment_summary_payment_default_message_bs_generic_error),
                labelPrimaryButton = getString(R.string.label_try_again),
                onPrimaryButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                            startAllowMe()
                        }
                    },
                onBackButtonClickListener =
                    object : HandlerViewBuilderFluiV2.HandlerViewListener {
                        override fun onClick(dialog: Dialog?) {
                            dialog?.dismiss()
                        }
                    },
            )
        }
    }

    private fun showSuccessBottomSheet(
        @DrawableRes illustration: Int,
        title: String,
        message: String,
        labelPrimaryButton: String,
        labelSecondaryButton: String,
        onPrimaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onSecondaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        onBackButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener?,
        isShowIconCloseButton: Boolean,
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                illustration = illustration,
                title = title,
                message = message,
                labelPrimaryButton = labelPrimaryButton,
                labelSecondaryButton = labelSecondaryButton,
                onPrimaryButtonClickListener = onPrimaryButtonClickListener,
                onSecondaryButtonClickListener = onSecondaryButtonClickListener,
                onBackButtonClickListener = onBackButtonClickListener,
                onIconButtonEndHeaderClickListener = onClickBackAndCloseButtonErrorHandler,
                isShowIconButtonEndHeader = isShowIconCloseButton,
            )
        }
    }

    private fun showErrorBottomSheet(
        message: String,
        labelPrimaryButton: String,
        onPrimaryButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener,
        onBackButtonClickListener: HandlerViewBuilderFluiV2.HandlerViewListener,
    ) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = getString(R.string.pix_qr_code_payment_summary_payment_title_bs_generic_error),
                message = message,
                labelPrimaryButton = labelPrimaryButton,
                onPrimaryButtonClickListener = onPrimaryButtonClickListener,
                onBackButtonClickListener = onBackButtonClickListener,
                onIconButtonEndHeaderClickListener = onClickBackAndCloseButtonErrorHandler,
            )
        }
    }

    private fun onTokenError(error: NewErrorMessage? = null) {
        handlerValidationToken.playAnimationError(
            error,
            object : HandlerValidationToken.CallbackAnimationError {
                override fun onTryAgain() = getToken()
            },
        )
    }

    private fun onClickChangePaymentAmount(labelButton: String) {
        findNavController().safeNavigate(
            PixQRCodePaymentSummaryFragmentDirections.actionPixQRCodePaymentSummaryFragmentToPixQRCodePaymentInsertAmountFragment(
                true,
                false,
                null,
            ),
        )
    }

    private fun onClickChangeChangeAmount(labelButton: String) {
        findNavController().safeNavigate(
            PixQRCodePaymentSummaryFragmentDirections.actionPixQRCodePaymentSummaryFragmentToPixQRCodePaymentInsertAmountFragment(
                true,
                true,
                null,
            ),
        )
    }

    private fun onDateSelected(date: Calendar) {
        viewModel.setPaymentDate(date)
    }

    private fun onClickChangeDatePayment(labelButton: String) {
        CieloDatePicker.show(
            title = R.string.pix_transfer_review_calendar_title,
            selectedDate = viewModel.paymentDate.value,
            fragmentManager = childFragmentManager,
            tag = this.javaClass.simpleName,
            onDateSelected = ::onDateSelected,
        )
    }

    private fun onClickWriteMessageButton(labelButton: String) {
        PixTransferMessageBottomSheet(
            context = requireContext(),
            message = viewModel.optionalMessage.value,
            onSaveMessage = {
                viewModel.setOptionalMessage(it)
            },
        ).show(childFragmentManager, this.javaClass.simpleName)
    }

    private fun onClickCancelTransactionButton(labelButton: String) {
        PixRefundCancelBottomSheet(
            context = requireContext(),
            onCancelRefund = {
                requireActivity().finish()
            },
        ).show(childFragmentManager, this.javaClass.simpleName)
    }

    private fun onClickToPayButton(labelButton: String) {
        startAllowMe()
    }

    private fun onClickOptionsItemMenuToolbar(item: MenuItem) {
        when (item.itemId) {
            R.id.menuActionHelp -> openFAQPix()
        }
    }

    private fun openFAQPix() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_PIX,
            subCategoryName = getString(R.string.cielo_facilita_central_de_ajuda_pix),
        )
    }

    private fun navigateToReceipt(transferResult: PixTransferResult) {
        findNavController().safeNavigate(
            PixQRCodePaymentSummaryFragmentDirections.actionPixQRCodePaymentSummaryFragmentToPixQRCodeReceiptFragment(
                transferResult,
                viewModel.pixDecodeQRCode.value?.pixType ?: PixQrCodeOperationType.TRANSFER,
            ),
        )
    }
}
