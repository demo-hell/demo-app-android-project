package br.com.mobicare.cielo.pix.ui.extract.detail

import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.SIMPLE_HOUR_MINUTE_SECOND
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixExtractDetailBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_EXTRACT_RECEIPT_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_IS_A_REVERSAL_TRANSACTION_ARGS
import br.com.mobicare.cielo.pix.domain.Credit
import br.com.mobicare.cielo.pix.domain.Fee
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.domain.ReversalReceiptsResponse
import br.com.mobicare.cielo.pix.domain.ScheduleCancelRequest
import br.com.mobicare.cielo.pix.domain.SchedulingDetailResponse
import br.com.mobicare.cielo.pix.domain.Settlement
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixExtractFilterEnum.QRCODE
import br.com.mobicare.cielo.pix.enums.PixExtractInformativeEnum
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.CANCELLED
import br.com.mobicare.cielo.pix.enums.TransactionTypeEnum
import br.com.mobicare.cielo.pix.ui.extract.detail.views.AmountViewSelector
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.PixInfringementNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val DEBIT = "-"

class PixExtractDetailFragment : BaseFragment(), PixExtractDetailContract.View,
    CieloNavigationListener {

    private val presenter: PixExtractDetailPresenter by inject {
        parametersOf(this)
    }
    private val isAReversalTransaction: Boolean? by lazy {
        arguments?.getBoolean(PIX_IS_A_REVERSAL_TRANSACTION_ARGS)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }
    private val pixExtractReceipt: PixExtractReceipt? by lazy {
        arguments?.getParcelable(PIX_EXTRACT_RECEIPT_ARGS)
    }

    private var navigation: CieloNavigation? = null
    private var _binding: FragmentPixExtractDetailBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixExtractDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        pixExtractReceipt?.let {
            if (isAReversalTransaction == true) {
                presenter.getReversalTransactionDetails(it.transactionCode.orEmpty())
            } else {
                presenter.getDetails(it.transactionCode, it.idEndToEnd, it.schedulingCode)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupListeners()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_toolbar_text_extract_detail))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
            controlVisibilityTransactionAnalyzeButton()
        }
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    private fun setTransactionType(@StringRes type: Int) {
        binding?.tvTransactionStatus?.text = getString(type)
    }

    private fun setTransferInformation(
        details: TransferDetailsResponse?,
        date: String?,
        isReceived: Boolean = false,
        isFeeExecutedType: Boolean = false
    ) {
        setupTransactionDate(date)

        binding?.apply {
            tvTransactionReceivedFrom.text =
                if (isReceived)
                    getString(R.string.text_pix_extract_detail_received_from)
                else if (isFeeExecutedType)
                    getString(R.string.text_pix_extract_detail_transfer_sent_to)
                else
                    getString(R.string.text_pix_extract_detail_sent_to)

            tvTransactionSender.text =
                if (isReceived) details?.debitParty?.name else details?.creditParty?.name

            val bank =
                if (isReceived) details?.debitParty?.bankName else details?.creditParty?.bankName
            val document =
                if (isReceived) details?.debitParty?.nationalRegistration else details?.creditParty?.nationalRegistration

            tvTransactionBankAndDocument.text = getString(
                R.string.text_pix_extract_detail_bank_and_document,
                bank,
                document
            )
        }
    }

    private fun setMessage(
        answerMessage: String?,
        @StringRes messageTitle: Int = R.string.text_pix_extract_detail_message_sent
    ) {
        binding?.apply {
            answerMessage?.let { itMessage ->
                if (itMessage != EMPTY) {
                    tvTransactionMessageReceivedSent.visible()
                    tvTransactionMessage.visible()
                    tvTransactionMessage.text =
                        getString(R.string.quoted_label, itMessage)
                }
            }

            tvTransactionMessageReceivedSent.text =
                getString(messageTitle)
        }
    }

    private fun setupTransactionDate(
        date: String?,
        isAScheduledTransaction: Boolean = false,
        isAnIncompleteTransaction: Boolean = false,
        isAPendingReversalTransaction: Boolean = false,
        isAScheduledCanceledTransaction: Boolean = false,
        isAStartedTransaction: Boolean = false,
    ) {
        val formattedDate = getFormattedDate(date)
        val hour = getFormattedHour(date)

        binding?.tvTransactionDatetime?.text =
            when {
                isAScheduledTransaction ->
                    getString(R.string.text_pix_scheduled_date, formattedDate)

                isAnIncompleteTransaction ->
                    getString(R.string.text_pix_transfer_failed_receipt_date, formattedDate, hour)

                isAPendingReversalTransaction ->
                    getString(R.string.text_pix_pending_transfer_receipt_date, formattedDate, hour)

                isAScheduledCanceledTransaction ->
                    getString(
                        R.string.text_pix_scheduling_canceled_receipt_date,
                        formattedDate,
                        hour
                    )

                isAStartedTransaction ->
                    getString(R.string.text_pix_transfer_started_receipt_date, formattedDate, hour)

                else ->
                    getString(R.string.text_pix_transfer_receipt_date, formattedDate, hour)
            }
    }

    private fun getFormattedDate(date: String?) =
        date?.clearDate()?.formatterDate(LONG_TIME_NO_UTC) ?: EMPTY

    private fun getFormattedHour(date: String?) = date?.clearDate()
        ?.isoDateToBrHourAndMinute(LONG_TIME_NO_UTC, SIMPLE_HOUR_MINUTE_SECOND) ?: EMPTY

    private fun showReceiptVisibility(isVisible: Boolean = true) {
        binding?.showReceipt?.visible(isVisible)
    }

    private fun setBannerInProgress() {
        setBannerInformation(
            R.string.text_pix_extract_detail_processing_title,
            R.string.text_pix_extract_detail_processing_content,
        )
    }

    private fun setBannerUncompleted(
        @StringRes title: Int = R.string.text_pix_extract_detail_failed_title,
        @StringRes content: Int = R.string.text_pix_extract_detail_failed_content
    ) {
        binding?.bannerContainer?.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.background_radius8dp_display_100)
        binding?.tvBannerTitle?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.display_500
            )
        )

        setBannerInformation(
            title,
            content
        )
    }

    private fun setBannerScheduled() {
        binding?.apply {
            bannerContainer.visible()
            bannerContainer.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.background_blue_corners_8dp)

            tvBannerTitle.text = getString(R.string.text_pix_extract_detail_scheduled_title)
            tvBannerTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_204986
                )
            )
            tvBannerContent.text = getString(R.string.text_pix_extract_detail_scheduled_content)
        }
    }

    private fun strikethrough() {
        binding?.apply {
            applyStrikeToText(
                tvTransactionStatus,
                tvTransactionSender,
                tvTransactionBankAndDocument,
                tvChannelUsedLabel,
                tvChannelUsedValue,
                tvMerchantLabel,
                tvMerchantValue
            )
        }
    }

    private fun applyStrikeToText(vararg textView: TextView?) {
        textView.forEach {
            it?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    private fun setBannerInformation(
        @StringRes title: Int,
        @StringRes text: Int
    ) {
        binding?.apply {
            bannerContainer.visible()
            tvBannerTitle.text = getString(title)
            tvBannerContent.text = Html.fromHtml(getString(text))
        }
    }

    private fun setAdditionalInformation(
        typeInformative: PixExtractInformativeEnum,
        details: TransferDetailsResponse
    ) {
        binding?.apply {
            informativeTransactionGroup.visible()
            tvChannelUsedValue.text = details.originChannel
            tvMerchantValue.text = details.merchantNumber
            tvTitleInformative.text = getString(typeInformative.titleInformative)
            tvChannelUsedLabel.text = getString(typeInformative.channelUsedLabel)
            tvMerchantLabel.text = getString(typeInformative.merchantLabel)
        }
    }

    private fun isHome() = navigation?.getData() as? Boolean ?: true

    private fun setupCancelSchedule() {
        binding?.separator?.visible()

        binding?.transactionLink?.apply {
            visible()
            text = getString(R.string.text_pix_extract_detail_transaction_cancel_schedule)
            setOnClickListener {
                CieloDialog.create(
                    getString(R.string.text_pix_dialog_cancel_scheduled_pix_title),
                    getString(R.string.text_pix_dialog_cancel_scheduled_pix_message)
                ).closeButtonVisible(true)
                    .setTitleTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                    .setMessageTextAlignment(View.TEXT_ALIGNMENT_TEXT_START)
                    .setPrimaryButton(getString(R.string.back))
                    .setSecondaryButton(getString(R.string.text_pix_transfer_cancel))
                    .setOnSecondaryButtonClickListener {
                        prepareRequestToCancel(true)
                    }
                    .show(
                        requireActivity().supportFragmentManager,
                        PixExtractDetailFragment::class.simpleName
                    )
            }
        }
    }

    private fun setGoToReversalListener(
        details: TransferDetailsResponse,
        reversalReceipts: ReversalReceiptsResponse
    ) {
        binding?.tvGoToReversal?.setOnClickListener {
            findNavController().navigate(
                PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixReversalFragment(
                    false,
                    pixExtractReceipt?.transactionCode ?: EMPTY,
                    pixExtractReceipt?.idEndToEnd ?: EMPTY,
                    balance.toString(),
                    details,
                    reversalReceipts
                )
            )
        }
    }

    private fun selectReceiptFlow(scheduledTransaction: SchedulingDetailResponse? = null) {
        binding?.showReceipt?.setOnClickListener {
            scheduledTransaction?.let {
                goToScheduledReceipt(scheduledTransaction)
            } ?: run {
                if (isAReversalTransaction == true) goToReversalReceipt()
                else goToCommonReceipt()
            }
        }
    }

    private fun goToCommonReceipt() {
        findNavController().navigate(
            PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixTransferReceiptFragment(
                EMPTY,
                pixExtractReceipt?.transactionCode ?: EMPTY,
                pixExtractReceipt?.idEndToEnd ?: EMPTY,
                presenter.getUpdatedDetailsResponse()
            )
        )
    }

    private fun goToReversalReceipt() {
        findNavController().navigate(
            PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixReversalReceiptFragment(
                pixExtractReceipt?.transactionCode.orEmpty()
            )
        )
    }

    private fun goToScheduledReceipt(scheduledTransaction: SchedulingDetailResponse?) {
        findNavController().navigate(
            PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixTransferScheduledReceiptFragment(
                scheduledTransaction,
                scheduledTransaction?.schedulingCode,
                EMPTY
            )
        )
    }

    private fun toBack() {
        findNavController().navigate(
            PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixExtractFragment()
        )
    }

    override fun showError(error: ErrorMessage?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.back),
                isFullScreen = true
            )
    }

    override fun showRefundsHistoryError(transferDetails: TransferDetailsResponse?) {
        transferDetails?.let { itTransfer ->
            setReversalData(itTransfer)
        }

        binding?.apply {
            llReversalReceipts.visible()
            tryAgainContainerInclude.root.visible()
            tryAgainContainerInclude.containerTryAgain.visible()
            tryAgainContainerInclude.tvErrorLoadingPix.visible()
            tryAgainContainerInclude.tvErrorLoadingPix.text =
                getString(R.string.text_pix_history_refunds_error)

            rvReversalHistory.gone()
            availableToRefundSeparator.gone()
            llAvailableToRefund.gone()

            tryAgainContainerInclude.containerTryAgain.setOnClickListener {
                presenter.getReceipts(pixExtractReceipt?.idEndToEnd, true)
            }
        }
    }

    override fun onClickSecondButtonError() {
        toBack()
    }

    override fun onActionSwipe() {
        toBack()
    }

    private fun setAmount(
        details: TransferDetailsResponse,
        transactionType: TransactionTypeEnum? = null,
        applyStrikeThrough: Boolean = false
    ) {
        binding?.containerAmount?.addView(
            AmountViewSelector(layoutInflater, details, transactionType, applyStrikeThrough).run {
                setOnFeeClickListener(::onFeeAmountClick)
                setOnNetAmountClickListener(::onNetAmountClick)
                setOnReceivingAmountClickListener(::onReceivingAmountClick)
                setOnReceivingDetailsClickListener(::onReceivingAmountDetailsClick)
                invoke()
            }
        )
    }

    private fun onFeeAmountClick(fee: Fee?) {
        if (fee == null) return
        navigateToDetails(fee.feeIdEndToEnd, fee.feeTransactionCode)
    }

    private fun onNetAmountClick(settlement: Settlement?) {
        if (settlement == null) return
        navigateToDetails(settlement.settlementIdEndToEnd, settlement.settlementTransactionCode)
    }

    private fun onReceivingAmountClick(credit: Credit?) {
        if (credit == null) return
        navigateToDetails(credit.creditIdEndToEnd, credit.creditTransactionCode)
    }

    private fun onReceivingAmountDetailsClick(credit: Credit?) {
        if (credit == null) return
        navigateToDetails(credit.creditIdEndToEnd, credit.creditTransactionCode)
    }

    private fun navigateToDetails(idEndToEnd: String?, transactionCode: String?) {
        findNavController().navigate(
            PixExtractDetailFragmentDirections.actionPixExtractDetailFragmentToPixExtractDetailFragment(
                false,
                PixExtractReceipt(
                    idEndToEnd = idEndToEnd,
                    transactionCode = transactionCode
                ),
                balance.toString()
            )
        )
    }

    override fun onQRCodeTransferReceived(details: TransferDetailsResponse) {
        setAmount(details)
        showReceiptVisibility()
        setTransferInformation(details, details.transactionDate, true)
        setMessage(details.payerAnswer, R.string.text_pix_extract_detail_message_received)
        setTransactionType(R.string.text_pix_extract_detail_status_title_qr_code_pix_receive)
        setAdditionalInformation(PixExtractInformativeEnum.RECEIVING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeTransferSent(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        showReceiptVisibility()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        setTransactionType(R.string.screen_text_read_qr_code_summary_pay_type)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeTransferInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setBannerInProgress()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_read_qr_code_summary_pay_type)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeTransferCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_read_qr_code_summary_pay_type)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeChangeTransferReceived(details: TransferDetailsResponse) {
        setAmount(details)
        showReceiptVisibility()
        setTransferInformation(details = details, date = details.transactionDate, isReceived = true)
        setMessage(details.payerAnswer, R.string.text_pix_extract_detail_message_received)
        setTransactionType(R.string.text_pix_extract_detail_status_title_qr_code_troco_receive)
        setAdditionalInformation(PixExtractInformativeEnum.RECEIVING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeChangePaymentSent(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        showReceiptVisibility()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        setTransactionType(R.string.screen_text_change_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeChangePaymentInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setBannerInProgress()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_change_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeChangePaymentCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_change_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeWithdrawalTransferReceived(details: TransferDetailsResponse) {
        setAmount(details)
        showReceiptVisibility()
        setTransferInformation(details = details, date = details.transactionDate, isReceived = true)
        setMessage(details.payerAnswer, R.string.text_pix_extract_detail_message_received)
        setTransactionType(R.string.text_pix_extract_detail_status_title_qr_code_saque_receive)
        setAdditionalInformation(PixExtractInformativeEnum.RECEIVING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeWithdrawalTransferSent(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        showReceiptVisibility()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        setTransactionType(R.string.screen_text_withdraw_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeWithdrawalTransferInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setBannerInProgress()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_withdraw_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onQRCodeWithdrawalTransferCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.screen_text_withdraw_qr_code_receipt)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onFeeTransferSent(details: TransferDetailsResponse) {
        setAmount(details)
        setTransferInformation(details, details.transactionDate, isFeeExecutedType = true)
        setMessage(details.payerAnswer, R.string.text_pix_extract_detail_message_received)
        setTransactionType(R.string.text_pix_extract_detail_status_title_fee)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        showReceiptVisibility()
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onFeeTransferInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setAmount(details)
        setBannerInProgress()
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.transactionDate, isAStartedTransaction = true)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.text_pix_extract_detail_status_title_fee)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onFeeTransferCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        setTransactionType(R.string.text_pix_extract_detail_status_title_fee)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onAutomaticTransferSent(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_send)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        showReceiptVisibility()
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onAutomaticTransferInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setAmount(details)
        setBannerInProgress()
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.credit?.creditTransactionDate, isAStartedTransaction = true)
        setTransactionType(R.string.text_pix_extract_detail_status_title_processing_transfer)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        showReceiptVisibility(isVisible = false)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onAutomaticTransferCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_send)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        showReceiptVisibility(isVisible = false)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onTransferReceived(details: TransferDetailsResponse) {
        setAmount(details)
        showReceiptVisibility()
        setTransferInformation(details = details, date = details.transactionDate, isReceived = true)
        setMessage(details.payerAnswer, R.string.text_pix_extract_detail_message_received)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_receive)
        setAdditionalInformation(PixExtractInformativeEnum.RECEIVING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onTransferSent(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        showReceiptVisibility()
        setAmount(details)
        setTransferInformation(details, details.transactionDate)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_send)
        setAdditionalInformation(PixExtractInformativeEnum.SENDING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onTransferInProcess(details: TransferDetailsResponse) {
        setMessage(details.payerAnswer)
        setAmount(details)
        setBannerInProgress()
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_send)
        setAdditionalInformation(PixExtractInformativeEnum.PROCESSING, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onTransferCancel(details: TransferDetailsResponse) {
        strikethrough()
        setBannerUncompleted()
        setMessage(details.payerAnswer)
        setAmount(details, applyStrikeThrough = true)
        setTransferInformation(details, details.transactionDate)
        showReceiptVisibility(isVisible = false)
        setupTransactionDate(details.transactionDate, isAnIncompleteTransaction = true)
        setTransactionType(R.string.text_pix_extract_detail_status_title_pix_send)
        setAdditionalInformation(PixExtractInformativeEnum.FAILED, details)
        selectReceiptFlow()
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onPixScheduled(scheduledTransaction: SchedulingDetailResponse?) {
        setBannerScheduled()
        setAmount(
            TransferDetailsResponse(finalAmount = scheduledTransaction?.finalAmount),
            transactionType = TransactionTypeEnum.TRANSFER_DEBIT,
            applyStrikeThrough = scheduledTransaction?.status == CANCELLED.name
        )
        showReceiptVisibility((scheduledTransaction?.status == CANCELLED.name).not())
        setTransactionType(
            if (scheduledTransaction?.transactionType?.contains(QRCODE.name) == true)
                R.string.text_pix_scheduled_payment
            else R.string.text_pix_extract_card_scheduled
        )
        selectReceiptFlow(scheduledTransaction)
        setRecipientInfo(scheduledTransaction)
        if (scheduledTransaction?.status == CANCELLED.name) {
            setPixScheduledCanceled()
            setupTransactionDate(
                scheduledTransaction?.schedulingCancellationDate,
                isAScheduledCanceledTransaction = true
            )
        } else {
            setupTransactionDate(
                scheduledTransaction?.schedulingDate,
                isAScheduledTransaction = true
            )
            if (isHome().not()) setupCancelSchedule()
        }

        controlVisibilityTransactionAnalyzeButton()
    }

    private fun setPixScheduledCanceled() {
        binding?.apply {
            transactionLink.gone()
            separator.gone()
        }
        setBannerUncompleted(
            title = R.string.text_pix_dialog_cancel_scheduled_pix,
            content = R.string.text_pix_dialog_cancel_scheduled_pix_success
        )
        strikethrough()
    }

    override fun onReversalCompletedTransaction(
        details: TransferDetailsResponse?,
        wasReceived: Boolean
    ) {
        details?.let {
            setAmount(
                details,
                if (wasReceived)
                    TransactionTypeEnum.TRANSFER_CREDIT
                else
                    TransactionTypeEnum.TRANSFER_DEBIT
            )
        }
        setMessage(details?.payerAnswer, R.string.tv_reason_title_receipt_reversal)
        showReceiptVisibility()
        setTransferInformation(details, details?.transactionDate, wasReceived)
        setTransactionType(if (wasReceived) R.string.pix_received_reversal else R.string.pix_sent_reversal)
        selectReceiptFlow()
        controlOriginalTransactionVisibility(isButtonVisible = true, details)
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onFailedReversalTransaction(
        details: TransferDetailsResponse?,
        wasReceived: Boolean
    ) {
        setMessage(details?.payerAnswer, R.string.tv_reason_title_receipt_reversal)
        showReceiptVisibility(false)
        strikethrough()
        details?.let { setAmount(it, applyStrikeThrough = true) }
        setTransferInformation(details, details?.transactionDate, wasReceived)
        setTransactionType(if (wasReceived) R.string.pix_received_reversal else R.string.pix_sent_reversal)
        setupTransactionDate(details?.transactionDate, isAnIncompleteTransaction = true)
        selectReceiptFlow()
        controlOriginalTransactionVisibility(isButtonVisible = false, details)
        setBannerUncompleted()
        details?.let {
            setAdditionalInformation(PixExtractInformativeEnum.FAILED, it)
        }
        controlVisibilityTransactionAnalyzeButton()
    }

    override fun onPendingReversalTransaction(
        details: TransferDetailsResponse?,
        wasReceived: Boolean
    ) {
        setMessage(details?.payerAnswer, R.string.tv_reason_title_receipt_reversal)
        showReceiptVisibility(false)
        setupTransactionDate(details?.transactionDate, isAPendingReversalTransaction = true)
        details?.let { setAmount(it) }
        setTransferInformation(details, details?.transactionDate, wasReceived)
        setTransactionType(if (wasReceived) R.string.pix_received_reversal else R.string.pix_sent_reversal)
        selectReceiptFlow()
        controlOriginalTransactionVisibility(isButtonVisible = false, details)
        setBannerInProgress()
        controlVisibilityTransactionAnalyzeButton()
    }

    private fun controlOriginalTransactionVisibility(
        isButtonVisible: Boolean = false,
        details: TransferDetailsResponse?,
    ) {
        binding?.separator?.visible(isButtonVisible)
        binding?.transactionLink?.apply {
            visible(isButtonVisible)
            text = getString(R.string.pix_view_original_transaction)
            setOnClickListener {
                binding?.transactionLink?.gone()
                presenter.getDetails(
                    transactionCode = details?.transactionCodeOriginal,
                    idEndToEnd = pixExtractReceipt?.idEndToEndOriginal
                )
            }
        }
    }

    private fun setRecipientInfo(transaction: SchedulingDetailResponse?) {
        binding?.apply {
            tvTransactionReceivedFrom.text = getString(R.string.text_pix_extract_detail_sent_to)
            tvTransactionSender.text = transaction?.payeeName
            tvTransactionBankAndDocument.text = getString(
                R.string.text_pix_extract_detail_bank_and_document,
                transaction?.payeeBankName,
                transaction?.payeeDocumentNumber
            )
        }
    }

    override fun onProcessError(onFirstAction: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onFirstAction.invoke()
            }
        })
    }

    private fun prepareRequestToCancel(
        isStartAnimation: Boolean
    ) {
        presenter.getSchedulingCode()?.let { itSchedulingCode ->
            validationTokenWrapper.generateOtp(showAnimation = isStartAnimation) { otpCode ->
                presenter.cancelTransactionScheduled(
                    itSchedulingCode,
                    otpCode,
                    ScheduleCancelRequest(itSchedulingCode)
                )
            }
        }
    }

    override fun onTransactionCancelSuccess(onAction: () -> Unit) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onAction.invoke()
                }
            })
    }

    override fun setReversalData(details: TransferDetailsResponse) {
        if (details.transactionType == TransactionTypeEnum.TRANSFER_DEBIT.name || isHome()) {
            binding?.llReversal.gone()
            return
        }
        binding?.separator.visible()
        binding?.llReversal.visible()
        setReversalMessage(details)
    }

    override fun showReversalReceiptsLoading() {
        binding?.pbReversalReceipts.visible()
    }

    override fun showReversalTryAgainLoading() {
        binding?.tryAgainContainerInclude?.containerTryAgain?.gone()
        binding?.tryAgainContainerInclude?.tvErrorLoadingPix?.gone()
        binding?.pbRefundsErrorLoading?.visible()
    }

    override fun hideReversalTryAgainLoading() {
        binding?.pbRefundsErrorLoading?.gone()
    }

    override fun hideReversalReceiptsLoading() {
        binding?.pbReversalReceipts.gone()
    }

    override fun setReversalReceipts(receiptsResponse: ReversalReceiptsResponse) {
        binding?.apply {
            if (receiptsResponse.totalAmountPossibleReversal == ZERO_DOUBLE) {
                tvRefundFull.visible()
            }
            if (presenter.canGoToReversal(isHome())) {
                tvGoToReversal.visible()
                availableToRefundSeparator.visible()
                llAvailableToRefund.visible()
                tvAvailableRefundAmount.text =
                    receiptsResponse.totalAmountPossibleReversal?.toPtBrRealString()

                presenter.transferDetails?.let {
                    setGoToReversalListener(it, receiptsResponse)
                }
            }
            rvReversalHistory.adapter = context?.let { PixReversalHistoryAdapter(it) }
            receiptsResponse.items.firstOrNull()?.receipts?.let {
                if (it.isNotEmpty())
                    llReversalReceipts.visible()
                rvReversalHistory.visible()
                (rvReversalHistory.adapter as PixReversalHistoryAdapter).populateList(it)
            }
        }
    }

    private fun setReversalMessage(details: TransferDetailsResponse) {
        binding?.tvRefundExpired.visible(details.expiredReversal == true)

        val transactionDate =
            details.transactionReversalDeadline
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat(LONG_TIME_NO_UTC, Locale.getDefault())
        transactionDate?.let {
            calendar.time = sdf.parse(it)
        }

        val day = calendar.time
        val dayFormatter = SimpleDateFormat(SIMPLE_DT_FORMAT_MASK, Locale.getDefault())
        val hour = calendar.time
        val hourFormatter = SimpleDateFormat(SIMPLE_HOUR_MINUTE_SECOND, Locale.getDefault())

        binding?.tvRefundMessage?.text = getString(
            R.string.pix_reversal_due_date_message,
            hourFormatter.format(hour),
            dayFormatter.format(day)
        )
    }

    private fun setupListeners() {
        pixExtractReceipt?.let { receipt ->
            binding?.btnTransactionAnalyse?.setOnClickListener {
                requireActivity().startActivity<PixInfringementNavigationFlowActivity>(
                    PixConstants.PIX_ID_END_TO_END_ARGS to receipt.idEndToEnd
                )
            }
        }
    }

    private fun controlVisibilityTransactionAnalyzeButton() {
        binding?.llButtonFooter?.visible(presenter.isShowTransactionAnalyseButton())
    }

}