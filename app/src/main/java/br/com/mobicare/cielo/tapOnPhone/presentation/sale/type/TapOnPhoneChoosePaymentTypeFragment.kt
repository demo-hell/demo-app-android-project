package br.com.mobicare.cielo.tapOnPhone.presentation.sale.type

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.constants.ONE_TEXT
import br.com.mobicare.cielo.commons.constants.TWELVE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.toCents
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneChoosePaymentTypeBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.constants.INSTALLMENTS
import br.com.mobicare.cielo.tapOnPhone.constants.INSTALLMENTS_TEXT
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneSaleInfoModel
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneTerminalResponse
import br.com.mobicare.cielo.tapOnPhone.domain.model.TransactionReceiptData
import br.com.mobicare.cielo.tapOnPhone.enums.TapOnPhonePaymentTypeEnum
import br.com.mobicare.cielo.tapOnPhone.presentation.sale.type.installment.TapOnPhoneChooseInstallmentsBottomSheet
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminal
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminalContract
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants
import br.com.mobicare.cielo.tapOnPhone.utils.tapPaymentType
import com.symbiotic.taponphone.Enums.AidType
import com.symbiotic.taponphone.Enums.TransactionType
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

class TapOnPhoneChoosePaymentTypeFragment :
    BaseFragment(),
    TapOnPhoneSetupTerminalContract.Result,
    CieloNavigationListener {
    private val tapOnPhoneSetupTerminal: TapOnPhoneSetupTerminal by inject {
        parametersOf(this@TapOnPhoneChoosePaymentTypeFragment)
    }

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private var aidType: AidType? = null
    private var navigation: CieloNavigation? = null
    private var installment: String? = null
    private var chosenTransactionTypePayment: TransactionType? = null

    private var binding: FragmentTapOnPhoneChoosePaymentTypeBinding? = null
    private val args: TapOnPhoneChoosePaymentTypeFragmentArgs by navArgs()

    private val saleValue: BigDecimal by lazy {
        args.topsalevalueargs
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentTapOnPhoneChoosePaymentTypeBinding
        .inflate(
            inflater,
            container,
            false,
        ).also {
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupView()
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(
            name = TapOnPhoneAnalytics.TRANSACTIONAL_MAKE_SALE_PATH,
            className = javaClass,
        )
        ga4.logScreenView(TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_MAKE_SALE)
        tapOnPhoneSetupTerminal.onResume()
    }

    override fun onPause() {
        super.onPause()
        tapOnPhoneSetupTerminal.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.tap_on_phone_sale_button))
            navigation?.showToolbar()
            navigation?.showBackIcon()
            navigation?.showCloseButton()
            navigation?.showButton(isShow = true)
            navigation?.showHelpButton(isShow = true)
            navigation?.enableButton(isEnabled = false)
            navigation?.showContainerButton(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.tvRadioButtonInclude?.radioGroupAccountType?.setOnCheckedChangeListener { group, _ ->
            setupRadioGroupView(group)
        }

        binding?.tvTotalValue?.setViewOnClickListener {
            findNavController().popBackStack()
        }

        binding?.tvRadioButtonInclude?.includeInstallmentsInfo?.root?.setOnClickListener {
            showInstallmentOptions(installment)
        }
    }

    private fun setupRadioGroupView(group: RadioGroup) {
        binding?.tvRadioButtonInclude?.apply {
            when (group.checkedRadioButtonId) {
                rbDebit.id -> setupRadioButtonDebit()
                rbCreditAtOnce.id -> setupRadioButtonCredit()
                rbInstallment.id -> setupRadioButtonInstallment(rbInstallment.isChecked)
                else -> setupRadioButton()
            }
        }

        shouldEnableButton()
    }

    private fun setupRadioButton() {
        clearPaymentsOptions()
        binding
            ?.tvRadioButtonInclude
            ?.includeInstallmentsInfo
            ?.root
            .gone()

        storeSelectedOptions(
            rbDebitSelected = false,
            rbCreditAtOnceSelected = false,
            rbInstallmentSelected = false,
        )
    }

    private fun setupRadioButtonDebit() {
        binding
            ?.tvRadioButtonInclude
            ?.includeInstallmentsInfo
            ?.root
            .gone()

        storeSelectedOptions(
            aidTypeChosen = AidType.Debit,
            transactionTypeChosen = TransactionType.Payment,
            installmentQuantityChosen = null,
            rbDebitSelected = true,
            rbCreditAtOnceSelected = false,
            rbInstallmentSelected = false,
        )
    }

    private fun setupRadioButtonCredit() {
        binding
            ?.tvRadioButtonInclude
            ?.includeInstallmentsInfo
            ?.root
            .gone()

        storeSelectedOptions(
            aidTypeChosen = AidType.Credit,
            transactionTypeChosen = TransactionType.SaleWithFinancing,
            installmentQuantityChosen = null,
            rbDebitSelected = false,
            rbCreditAtOnceSelected = true,
            rbInstallmentSelected = false,
        )
    }

    private fun setupRadioButtonInstallment(isChecked: Boolean) {
        if (isChecked) {
            clearPaymentsOptions()

            storeSelectedOptions(
                rbDebitSelected = false,
                rbCreditAtOnceSelected = false,
                rbInstallmentSelected = true,
            )

            showInstallmentOptions()
        }
    }

    private fun clearPaymentsOptions() {
        aidType = null
        chosenTransactionTypePayment = null
        installment = null
    }

    private fun setupView() {
        binding?.apply {
            tvTotalValue.text = saleValue.toPtBrRealString()
            tvTotalValue.isEnabled = false
            cardInformation.visible(hasCardReader())
        }
    }

    private fun showInstallmentOptions(currentSelectedInstallment: String? = null) {
        TapOnPhoneChooseInstallmentsBottomSheet
            .onCreate(
                arrayListOf(TWO..TWELVE).flatten() as ArrayList<Int>,
                currentSelectedInstallment,
            ) { selectedInstallment ->
                selectedInstallment?.let { setupInstallment(it) } ?: noInstallment()
                shouldEnableButton()
            }.show(childFragmentManager, TapOnPhoneChooseInstallmentsBottomSheet::class.java.simpleName)
    }

    private fun setupInstallment(selectedInstallment: String) {
        binding?.tvRadioButtonInclude?.includeInstallmentsInfo?.apply {
            root.visible()
            tvPaymentInfo.text =
                getString(
                    R.string.tap_on_phone_sale_total_installments_value,
                    selectedInstallment,
                )

            storeSelectedOptions(
                aidTypeChosen = AidType.Credit,
                transactionTypeChosen = TransactionType.SaleWithFinancing,
                installmentQuantityChosen = selectedInstallment,
            )
        }
    }

    private fun noInstallment() {
        clearPaymentsOptions()
        binding?.tvRadioButtonInclude?.apply {
            radioGroupAccountType.clearCheck()
            storeSelectedOptions(
                rbDebitSelected = false,
                rbCreditAtOnceSelected = false,
                rbInstallmentSelected = false,
            )
        }
    }

    private fun storeSelectedOptions(
        aidTypeChosen: AidType? = null,
        transactionTypeChosen: TransactionType? = null,
        installmentQuantityChosen: String? = null,
        rbDebitSelected: Boolean = false,
        rbCreditAtOnceSelected: Boolean = false,
        rbInstallmentSelected: Boolean = false,
    ) {
        aidType = aidTypeChosen
        chosenTransactionTypePayment = transactionTypeChosen
        installment = installmentQuantityChosen

        binding?.tvRadioButtonInclude?.apply {
            setOptionsBackgroundColor(
                RadioButtonSelection(rbDebit, rbDebitSelected),
                RadioButtonSelection(rbCreditAtOnce, rbCreditAtOnceSelected),
                RadioButtonSelection(rbInstallment, rbInstallmentSelected),
            )
        }
    }

    private fun shouldEnableButton(
        isMakeTransaction: Boolean = false,
        enable: Boolean = true,
    ) {
        val isEnable =
            if (isMakeTransaction) {
                enable
            } else {
                chosenTransactionTypePayment != null && saleValue > BigDecimal.ZERO
            }
        navigation?.enableButton(isEnable)
    }

    private fun setOptionsBackgroundColor(vararg radioButtonSelection: RadioButtonSelection) {
        listOf(*radioButtonSelection).forEach { paymentType ->
            paymentType.radioButton.background =
                ContextCompat.getDrawable(
                    requireContext(),
                    if (paymentType.isSelected) {
                        R.drawable.background_border_blue
                    } else {
                        R.drawable.background_radius8dp_stroke_gray_204986
                    },
                )
        }
    }

    override fun onButtonClicked(labelButton: String) {
        logScreenAction(buttonName = TapOnPhoneAnalytics.SELL, amount = saleValue)
        ga4.logSaleValueAddPaymentInfo(
            value = saleValue,
            paymentType = paymentMethod.tagGa4,
            installment = installment?.toIntOrNull(),
        )
        shouldEnableButton(isMakeTransaction = true, enable = false)
        setupTerminal(args.devicetapargs)
    }

    private fun setupTerminal(device: TapOnPhoneTerminalResponse?) {
        tapOnPhoneSetupTerminal.onInitializeAllowMe({
            tapOnPhoneSetupTerminal.onInitializeTapOnPhone(deviceResponse = device)
        })
    }

    override fun onErrorInActivatingTerminal(
        errorCode: Short?,
        errorMessage: ErrorMessage?,
    ) {
        ga4.run {
            TapOnPhoneGA4.getTransactionSaleNotMadeScreenView(paymentMethod.tagGa4).let {
                logScreenView(it)
                logException(
                    screenName = it,
                    errorMessage = errorMessage?.errorMessage.orEmpty(),
                    errorCode = errorMessage?.errorCode.orEmpty(),
                )
            }
        }
        shouldEnableButton()
        navigation?.showCustomErrorHandler(
            title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
            error =
                processErrorMessage(
                    errorMessage,
                    getString(R.string.error_generic),
                    getString(R.string.tap_on_phone_initialize_terminal_generic_error_message),
                ),
            titleAlignment = TEXT_ALIGNMENT_TEXT_START,
            messageAlignment = TEXT_ALIGNMENT_TEXT_START,
            labelSecondButton = getString(R.string.entendi),
            isBack = true,
        )
    }

    override fun onShowInsertSaleValueScreen(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            onSuccessInActivatingTerminal(device)
        }
    }

    override fun onSuccessInActivatingTerminal(device: TapOnPhoneTerminalResponse?) {
        val installments =
            HashMap<String, String>().also {
                it[INSTALLMENTS] = installment ?: ONE_TEXT
                it[INSTALLMENTS_TEXT] = textExtension()
            }

        tapOnPhoneSetupTerminal.onMakeTransaction(
            TapOnPhoneSaleInfoModel(
                aidType = aidType,
                transactionValue = saleValue.toCents(),
                transactionType = chosenTransactionTypePayment,
                extendedTransactionData = installments,
                transactionApprovedAction = { transactionReceiptData ->
                    doWhenResumed {
                        showPaymentReceipt(transactionReceiptData)
                    }
                },
                transactionCancelled = {
                    doWhenResumed {
                        canceledSale()
                    }
                },
                transactionTimeExpired = {
                    doWhenResumed {
                        expiredSale()
                    }
                },
            ),
        )
    }

    private fun canceledSale() {
        analytics.logTransactionError(
            paymentMethod = paymentMethod.tag,
            errorMessage = TapOnPhoneAnalytics.CANCELED_SALE,
            className = javaClass,
        )
        ga4.run {
            logTransactionErrorScreenView(
                paymentMethod = paymentMethod.tagGa4,
                errorMessage = TapOnPhoneGA4.CANCELED_SALE,
            )
            logTransactionErrorException(
                paymentMethod = paymentMethod.tagGa4,
                errorMessage = TapOnPhoneGA4.CANCELED_SALE,
            )
        }
        genericError(
            message = getErrorMessage(getString(R.string.tap_on_phone_canceled_sale_message)),
        )
    }

    private fun expiredSale() {
        analytics.logTransactionError(
            paymentMethod = paymentMethod.tag,
            errorMessage = TapOnPhoneAnalytics.EXPIRED_TIME,
            className = javaClass,
        )
        ga4.run {
            logTransactionErrorScreenView(
                paymentMethod = paymentMethod.tagGa4,
                errorMessage = TapOnPhoneGA4.EXPIRED_TIME,
            )
            logTransactionErrorException(
                paymentMethod = paymentMethod.tagGa4,
                errorMessage = TapOnPhoneGA4.EXPIRED_TIME,
            )
        }
        genericError(
            message = getErrorMessage(getString(R.string.tap_on_phone_expired_sale_message)),
        )
    }

    override fun onTransactionFailedError(errorCode: Short) {
        doWhenResumed {
            ga4.run {
                logTransactionErrorScreenView(
                    paymentMethod = paymentMethod.tagGa4,
                    errorCode = errorCode.toString(),
                )
                logTransactionErrorException(
                    paymentMethod = paymentMethod.tagGa4,
                    errorCode = errorCode.toString(),
                )
            }
            genericError(
                message = getErrorMessage(getString(R.string.tap_on_phone_error_generic_sale)),
                errorCode = errorCode,
            )
        }
    }

    private fun genericError(
        message: String,
        errorCode: Short? = null,
    ) {
        shouldEnableButton()
        analytics.logTransactionError(
            paymentMethod = paymentMethod.tag,
            errorCode = errorCode.toString(),
            className = javaClass,
        )
        navigation?.showCustomHandler(
            contentImage = R.drawable.ic_07,
            title = getString(R.string.tap_on_phone_error_sale_title),
            titleAlignment = TEXT_ALIGNMENT_TEXT_START,
            messageAlignment = TEXT_ALIGNMENT_TEXT_START,
            message = message,
            isShowHeaderImage = true,
            labelSecondButton = getString(R.string.text_try_again_label),
        )
    }

    private fun getErrorMessage(message: String) =
        HtmlCompat
            .fromHtml(
                getString(
                    R.string.tap_on_phone_error_sale_message,
                    message,
                    typeSale(saleValue.toPtBrRealString()),
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY,
            ).toString()

    private fun typeSale(value: String) =
        when (paymentMethod) {
            TapOnPhonePaymentTypeEnum.DEBIT ->
                getString(
                    R.string.tap_on_phone_sale_value_debit,
                    value,
                )

            TapOnPhonePaymentTypeEnum.INSTALLMENT ->
                getString(
                    R.string.tap_on_phone_sale_value_installment,
                    installment ?: ONE_TEXT,
                    value,
                )

            else -> getString(R.string.tap_on_phone_sale_value_credit, value)
        }

    private fun textExtension() =
        when (paymentMethod) {
            TapOnPhonePaymentTypeEnum.DEBIT -> getString(R.string.tap_on_phone_sale_value_debit_extension)

            TapOnPhonePaymentTypeEnum.INSTALLMENT ->
                getString(
                    R.string.tap_on_phone_sale_value_installment_extension,
                    installment ?: ONE_TEXT,
                )

            else -> getString(R.string.tap_on_phone_sale_value_credit_extension)
        }

    private fun showPaymentReceipt(transactionReceiptData: TransactionReceiptData?) {
        transactionReceiptData?.let { itTransactionReceiptData ->
            requireActivity().apply {
                findNavController().navigate(
                    TapOnPhoneChoosePaymentTypeFragmentDirections
                        .actionTapOnPhoneChoosePaymentTypeFragmentToTapOnPhonePaymentReceiptFragment(
                            args.devicetapargs,
                            itTransactionReceiptData,
                        ),
                )
            }
        }
    }

    override fun onTapShowLoading(message: Int?) {
        doWhenResumed {
            navigation?.showAnimatedLoading(message)
        }
    }

    override fun onTapChangeLoadingText(message: Int?) {
        doWhenResumed {
            navigation?.changeAnimatedLoadingText(message)
        }
    }

    override fun onTapHideLoading() {
        doWhenResumed {
            navigation?.hideAnimatedLoading()
        }
    }

    override fun isSaleScreen() = false

    override fun isMakeTransaction() = true

    override fun getActivityTapOnPhone() = requireActivity()

    override fun getFragmentManagerTapOnPhone() = childFragmentManager

    override fun hasCardReader(): Boolean {
        val intent = navigation?.getData() as? Bundle
        return intent?.getBoolean(TapOnPhoneConstants.TAP_ON_PHONE_HAS_CARD_READER_ARGS) ?: false
    }

    override fun onEnableNFC(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            shouldEnableButton()
            TapOnPhoneChoosePaymentTypeFragmentDirections
                .actionTapOnPhoneChoosePaymentTypeFragmentToTapOnPhoneEnableNFCFragment(
                    device,
                )
        }
    }

    override fun onRetryConnectCardReader(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            shouldEnableButton()
            navigation?.retryConnectCardReader(onAction = {
                doWhenResumed {
                    setupTerminal(device)
                }
            })
        }
    }

    override fun onDevelopModeEnable(device: TapOnPhoneTerminalResponse?) {
        doWhenResumed {
            shouldEnableButton()
            findNavController().navigate(
                TapOnPhoneChoosePaymentTypeFragmentDirections
                    .actionTapOnPhoneChoosePaymentTypeFragmentToTapOnPhoneDeveloperModeIsEnableFragment(
                        device,
                    ),
            )
        }
    }

    override fun onExtensionError() {
        doWhenResumed {
            shouldEnableButton()
            analytics.logPaymentSaleNotMade(paymentMethod.tag, javaClass)
            ga4.logSaleNotMadeScreenView(paymentMethod.tagGa4)
            navigation?.showCustomHandler(
                contentImage = R.drawable.ic_19_maintenance,
                title = getString(R.string.tap_on_phone_initialize_terminal_generic_error_title),
                message = getString(R.string.id_onboarding_validate_p2_generic_error),
                titleAlignment = TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = {
                    navigation?.goToHome()
                },
                headerCallback = {
                    navigation?.goToHome()
                },
                finishCallback = {
                    navigation?.goToHome()
                },
            )
        }
    }

    override fun onMakeTransactionErrorWithCodeFortyTwo() {
        doWhenResumed {
            shouldEnableButton()
            navigation?.showCustomHandler(
                contentImage = R.drawable.img_128_preenchimento_ultimos_5,
                title = getString(R.string.tap_on_phone_title_bs_error_code_forty_two),
                message = getString(R.string.tap_on_phone_message_bs_error_code_forty_two),
                titleAlignment = TEXT_ALIGNMENT_TEXT_START,
                messageAlignment = TEXT_ALIGNMENT_TEXT_START,
                labelSecondButton = getString(R.string.entendi),
                secondButtonCallback = {
                    navigation?.goToHome()
                },
                headerCallback = {
                    navigation?.goToHome()
                },
                finishCallback = {
                    navigation?.goToHome()
                },
            )
        }
    }

    override fun onConnectCardReaderError() {
        doWhenResumed {
            shouldEnableButton()
            navigation?.retryConnectCardReader(btnLabel = R.string.entendi)
        }
    }

    override fun onHelpButtonClicked() {
        logScreenAction(Action.DUVIDA)
        ga4.logClick(
            screenName = TapOnPhoneGA4.SCREEN_VIEW_TRANSACTIONAL_MAKE_SALE,
            contentName = Action.DUVIDA,
        )
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_TAP_ON_PHONE,
            subCategoryName = getString(R.string.tap_on_phone),
        )
    }

    private fun logScreenAction(
        buttonName: String,
        amount: BigDecimal? = null,
    ) {
        val labelList = mutableListOf(buttonName)
        labelList.add(paymentMethod.tag)
        amount?.let { labelList.add(it.toPtBrRealStringWithoutSymbol()) }

        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.MAKE_SALE,
            labelList = labelList,
        )
    }

    private val paymentMethod
        get() = tapPaymentType(aidType?.name, installment)

    private inner class RadioButtonSelection(
        val radioButton: AppCompatRadioButton,
        val isSelected: Boolean,
    )
}
