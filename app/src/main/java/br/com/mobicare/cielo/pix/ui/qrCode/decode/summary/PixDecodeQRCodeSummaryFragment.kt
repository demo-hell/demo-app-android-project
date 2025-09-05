package br.com.mobicare.cielo.pix.ui.qrCode.decode.summary

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.DOUBLE
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
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.databinding.FragmentPixDecodeQrcodeSummaryBinding
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.PixTransferResponse
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.enums.PixOwnerTypeEnum
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixQRCodeTypeEnum
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

const val DEFAULT_AMOUNT = "-"
const val SCHEDULE = 90

class PixDecodeQRCodeSummaryFragment : BaseFragment(), CieloNavigationListener,
    PixDecodeQRCodeSummaryContract.View, AllowMeContract.View {

    private val presenter: PixDecodeQRCodeSummaryPresenter by inject {
        parametersOf(this)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private var navigation: CieloNavigation? = null
    private var decodeResponse: QRCodeDecodeResponse? = null
    private var balance: String? = null
    private var isChangeChargeAmount = true
    private var isAnimation = true
    private var isDecode = false
    private var amountTitle: Int = R.string.text_pix_enter_amount_title_pay

    private var _binding: FragmentPixDecodeQrcodeSummaryBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireActivity() is CieloNavigation)
            navigation = requireActivity() as CieloNavigation

        getData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixDecodeQrcodeSummaryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.screen_toolbar_text_read_qr_code))
            navigation?.setTextButton(getString(R.string.screen_text_read_qr_code_summary_pay_btn))
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(isShow = true)
            navigation?.showFirstButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        validateQRCodeType()
        setMessage()
        setupChargeAmount()
        setupFees()
        setupCancelTransaction()
        setDate()
    }

    private fun validateQRCodeType() {
        binding?.containerTransferInformation?.apply {
            containerDestinationInformation.typedKeyGroup.gone()
            containerPurchaseValue.gone()
        }

        if (decodeResponse?.type != PixQRCodeTypeEnum.DYNAMIC_COBV.name) {
            when (decodeResponse?.pixType) {
                PixQRCodeOperationTypeEnum.CHANGE.name -> payQRCodeChange()
                PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> payQRCodeWithdrawal()
                else -> payQRCode()
            }
        } else
            payQRCodeConv()
    }

    private fun payQRCodeConv() {
        payQRCode()
        setupDebtorInformation()
        setupDueDate()
        setupBillingDetails()
    }

    private fun payQRCodeWithdrawal() {
        amountTitle = R.string.text_pix_enter_amount_title_withdrawal
        setTitles(
            titleId = R.string.text_pix_summary_qrcode_withdrawal_title,
            titleValueId = R.string.text_pix_summary_qrcode_withdrawal_title_value,
            typeId = R.string.screen_text_withdraw_qr_code_receipt,
            dateId = R.string.text_pix_summary_pay_date_withdrawal
        )
        setupPurchaseValue()
        setupDestinationInformation()
        showImproperBilling()
    }

    private fun payQRCodeChange() {
        amountTitle = R.string.text_pix_enter_amount_title_change
        setTitles(
            titleId = R.string.text_pix_summary_qrcode_change_title,
            titleValueId = R.string.text_pix_summary_qrcode_change_title_value,
            typeId = R.string.screen_text_change_qr_code_receipt,
        )
        setupPurchaseValue(isVisible = true)
        setupDestinationInformation()
        showImproperBilling()
    }

    private fun payQRCode() {
        amountTitle = R.string.text_pix_enter_amount_title_pay
        setTitles()
        setupPurchaseValue()
        setupDestinationInformation(isShowAdditionalInformation = true)
    }

    private fun getData() {
        navigation?.getData()?.let {
            val intent = it as? Intent
            balance = intent?.getStringExtra(PIX_BALANCE_ARGS)
            decodeResponse = intent?.getParcelableExtra(PIX_DECODE_QRCODE_ARGS)
        }
    }

    private fun setupPurchaseValue(isVisible: Boolean = false) {
        binding?.containerTransferInformation?.containerPurchaseValue?.visible(isVisible)
        binding?.containerTransferInformation?.tvPurchaseValue?.text =
            decodeResponse?.originalAmount?.toPtBrRealString()
    }

    private fun setupChargeAmount() {
        decodeResponse?.let { decode ->
            val amount = presenter.getTransferValue(decode)
            isChangeChargeAmount = presenter.isAllowedChangeValue(decode)

            if (isChangeChargeAmount.not())
                binding?.containerTransferInformation?.tvValue?.apply {
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.display_400))
                    setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, ZERO, ZERO)
                }

            binding?.containerTransferInformation?.tvValue?.text = amount.toPtBrRealString()
            validateAmount(amount)
        }
        changeChargeAmount()
        setChargeAmount()
    }

    private fun setChargeAmount() {
        binding?.containerTransferInformation?.tvValue?.setOnClickListener {
            changeChargeAmount()
        }
    }

    private fun setMessage() {
        val tvMessage =
            binding?.containerTransferInformation?.containerPaymentInformation?.tvMessage
        tvMessage?.setOnClickListener {
            PixEnterTransferMessageBottomSheet.onCreate(
                object : PixEnterTransferMessageContract {
                    override fun onMessage(message: String) {
                        tvMessage.text =
                            message.ifEmpty { getString(R.string.text_pix_summary_transfer_insert_msg_hint) }
                    }
                },
                getMessage() ?: EMPTY
            ).show(childFragmentManager, tag)
        }
    }

    private fun setDate() {
        if (decodeResponse?.isSchedulable == true)
            binding?.containerTransferInformation?.containerPaymentInformation?.tvDate?.apply {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_0774E7))
                setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_calendar_pix_qr_code_16dp_blue,
                    ZERO,
                    ZERO,
                    ZERO
                )

                setOnClickListener {
                    openCalendar()
                }
            }
    }

    private fun openCalendar() {
        val maxDays = if (decodeResponse?.expireDate == null) SCHEDULE else null
        CalendarDialogCustom(
            ZERO,
            maxDays,
            ZERO,
            ZERO,
            ZERO,
            ZERO,
            getString(R.string.screen_text_read_qr_code_summary_pay_set_date),
            context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                val dataFilter = DataCustom(year, monthOfYear, dayOfMonth)
                if (decodeResponse?.type == PixQRCodeTypeEnum.DYNAMIC_COBV.name)
                    presenter.onDecode(decodeResponse?.qrCode, dataFilter.formatDate())
                else
                    binding?.containerTransferInformation?.containerPaymentInformation?.tvDate?.text =
                        dataFilter.formatBrDateNowOrFuture()
            },
            R.style.DialogThemeMeusRecebimentos,
            endCalendar = decodeResponse?.expireDate?.calendarDate()
        ).show()
    }

    private fun changeChargeAmount() {
        if (isChangeChargeAmount) {
            val balance = balance ?: DEFAULT_BALANCE
            PixEnterTransferAmountBottomSheet.onCreate(
                object : PixEnterTransferAmountContract.Result {
                    override fun onAmount(amount: Double) {
                        binding?.containerTransferInformation?.tvValue?.text =
                            amount.toPtBrRealString()
                        validateAmount(amount)
                    }
                },
                balance,
                getAmount(),
                getString(amountTitle)
            ).show(childFragmentManager, tag)
        }
    }

    private fun getMessage(): String? {
        val message =
            binding?.containerTransferInformation?.containerPaymentInformation?.tvMessage?.text?.toString()
        return if (message == getString(
                R.string.text_pix_summary_transfer_insert_msg_hint
            )
        ) null else message
    }

    private fun getAmount(): Double =
        binding?.containerTransferInformation?.tvValue?.text?.toString()?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun getPurchaseAmount(): Double =
        binding?.containerTransferInformation?.tvPurchaseValue?.text?.toString()
            ?.moneyToDoubleValue()
            ?: ZERO_DOUBLE

    private fun validateAmount(amount: Double? = ZERO_DOUBLE) {
        val tvErrorAmount =
            binding?.containerTransferInformation?.containerTaxInformation?.tvErrorAmount
        val containerTax =
            binding?.containerTransferInformation?.containerTaxInformation?.containerTax
        amount?.let {
            val isValid = it > ZERO_DOUBLE
            if (isValid) {
                tvErrorAmount?.gone()
                containerTax?.visible()

            } else {
                containerTax?.gone()
                tvErrorAmount?.visible()
            }
            navigation?.enableButton(isValid)
        }
        setupFees(amount)
    }

    private fun setupFees(amount: Double? = null) {
        val enteredAmount = amount ?: getAmount()
        val total = if (decodeResponse?.pixType == PixQRCodeOperationTypeEnum.CHANGE.name)
            getTotalValueChange(enteredAmount, getPurchaseAmount())
        else enteredAmount

        binding?.containerTransferInformation?.containerTaxInformation?.tvTotalTransfer?.text =
            HtmlCompat.fromHtml(
                getString(
                    R.string.text_pix_summary_transfer_total,
                    total.toPtBrRealString()
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        //TODO when you have rates in decoding set fee here
        val fee = getString(R.string.text_pix_transfer_receipt_free)
        binding?.containerTransferInformation?.containerTaxInformation?.tvRatePix?.text =
            HtmlCompat.fromHtml(
                getString(R.string.text_pix_summary_transfer_rate, fee),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
    }

    private fun setupDestinationInformation(isShowAdditionalInformation: Boolean = false) {
        decodeResponse?.let { decode ->
            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationName?.text =
                decode.receiverName

            val isShowBank = presenter.getBankName(decode) != null
            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationBank?.visible(
                isShowBank
            )
            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationBankTitle?.visible(
                isShowBank
            )

            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationBank?.text =
                presenter.getBankName(decode)

            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationKeyTitle?.text =
                if (decode.receiverPersonType == PixOwnerTypeEnum.LEGAL_PERSON.name)
                    PixOwnerTypeEnum.LEGAL_PERSON.owner
                else
                    PixOwnerTypeEnum.NATURAL_PERSON.owner

            binding?.containerTransferInformation?.containerDestinationInformation?.tvDestinationKey?.text =
                decode.receiverDocument

            binding?.containerTransferInformation?.containerDestinationInformation?.ivDestinationKey?.setBackgroundResource(
                R.drawable.ic_cnh_pix_16dp
            )

            binding?.containerTransferInformation?.containerMorePaymentInformation?.root?.visible(
                isShowAdditionalInformation
            )

            if (isShowAdditionalInformation)
                setupAdditionalInformation(decode)
        }

    }

    private fun setupAdditionalInformation(decode: QRCodeDecodeResponse) {
        var isIdentifier = true
        var isMessage = true
        decode.idTx?.let {
            binding?.containerTransferInformation?.containerMorePaymentInformation?.containerIdentifier?.visible()
            binding?.containerTransferInformation?.containerMorePaymentInformation?.tvIdentifier?.text =
                it
        } ?: run {
            isIdentifier = false
            binding?.containerTransferInformation?.containerMorePaymentInformation?.containerIdentifier?.gone()
        }

        decode.additionalData?.let {
            binding?.containerTransferInformation?.containerMorePaymentInformation?.containerMessage?.visible()
            binding?.containerTransferInformation?.containerMorePaymentInformation?.tvCollectorMessage?.text =
                it
        } ?: run {
            isMessage = false
            binding?.containerTransferInformation?.containerMorePaymentInformation?.containerMessage?.gone()
        }

        if (isIdentifier && isMessage)
            binding?.containerTransferInformation?.containerMorePaymentInformation?.root?.visible()
        else
            binding?.containerTransferInformation?.containerMorePaymentInformation?.root?.gone()
    }

    private fun setupDebtorInformation() {
        decodeResponse?.let { decode ->
            binding?.containerTransferInformation?.containerDebtorInformation?.root?.visible()
            binding?.containerTransferInformation?.containerDebtorInformation?.tvDebtorName?.text =
                decode.payerName

            val document = if (decode.payerType == PixOwnerTypeEnum.NATURAL_PERSON.name)
                PixOwnerTypeEnum.NATURAL_PERSON.owner
            else PixOwnerTypeEnum.LEGAL_PERSON.owner

            binding?.containerTransferInformation?.containerDebtorInformation?.tvDebtorDocumentTitle?.text =
                getString(R.string.text_pix_summary_qrcode_debtor_document, document)
            binding?.containerTransferInformation?.containerDebtorInformation?.tvDebtorDocument?.text =
                decode.payerDocument
        }
    }

    private fun setupDueDate() {
        decodeResponse?.let { decode ->
            binding?.containerTransferInformation?.containerPaymentInformation?.containerBillingDate?.visible()
            binding?.containerTransferInformation?.containerPaymentInformation?.tvDueDate?.text =
                decode.dueDate?.dateFormatToBrSubString()
            binding?.containerTransferInformation?.containerPaymentInformation?.tvDeadlineDueDate?.text =
                decode.expireDate?.dateFormatToBrSubString()
        }
    }

    private fun setupBillingDetails() {
        showBillingDetails()
        binding?.containerTransferInformation?.containerBillingDetails?.root.visible()
    }

    private fun showBillingDetails() {
        decodeResponse?.let { decode ->
            binding?.containerTransferInformation?.containerBillingDetails?.tvOriginalValue?.text =
                decode.originalAmount?.toPtBrRealString() ?: DEFAULT_AMOUNT
            binding?.containerTransferInformation?.containerBillingDetails?.tvAbatementValue?.text =
                decode.abatement?.toPtBrRealString() ?: DEFAULT_AMOUNT
            binding?.containerTransferInformation?.containerBillingDetails?.tvDiscountValue?.text =
                decode.discount?.toPtBrRealString() ?: DEFAULT_AMOUNT
            binding?.containerTransferInformation?.containerBillingDetails?.tvPenaltyValue?.text =
                decode.penalty?.toPtBrRealString() ?: DEFAULT_AMOUNT
            binding?.containerTransferInformation?.containerBillingDetails?.tvInterestValue?.text =
                decode.interest?.toPtBrRealString() ?: DEFAULT_AMOUNT
        }
    }

    private fun setTitles(
        @StringRes titleId: Int = R.string.text_pix_summary_pay_title,
        @StringRes titleValueId: Int = R.string.screen_text_read_qr_code_summary_pay_value,
        @StringRes typeId: Int = R.string.screen_text_read_qr_code_summary_pay_type,
        @StringRes dateId: Int = R.string.text_pix_summary_pay_date
    ) {
        binding?.tvTitleDecodeSummary?.text =
            getString(titleId)

        binding?.containerTransferInformation?.tvValueTitle?.text =
            getString(titleValueId)

        binding?.containerTransferInformation?.containerPaymentInformation?.tvType?.text =
            getString(typeId)
        binding?.containerTransferInformation?.containerPaymentInformation?.tvDateTitle?.text =
            getString(dateId)
    }

    private fun showImproperBilling() {
        binding?.containerImproperBilling?.root?.visible()
    }

    private fun setupCancelTransaction() {
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
                        toHomePix()
                    }
                    it.show(
                        childFragmentManager,
                        PixDecodeQRCodeSummaryFragment::class.java.simpleName
                    )
                }
        }
    }

    private fun getFingerPrint(isAnimation: Boolean = true) {
        this.isAnimation = isAnimation

        val mAllowMeContextual = allowMePresenter.init(requireContext())
        allowMePresenter.collect(
            mAllowMeContextual = mAllowMeContextual,
            requireActivity(),
            mandatory = true,
            hasAnimation = true
        )
    }

    private fun pay(isAnimation: Boolean, fingerPrint: String) {
        val date =
            binding?.containerTransferInformation?.containerPaymentInformation?.tvDate?.text?.toString()
        val dateFormatted = if (date ==
            getString(R.string.text_pix_summary_transfer_date_value)
        ) null else date?.dateInternationalFormat()

        val amount = if (getAmount() == DOUBLE) null else getAmount()

        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            presenter.onPayQRCode(
                otp = otpCode,
                message = getMessage(),
                date = dateFormatted,
                amount = amount,
                decodeResponse = decodeResponse,
                fingerprint = fingerPrint
            )
        }
    }

    private fun payback(
        @StringRes title: Int,
        message: String,
        @DrawableRes image: Int,
    ) {
        bottomSheetGenericFlui(
            EMPTY,
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
            isFullScreen = true,
            isPhone = false
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        toHomePix()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        toHomePix()
                    }

                    override fun onCancel() {
                        dismiss()
                        toHomePix()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun bottomSheetSuccess(
        transferResponse: PixTransferResponse,
        isScheduling: Boolean = false
    ) {
        val message = getSuccessMessage(isScheduling)

        bottomSheetGenericFlui(
            nameTopBar = "",
            R.drawable.ic_validado_transfer,
            getString(R.string.text_success_pay_qrcode_title),
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
                        toReceipt(transferResponse, isScheduling)
                    }

                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                        toHomePix()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                        toHomePix()
                    }

                    override fun onCancel() {
                        dismiss()
                        toHomePix()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    private fun toReceipt(transferResponse: PixTransferResponse, isScheduling: Boolean) {
        findNavController().navigate(
            when {
                isScheduling -> PixDecodeQRCodeSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixTransferScheduledReceiptFragment(
                    null,
                    transferResponse.schedulingCode,
                    PIX_QR_CODE
                )
                decodeResponse?.type != PixQRCodeTypeEnum.DYNAMIC_COBV.name -> PixDecodeQRCodeSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixTransferReceiptFragment(
                    PIX_QR_CODE,
                    transferResponse.transactionCode ?: EMPTY,
                    transferResponse.idEndToEnd ?: EMPTY
                )
                else -> {
                    PixDecodeQRCodeSummaryFragmentDirections.actionPixTransferSummaryFragmentToPixBillingReceiptFragment(
                        true,
                        transferResponse.transactionCode ?: EMPTY,
                        transferResponse.idEndToEnd ?: EMPTY
                    )
                }
            }
        )
    }

    private fun getSuccessMessage(isScheduling: Boolean): String {
        return if (isScheduling)
            getString(
                R.string.text_pix_summary_scheduled_transfer_success_message,
                getAmount().toPtBrRealString(),
                decodeResponse?.receiverName ?: EMPTY
            )
        else
            when (decodeResponse?.pixType) {
                PixQRCodeOperationTypeEnum.CHANGE.name -> {
                    getString(
                        R.string.text_success_pay_qrcode_change,
                        getPurchaseAmount().toPtBrRealString(),
                        getAmount().toPtBrRealString(),
                        decodeResponse?.receiverName ?: EMPTY
                    )
                }
                PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> {
                    getString(
                        R.string.text_success_pay_qrcode_withdrawal,
                        getAmount().toPtBrRealString(),
                        decodeResponse?.receiverName ?: EMPTY
                    )
                }
                else -> {
                    getString(
                        R.string.text_success_pay_qrcode,
                        getAmount().toPtBrRealString(),
                        decodeResponse?.receiverName ?: EMPTY
                    )
                }
            }
    }

    private fun stopAnimationAllowMe(onAction: () -> Unit = {}) {
        if (isAnimation)
            onAction.invoke()
        else
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

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showContent(true)
    }

    override fun onButtonClicked(labelButton: String) {
        getFingerPrint()
    }

    override fun onClickSecondButtonError() {
        if (isDecode)
            presenter.onDecode()
        else
            getFingerPrint()
    }

    override fun onSuccessDecode(decode: QRCodeDecodeResponse, scheduling: String?) {
        decodeResponse = decode
        binding?.containerTransferInformation?.containerPaymentInformation?.tvDate?.text =
            scheduling.dateFormatToBrSubString()
        setupChargeAmount()
        setupFees()
        setDate()
        payQRCodeConv()
    }

    override fun onErrorDecode(errorMessage: ErrorMessage?) {
        isDecode = true
        if (errorMessage?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || errorMessage.errorCode.contains(
                OTP
            )
        )
            navigation?.showErrorBottomSheet(
                textButton = getString(R.string.text_try_again_label),
                error = processErrorMessage(
                    errorMessage,
                    getString(R.string.business_error),
                    getString(R.string.text_pix_error_in_processing)
                ),
                title = getString(R.string.text_pix_summary_qrcode_cob_error_title),
                isFullScreen = false
            )
    }

    override fun onSuccessPayQRCode(onAction: () -> Unit) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onAction.invoke()
                }
            })
    }

    override fun onSuccessScheduling(transferResponse: PixTransferResponse) {
        bottomSheetSuccess(transferResponse, isScheduling = true)
    }

    override fun onSuccessfulPayment(
        transferResponse: PixTransferResponse
    ) {
        bottomSheetSuccess(transferResponse)
    }

    override fun onTransactionInProcess() {
        val finalMessage = getString(
            R.string.text_in_process_pay_qrcode,
            getAmount().toPtBrRealString(),
            decodeResponse?.receiverName ?: EMPTY
        )

        payback(
            R.string.text_pix_error_transaction_in_process_title,
            finalMessage,
            R.drawable.ic_29
        )
    }

    override fun onErrorPayQRCode(onGenericError: () -> Unit) {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                onGenericError.invoke()
            }
        })
    }

    override fun onPaymentError() {
        val finalMessage = getString(
            R.string.text_error_pay_qrcode,
            getAmount().toPtBrRealString(),
            decodeResponse?.receiverName ?: EMPTY,
            getString(R.string.text_pix_error_transaction_message)
        )
        payback(
            R.string.text_pix_error_transaction_title,
            finalMessage,
            R.drawable.ic_transaction_error
        )
    }

    override fun showError(error: ErrorMessage?) {
        isDecode = false

        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        ) {
            val isAnInternalError = error?.code == HttpURLConnection.HTTP_INTERNAL_ERROR.toString()

            if (isAnInternalError) showInternalError()
            else showCommonError(error)
        }
    }

    private fun showInternalError() {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_close),
            title = getString(R.string.text_pix_transfer_bs_error_title),
            subtitle = getString(R.string.text_pix_transfer_bs_error_subtitle),
            callToActionButton = { toHomePix() },
            callToActionSwiped = { toHomePix() },
            isFullScreen = false
        )
    }

    private fun showCommonError(errorMessage: ErrorMessage?) {
        navigation?.showErrorBottomSheet(
            textButton = getString(R.string.text_try_again_label),
            error = processErrorMessage(
                errorMessage,
                getString(R.string.business_error),
                getString(R.string.text_pix_error_in_processing)
            ),
            title = getString(R.string.text_pix_summary_transfer_error_title),
            isFullScreen = false
        )
    }

    override fun successCollectToken(result: String) {
        pay(isAnimation, result)
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        stopAnimationAllowMe(onAction = { showAlert(errorMessage) })
    }

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }

    override fun stopAction() {
        stopAnimationAllowMe(onAction = {
            dialogLocationActivation(
                requireActivity(),
                childFragmentManager
            )
        })
    }

    private fun toHomePix(){
        requireActivity().toHomePix()
    }
}