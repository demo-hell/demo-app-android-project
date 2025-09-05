package br.com.mobicare.cielo.pix.ui.extract.reversal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.cielo.libflue.alert.CieloAlertDialogFragment
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.commons.constants.Text.OTP
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
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentPixReversalBinding
import br.com.mobicare.cielo.databinding.LayoutPixTransferSummaryBinding
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pix.constants.*
import br.com.mobicare.cielo.pix.domain.ReversalDetailsResponse
import br.com.mobicare.cielo.pix.domain.ReversalReceiptsResponse
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageBottomSheet
import br.com.mobicare.cielo.pix.ui.transfer.message.PixEnterTransferMessageContract
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class PixReversalFragment : BaseFragment(), CieloNavigationListener,
    PixReversalContract.View, AllowMeContract.View {

    private val presenter: PixReversalPresenter by inject {
        parametersOf(this)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val transferDetails: TransferDetailsResponse? by lazy {
        arguments?.getParcelable(PIX_TRANSFER_DETAILS_RESPONSE_ARGS)
    }
    private val reversalReceipts: ReversalReceiptsResponse? by lazy {
        arguments?.getParcelable(PIX_REVERSAL_RECEIPTS_RESPONSE_ARGS)
    }
    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }

    private val allowMePresenter: AllowMePresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    private var isTryAgain = false
    private lateinit var mAllowMeContextual: AllowMeContextual

    private var _binding: FragmentPixReversalBinding? = null
    private val binding get() = _binding

    private var _transferInfoBinding: LayoutPixTransferSummaryBinding? = null
    private val transferInfoBinding get() = _transferInfoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixReversalBinding.inflate(inflater, container, false)
        _transferInfoBinding = binding?.containerTransferInformation
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAllowMeContextual = allowMePresenter.init(requireContext())

        setupNavigation()
        setupView()
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

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setTextToolbar(getString(R.string.toolbar_screen_transfer_pix_reversal))
                setTextButton(getString(R.string.text_pix_transaction_transfer_reversal))
                setTextFirstButton(getString(R.string.text_pix_summary_transfer_cancel))
                showContainerButton(isShow = true)
                showButton(isShow = true)
                enableButton(isEnabled = false)
                showFirstButton(false)
                showHelpButton()
                setNavigationListener(this@PixReversalFragment)
            }
        }
    }

    private fun defineTransferFlow() {
        allowMePresenter.collect(
            mAllowMeContextual = mAllowMeContextual,
            requireActivity(),
            mandatory = true
        )
    }

    private fun setupView() {
        setAmount()
        validateAmount()
        setupFees()
        setMessage()
        showTransferInfo()
        changeAmount()
        setReversalViews()
    }

    private fun setReversalViews() {
        binding?.tvTitleReversal?.text =
            getString(R.string.text_pix_summary_transfer_title_reversal)
        transferInfoBinding?.apply {
            tvValueTitle.text = getString(R.string.text_pix_summary_transfer_value_title_reversal)
            tvUserDataTitle.text =
                getString(R.string.text_pix_summary_transfer_destination_reversal)
            containerPaymentInformation.tvMessageTitle.text =
                getString(R.string.text_pix_reversal_insert_reason)
            containerPaymentInformation.tvMessage.text =
                getString(R.string.text_pix_reversal_insert_reason_hint)
            containerPaymentInformation.tvType.text =
                getString(R.string.text_pix_reversal_type_value)
            containerTaxInformation.tvRatePix.gone()
        }
    }

    private fun setAmount() {
        val balance = balance ?: DEFAULT_BALANCE
        PixEnterTransferAmountBottomSheet.onCreate(
            object : PixEnterTransferAmountContract.Result {
                override fun onAmount(amount: Double) {
                    transferInfoBinding?.tvValue?.text = amount.toPtBrRealString()
                    validateAmount(amount)
                    setupFees(amount)
                }
            },
            balance,
            getAmount(),
            reversalAvailable = reversalReceipts?.totalAmountPossibleReversal
        ).show(childFragmentManager, tag)
    }

    private fun setMessage() {
        transferInfoBinding?.containerPaymentInformation?.tvMessage?.apply {
            setOnClickListener {
                PixEnterTransferMessageBottomSheet.onCreate(
                    object : PixEnterTransferMessageContract {
                        override fun onMessage(message: String) {
                            text =
                                message.ifEmpty {
                                    getString(R.string.text_pix_reversal_insert_reason_hint)
                                }
                        }
                    },
                    text?.toString() ?: EMPTY,
                    true
                ).show(childFragmentManager, this@PixReversalFragment.tag)
            }
        }
    }

    private fun getMessage(): String? {
        val message = transferInfoBinding?.containerPaymentInformation?.tvMessage?.text
        val isHintText =
            message?.toString() == getString(R.string.text_pix_reversal_insert_reason_hint)
        return if (isHintText) {
            null
        } else message?.toString()
    }

    private fun getAmount(): Double =
        transferInfoBinding?.tvValue?.text?.toString()?.moneyToDoubleValue() ?: ZERO_DOUBLE

    private fun changeAmount() {
        transferInfoBinding?.tvValue?.setOnClickListener {
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

    private fun cancelReversal() {
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
                it.show(childFragmentManager, PixReversalFragment::class.java.simpleName)
            }
    }

    private fun showTransferInfo() {
        val destinationParty = transferDetails?.debitParty
        transferInfoBinding?.containerDestinationInformation?.apply {
            tvDestinationName.text = destinationParty?.name
            tvDestinationBank.text = destinationParty?.bankName

            tvDestinationKeyTitle.text = getString(R.string.text_your_document)
            tvDestinationKey.text = destinationParty?.nationalRegistration
        }
        transferInfoBinding?.containerPaymentInformation?.apply {
            tvToBeDebitedTitle.gone()
            tvToBeDebitedSubtitle.gone()
        }
    }

    private fun toHome() {
        requireActivity().toHomePix()
        requireActivity().finish()
    }

    private fun toReceipt(details: ReversalDetailsResponse) {
        findNavController().navigate(
            PixReversalFragmentDirections.actionPixReversalFragmentToPixReversalReceiptFragment(
                details.transactionCode.toString()
            )
        )
    }

    private fun bottomSheetSuccess(
        details: ReversalDetailsResponse
    ) {
        val amount = getAmount().toPtBrRealString()
        val message = getString(
            R.string.text_pix_summary_transfer_success_message,
            amount,
            transferDetails?.creditParty?.name ?: EMPTY
        )

        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_validado_transfer,
            getString(R.string.text_pix_summary_transfer_success_title),
            message,
            nameBtn1Bottom = getString(R.string.text_close),
            nameBtn2Bottom = getString(R.string.text_pix_summary_transfer_show_receipt),
            statusNameTopBar = false,
            statusBtnClose = false,
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
                        toReceipt(details)
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
            statusBtnClose = false,
            statusBtnFirst = false,
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

    override fun onButtonClicked(labelButton: String) {
        defineTransferFlow()
    }

    override fun onFirstButtonClicked(labelButton: String) {
        cancelReversal()
    }

    override fun onShowSuccessReversal(details: ReversalDetailsResponse) {
        isTryAgain = false
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    bottomSheetSuccess(details)
                }
            })
    }

    override fun onTransactionInProcess() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    val message = getString(
                        R.string.text_pix_error_transaction_in_process_message,
                        getAmount().toPtBrRealString(),
                        transferDetails?.creditParty?.name ?: EMPTY
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

    override fun showError(error: ErrorMessage?) {
        isTryAgain = true
        validationTokenWrapper.playAnimationError(error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    bottomSheetErrorTokenValidation(error)
                }
            })
    }

    private fun bottomSheetErrorTokenValidation(error: ErrorMessage?) {
        if(error?.code  != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(OTP)) {
            val isAnInternalError = error?.code == HttpURLConnection.HTTP_INTERNAL_ERROR.toString()

            if(isAnInternalError) showInternalError()
            else showCommonError(error)
        }
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
            callToActionButton = { requireActivity().toHomePix() },
            callToActionSwiped = { requireActivity().toHomePix() },
            isFullScreen = false
        )
    }

    override fun onError() {
        validationTokenWrapper.playAnimationError(null,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    val error = getString(R.string.text_pix_error_transaction_message)
                    transactionReturn(
                        R.string.text_pix_error_transaction_title,
                        error,
                        R.drawable.ic_transaction_error
                    )
                }
            })
    }

    override fun successCollectToken(result: String) {
        validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                presenter.onReverse(
                    otpCode,
                    transferDetails?.idEndToEnd,
                    getAmount(),
                    getMessage(),
                    result,
                    transferDetails?.idTx
                )
            }
    }

    override fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean) {
        showAlert(errorMessage)
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

    override fun getSupportFragmentManagerInstance(): FragmentManager {
        return childFragmentManager
    }
}