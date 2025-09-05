package br.com.mobicare.cielo.pix.ui.transfer.summary

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.constants.MAXIMUM_90_DAYS
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCNPJ
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.databinding.FragmentPixTransferSummaryBinding
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pixMVVM.analytics.PixAnalytics
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum.CNPJ
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum.CPF
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.util.*

class PixTransferSummaryFragment : BaseFragment(), CieloNavigationListener,
    PixTransferSummaryContract.View, AllowMeContract.View {

    private val presenter: PixTransferSummaryPresenter by inject {
        parametersOf(this)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    private val keyInformation: ValidateKeyResponse? by lazy {
        arguments?.getParcelable(PIX_KEY_INFORMATION_ARGS)
    }
    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }
    private val manualTransferPayee: ManualPayee? by lazy {
        arguments?.getParcelable(PIX_PAYEE_ARGS)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private var binding: FragmentPixTransferSummaryBinding? = null
    private var navigation: CieloNavigation? = null
    private var isTryAgain = false
    private var isABankTransfer = false
    private var chosenScheduleDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentPixTransferSummaryBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        manualTransferPayee?.let {
            isABankTransfer = true
        }

        setupNavigation()
        setupView()
        logScreenView()
    }

    override fun onResume() {
        super.onResume()
        isTryAgain = false
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun logScreenView() {
        PixAnalytics.logScreenView(PixAnalytics.ScreenView.TRANSFER)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.toolbar_screen_transfer_pix))
            navigation?.setTextButton(getString(R.string.text_pix_transaction_transfer))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.enableButton(isEnabled = false)
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun defineTransferFlow() {
        val mAllowMeContextual = allowMePresenter.init(requireContext())
        allowMePresenter.collect(
            mAllowMeContextual = mAllowMeContextual,
            requireActivity(),
            mandatory = true,
            hasAnimation = true
        )
    }

    private fun setupView() {
        setDate()
        setAmount()
        validateAmount()
        setupFees()
        setMessage()
        setupCancel()
        setupDestination()
        changeAmount()
    }

    private fun setAmount() {
        val balance = balance ?: DEFAULT_BALANCE
        PixEnterTransferAmountBottomSheet.onCreate(
            object : PixEnterTransferAmountContract.Result {
                override fun onAmount(amount: Double) {
                    binding?.containerTransferInformation?.tvValue?.text = amount.toPtBrRealString()
                    validateAmount(amount)
                    setupFees(amount)
                }
            },
            balance = balance,
            amount = getAmount()
        ).show(childFragmentManager, tag)
    }

    private fun setMessage() {
        binding?.containerTransferInformation?.containerPaymentInformation?.apply{
            tvMessage.setOnClickListener {
                PixEnterTransferMessageBottomSheet.onCreate(
                    object : PixEnterTransferMessageContract {
                        override fun onMessage(message: String) {
                            tvMessage.text = message.ifEmpty {
                                getString(R.string.text_pix_summary_transfer_insert_msg_hint)
                            }
                        }
                    },
                    tvMessage.text?.toString() ?: EMPTY
                ).show(childFragmentManager, tag)
            }
        }
    }

    private fun setDate() {
        binding?.containerTransferInformation?.
        containerPaymentInformation?.tvDate?.apply {
            text = getString(
                R.string.screen_text_transfer_summary_set_scheduled_date_today,
                Date().currency()
            )
            setTextAppearance(requireContext(), R.style.label_16_brand_400_ubuntu_bold)
            compoundDrawables[ZERO]?.setTint(resources.getColor(R.color.brand_400))
            setOnClickListener {
                openCalendar()
            }
        }
    }

    private fun openCalendar() {
        CalendarDialogCustom(
            ZERO,
            MAXIMUM_90_DAYS,
            ZERO,
            ZERO,
            ZERO,
            ZERO,
            getString(R.string.screen_text_transfer_summary_set_scheduled_date),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                val dataFilter = DataCustom(year, monthOfYear, dayOfMonth)
                val formattedDate = dataFilter.formatBrDateNowOrFuture()
                chosenScheduleDate = dataFilter.formatDate()

                binding?.containerTransferInformation?.
                containerPaymentInformation?.tvDate?.
                text = setupDateTitleToShow(
                    if (DateUtils.isToday(dataFilter.toDate().time)) {
                        chosenScheduleDate = null
                        R.string.screen_text_transfer_summary_set_scheduled_date_today
                    } else R.string.screen_text_transfer_summary_set_scheduled_date_future,
                    formattedDate
                )
            },
            R.style.DialogThemeMeusRecebimentos
        ).show()
    }

    private fun setupDateTitleToShow(@StringRes text: Int, date: String) = getString(text, date)

    private fun getMessage(): String? =
        if (binding?.containerTransferInformation?.
            containerPaymentInformation?.tvMessage?.text?.toString() == getString(R.string.text_pix_summary_transfer_insert_msg_hint)) null else binding?.containerTransferInformation?.
        containerPaymentInformation?.tvMessage?.text?.toString()

    private fun getAmount(): Double =
        binding?.containerTransferInformation?.tvValue?.
        text?.toString()?.moneyToDoubleValue() ?: ZERO_DOUBLE

    private fun changeAmount() {
        binding?.containerTransferInformation?.tvValue?.setOnClickListener {
            setAmount()
        }
    }

    private fun validateAmount(amount: Double = ZERO_DOUBLE) {
        binding?.containerTransferInformation?.containerTaxInformation?.apply {
            if (amount > ZERO_DOUBLE) {
                tvErrorAmount.gone()
                containerTax.visible()
            } else {
                tvErrorAmount.gone()
                containerTax.visible()
            }
        }
        navigation?.enableButton(amount > ZERO_DOUBLE)
    }

    private fun setupFees(amount: Double? = null) {
        val total = amount ?: getAmount()
        binding?.containerTransferInformation?.containerTaxInformation?.apply {
            tvTotalTransfer.text = HtmlCompat.fromHtml(
                getString(R.string.text_pix_summary_transfer_total, total.toPtBrRealString()),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            tvRatePix.text = HtmlCompat.fromHtml(
                getString(
                    R.string.text_pix_summary_transfer_rate,
                    getString(R.string.text_pix_transfer_receipt_free)
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    }

    private fun setupCancel() {
        binding?.tvCancelTransaction?.setOnClickListener {
            CieloAskQuestionDialogFragment
                .Builder()
                .title(getString(R.string.text_pix_transfer_cancel_title))
                .message(getString(R.string.text_pix_transfer_cancel_message))
                .cancelTextButton(getString(R.string.text_pix_transfer_cancel))
                .positiveTextButton(getString(R.string.back))
                .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
                .build().let {
                    it.onCancelButtonClickListener = View.OnClickListener {
                        toHome()
                    }
                    it.show(childFragmentManager, PixTransferSummaryFragment::class.java.simpleName)
                }
        }
    }

    private fun setupDestination() {
        keyInformation?.let {
            showKeyTransferInfo()
        } ?: run {
            manualTransferPayee?.let {
                showBankTransferInfo()
            }
        }
    }

    private fun showKeyTransferInfo() {
        binding?.containerTransferInformation?.containerDestinationInformation?.apply {
            tvDestinationName.text = keyInformation?.ownerName
            tvDestinationBank.text = keyInformation?.participantName
            tvTypedKeyValue.text = keyInformation?.key
            tvTypedKeyValue.setCompoundDrawablesWithIntrinsicBounds(getIconKeyPix(keyInformation?.keyType), ZERO, ZERO, ZERO)
            tvDestinationKey.text = keyInformation?.ownerDocument
            isCNPJ(keyInformation?.ownerDocument).also { isTheDocumentCNPJ ->
                tvDestinationKeyTitle.text = getString(if (isTheDocumentCNPJ) R.string.text_pix_my_keys_cnpf else R.string.access_manager_cpf)
                ivDestinationKey.setBackgroundResource(getIconKeyPix(if (isTheDocumentCNPJ) CNPJ.name else CPF.name))
            }
        }

        binding?.containerTransferInformation?.containerPaymentInformation?.tvToBeDebitedTitle.gone()
        binding?.containerTransferInformation?.containerPaymentInformation?.tvToBeDebitedSubtitle.gone()
    }

    private fun showBankTransferInfo() {
        val documentType =
            if (manualTransferPayee?.documentNumber?.length == ELEVEN) getString(R.string.cpf_title)
            else getString(R.string.cnpj_title)

        binding?.containerTransferInformation?.containerDestinationInformation?.apply{
            tvDestinationName.text = manualTransferPayee?.name
            tvDestinationBank.text = manualTransferPayee?.bankName
            tvDestinationKeyTitle.text = documentType
            tvDestinationKey.text = manualTransferPayee?.documentNumber
            ivDestinationKey.setBackgroundResource(getIconKeyPix(documentType))
            typedKeyGroup.gone()
        }
        binding?.containerTransferInformation?.containerPaymentInformation?.tvToBeDebitedTitle.visible()
        binding?.containerTransferInformation?.containerPaymentInformation?.tvToBeDebitedSubtitle.visible()
        binding?.containerTransferInformation?.containerDestinationInformation?.
        tvDestinationAgencyAndAccount?.apply {
            visible()
            text = getString(
                R.string.agency_and_account_bank_value,
                manualTransferPayee?.bankBranchNumber,
                manualTransferPayee?.bankAccountNumber
            )
        }
    }

    private fun toHome() {
        findNavController().navigate(
            PixTransferSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixHomeFragment(
                PrepaidResponse()
            )
        )
    }

    private fun toReceipt(details: TransferDetailsResponse, transferResponse: PixTransferResponse) {
        findNavController().navigate(
            PixTransferSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixTransferReceiptFragment(
                PIX_KEY,
                transferResponse.transactionCode ?: EMPTY,
                transferResponse.idEndToEnd ?: EMPTY,
                details
            )
        )
    }

    private fun toScheduledTransactionReceipt(scheduledTransactionInfo: PixExtractReceipt?) {
        findNavController().navigate(
            PixTransferSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixTransferScheduledReceiptFragment(
                null,
                scheduledTransactionInfo?.schedulingCode,
                PIX_HOME
            )
        )
    }

    private fun transactionReturn(
        @StringRes title: Int,
        message: String,
        @DrawableRes image: Int,
        isFull: Boolean = false
    ) {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            image,
            getString(title),
            message,
            nameBtn1Bottom = getString(R.string.back),
            nameBtn2Bottom = getString(R.string.back),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = isFull,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        toHome()
                    }

                    override fun onSwipeClosed() {
                        toHome()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun stopAnimationAllowMe(onAction: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            validationTokenWrapper.playAnimationError(callbackValidateToken =
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    onAction.invoke()
                }
            })
        }, ANIMATION_TIME)
    }

    private fun showAlert(message: String) {
        CieloAlertDialogFragment
            .Builder()
            .title(getString(R.string.dialog_title))
            .message(message)
            .closeTextButton(getString(R.string.dialog_button))
            .build().showAllowingStateLoss(
                requireActivity().supportFragmentManager,
                getString(R.string.text_cieloalertdialog)
            )
    }

    override fun onButtonClicked(labelButton: String) {
        defineTransferFlow()
    }

    override fun onSuccessFlow(action: () -> Unit) {
        isTryAgain = false
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    action.invoke()
                }
            })
    }

    override fun showBottomSheetSuccessfulTransaction(
        details: TransferDetailsResponse,
        transferResponse: PixTransferResponse
    ) {
        PixAnalytics.run {
            logScreenView(PixAnalytics.ScreenView.TRANSFER_SUCCESS)
            logTransactionPurchase(
                amount = getAmount(),
                transactionType = PixAnalytics.Values.TRANSFER
            )
        }

        val amount = getAmount().toPtBrRealString()
        val message = getString(
            R.string.text_pix_summary_transfer_success_message,
            amount,
            details.creditParty?.name ?: EMPTY
        )

        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.text_pix_summary_transfer_success_title),
            message,
            nameBtn1Bottom = getString(R.string.text_close),
            nameBtn2Bottom = getString(R.string.text_pix_summary_transfer_show_receipt),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        toReceipt(details, transferResponse)
                    }

                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        toHome()
                    }

                    override fun onSwipeClosed() {
                        toHome()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun showBottomSheetScheduledTransaction(
        scheduledTransactionInfo: PixExtractResponse
    ) {
        val scheduledItem = scheduledTransactionInfo.items?.first()?.receipts?.first()
        val payee = scheduledItem?.payeeName
        val amount = scheduledItem?.finalAmount?.toPtBrRealString()
        val message = getString(
            R.string.text_pix_summary_scheduled_transfer_success_message,
            amount,
            payee
        )

        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.text_pix_extract_detail_scheduled_title),
            message,
            nameBtn1Bottom = getString(R.string.text_close),
            nameBtn2Bottom = getString(R.string.text_pix_summary_transfer_show_receipt),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        toScheduledTransactionReceipt(scheduledItem)
                    }

                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        toHome()
                    }

                    override fun onSwipeClosed() {
                        toHome()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onTransactionInProcess() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    val message = getString(
                        R.string.text_pix_error_transaction_in_process_message,
                        getAmount().toPtBrRealString(),
                        keyInformation?.ownerName ?: manualTransferPayee?.name
                    )
                    transactionReturn(
                        R.string.text_pix_error_transaction_in_process_title,
                        message,
                        R.drawable.ic_29,
                        isFull = true
                    )
                }
            })
    }

    override fun onPixManyRequestsError(error: ErrorMessage?) {
        PixAnalytics.logException(
            screenView = PixAnalytics.ScreenView.TRANSFER,
            description = ERROR_CODE_TOO_MANY_REQUESTS,
            statusCode = error?.httpStatus?.toString()
        )
        validationTokenWrapper.playAnimationError(error = null, object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                HandlerViewBuilderFlui.Builder(requireContext())
                    .isShowButtonContained(true)
                    .isShowHeaderImage(true)
                    .isShowButtonBack(false)
                    .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                    .messageAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                    .headerImageContentDescription(getString(R.string.content_description_close_button))
                    .contentImage(R.drawable.ic_transfer_error_pix)
                    .title(getString(R.string.text_pix_to_many_requests_error_title_handlerview))
                    .titleStyle(R.style.TxtTitleDarkBlue)
                    .message(getString(R.string.text_pix_to_many_requests_error_body_handlerview))
                    .messageStyle(R.style.Paragraph_400_regular_16_display_400)
                    .labelContained(getString(R.string.close_handlerview))
                    .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                        override fun onClick(dialog: Dialog?) {
                            requireActivity().toHomePix()
                        }
                    })
                    .headerClickListener(object : HandlerViewBuilderFlui.HeaderOnClickListener {
                        override fun onClick(dialog: Dialog?) {
                            requireActivity().toHomePix()
                        }
                    })
                    .build()
                    .show(childFragmentManager, null)

            }
        })
    }

    override fun showError(error: ErrorMessage?) {
        PixAnalytics.logException(
            screenView = PixAnalytics.ScreenView.TRANSFER,
            statusCode = error?.httpStatus?.toString(),
            description = error?.message
        )

        isTryAgain = true

        validationTokenWrapper.playAnimationError(
            error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    if (error?.code != HTTP_FORBIDDEN.toString() || error.errorCode.contains(OTP)) {
                        val isAnInternalError = error?.code == HTTP_INTERNAL_ERROR.toString()

                        if (isAnInternalError) showInternalError()
                        else showCommonError(error)
                    }
                }
            })
    }

    private fun showCommonError(error: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_pix_summary_transfer_error_btn),
            error = processErrorMessage(
                error,
                getString(R.string.business_error),
                getString(R.string.text_pix_error_in_processing)
            ),
            title = getString(R.string.text_pix_summary_transfer_error_title),
            isFullScreen = false
        )
    }

    private fun showInternalError() {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_close),
            title = getString(R.string.text_pix_transfer_bs_error_title),
            subtitle = getString(R.string.text_pix_transfer_bs_error_subtitle),
            callToActionButton = { requireActivity().toHomePix()},
            callToActionSwiped = { requireActivity().toHomePix() },
            isFullScreen = false
        )
    }

    override fun onError(message: String?) {
        validationTokenWrapper.playAnimationError(null,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    val error = message ?: getString(R.string.text_pix_error_transaction_message)
                    transactionReturn(
                        R.string.text_pix_error_transaction_title,
                        error,
                        R.drawable.ic_transaction_error
                    )
                }
            })
    }

    override fun successCollectToken(result: String) {
        if (isABankTransfer) {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                presenter.onBankTransfer(
                    otpCode,
                    PixManualTransferRequest(
                        getAmount(),
                        getMessage(),
                        manualTransferPayee,
                        result,
                        chosenScheduleDate
                    )
                )
            }
        } else {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                presenter.onTransfer(
                    otpCode,
                    keyInformation,
                    getAmount(),
                    getMessage(),
                    result,
                    chosenScheduleDate
                )
            }
        }
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        stopAnimationAllowMe(onAction = { showAlert(errorMessage) })
    }

    override fun stopAction() {
        stopAnimationAllowMe(onAction = {
            dialogLocationActivation(
                requireActivity(),
                childFragmentManager
            )
        })
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}