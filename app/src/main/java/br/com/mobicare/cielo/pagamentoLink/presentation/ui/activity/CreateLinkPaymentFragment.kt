package br.com.mobicare.cielo.pagamentoLink.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CLICK
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.constants.HelpCenter.HELP_CENTER
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentCreateLinkPaymentBinding
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.LinkRequest
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSaleEnum
import br.com.mobicare.cielo.pagamentoLink.domains.TypeSalePeriodicEnum
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.pagamentoLink.presentation.PREVENT_FRAUD_LINK
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.CreateLinkPaymentPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.LinkContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog.PgLinkOptionsAdvancedBottomSheet
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.CODE
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SKU
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.VALUE
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.CHARGE_VALUE
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.CREATE_NEW_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIBERADO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.NOME_DO_PRODUTO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.SEND_PRODUCT
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.VALOR_DA_VENDA
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal

class CreateLinkPaymentFragment : BaseFragment(), LinkContract.CreateLinkView,
    CieloNavigationListener {

    private var binding: FragmentCreateLinkPaymentBinding? = null

    private val createLinkPaymentPresenter: CreateLinkPaymentPresenter by inject {
        parametersOf(this)
    }

    private val compositeDisposable = CompositeDisposable()
    private var navigation: CieloNavigation? = null

    private var linkValue: BigDecimal = BigDecimal.ZERO
    private var linkTitle: String = EMPTY_STRING
    private var isRecurrentSale = false
    private val predefinedValues = listOf(5L, 10L, 50L, 100L)
    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var gaPurposesTypeSale = EMPTY_STRING
    private var chargeDate = DataCustomNew()
    private var quickFilter: QuickFilter = QuickFilter.Builder().build()

    private val ga4: PaymentLinkGA4 by inject()
    private val screenPathSetAmount
        get() = paymentLinkDTO?.typeSale?.screenPath?.let { it + VALUE } ?: EMPTY_STRING
    private val screenPathSetCode
        get() = paymentLinkDTO?.typeSale?.let {
            if (it == TypeSaleEnum.RECURRENT_SALE) {
                it.screenPath + SKU
            } else {
                it.screenPath + CODE
            }
        } ?: EMPTY_STRING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCreateLinkPaymentBinding.inflate(
        inflater,
        container,
        false
    ).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        loadArguments()
        setupReactiveListeners()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (compositeDisposable.isDisposed.not()) compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setTextToolbar(getString(R.string.text_create_link_title))
                it.showButton(false)
                it.setNavigationListener(this)
                it.showContent(true)
            }
        }
    }

    private fun loadArguments() {
        navigation?.getSavedData()?.getParcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
            paymentLinkDTO = it
            createLinkPaymentPresenter.setPaymentLinkDTO(it)
            populateFields(it)
        }
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendWhatButton(gaPurposesTypeSale, VOLTAR)
        return super.onBackButtonClicked()
    }

    override fun setLabelButton(resId: Int) {
        binding?.buttonGenerateLink?.setText(getString(resId))
    }

    override fun goToShippingMethod(dto: PaymentLinkDTO) {
        navigation?.saveData(Bundle().apply {
            putParcelable(ARG_PARAM_PAYMENT_LINK_DTO, dto)
            putSerializable(ARG_PARAM_QUICKER_FILTER, quickFilter)
        })
        findNavController().navigate(
            CreateLinkPaymentFragmentDirections
                .actionCreateLinkPaymentFragmentToFormaEnvioFragment()
        )
    }

    private fun setupReactiveListeners() {
        binding?.apply {
            textInputEditCreateLinkValue.addTextChangedListener(
                ValorMonetarioWatcher.Builder().build()
            )
            textInputEditCreateLinkTitle.setOnFocusChangeListener(::onTitleFieldFocusChange)
            textInputEditCreateLinkValue.setOnFocusChangeListener(::onValueFieldFocusChange)
            textInputEditCreateLinkTitle.setOnTextChangeListener(getOnTitleTextChangeListener())

            compositeDisposable.add(
                textInputEditCreateLinkValue.afterTextChangesNotEmptySubscribe(
                    TIMEOUT_0_MILLISECONDS
                ) {
                    buttonGenerateLink.isEnabled = validateLinkInputFields()
                    gaSendEnableButton(
                        buttonGenerateLink.isEnabled,
                        buttonGenerateLink.getText()
                    )
                    gaSendFormInterection(gaPurposesTypeSale, it.toString().trim())
                    linkValue = BigDecimal.valueOf(it.toString().trim().currencyToDouble())
                }
            )

            compositeDisposable.add(
                textInputEditCreateLinkValue.afterTextChangesEmptySubscribe {
                    buttonGenerateLink.isEnabled = false
                }
            )
        }
    }

    private fun onTitleFieldFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) return

        binding?.textInputEditCreateLinkTitle?.getText()?.trim()?.let { titleText ->
            if (titleText.isEmpty().not() && titleText.length > FOUR) {
                gaSendFormInterection(
                    NOME_DO_PRODUTO,
                    paymentLinkDTO?.typeSale?.name ?: EMPTY_STRING
                )
            }
        }
    }

    private fun onValueFieldFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) return

        val valueText =
            binding?.textInputEditCreateLinkValue?.text?.toString()?.trim() ?: EMPTY_STRING
        val titleText = binding?.textInputEditCreateLinkTitle?.getText()?.trim() ?: EMPTY_STRING

        if (valueText.isNotEmpty() && titleText.isNotEmpty() && valueText != ZERO_EMPTY_VALUE) {
            gaSendFormInterection(
                VALOR_DA_VENDA,
                paymentLinkDTO?.typeSale?.name ?: EMPTY_STRING
            )
        }
    }

    private fun getOnTitleTextChangeListener() = object : CieloTextInputView.TextChangeListener {
        override fun afterTextChanged(s: Editable?) {
            binding?.apply {
                buttonGenerateLink.isEnabled = s.isNullOrBlank().not() && validateLinkInputFields()
                gaSendEnableButton(buttonGenerateLink.isEnabled, buttonGenerateLink.getText())
                gaSendFormInterection(gaPurposesTypeSale, s.toString().trim())
                linkTitle = s?.toString()?.trim() ?: EMPTY_STRING
            }
        }
    }

    private fun validateLinkInputFields(): Boolean {
        val titleText = binding?.textInputEditCreateLinkTitle?.getText()?.trim() ?: EMPTY_STRING
        val valueText =
            binding?.textInputEditCreateLinkValue?.text?.toString()?.trim() ?: EMPTY_STRING
        val valueDecimal = BigDecimal.valueOf(valueText.currencyToDouble())

        if (titleText.isBlank()) return false
        if (titleText.length <= MIN_TITLE_LENGTH_ALLOWED) return false
        if (valueDecimal < MIN_VALUE) return false
        if (valueDecimal > MAX_VALUE) return false

        if (isRecurrentSale) {
            val frequencyText =
                binding?.layoutPeriodicyCharge?.textViewFrequency?.text?.toString() ?: EMPTY_STRING
            val chargeDateText =
                binding?.layoutPeriodicyCharge?.textViewChargeDate?.text?.toString() ?: EMPTY_STRING
            val isFrequencyNotSelected = frequencyText == getString(R.string.select_frequency)
            val isChargeDateNotSelected = chargeDateText == getString(R.string.select_date)

            if (isFrequencyNotSelected) return false
            if (isChargeDateNotSelected) return false
        }

        return true
    }


    private fun setupClickListeners() {
        binding?.apply {
            textPlusFiveShortcut.setOnClickListener {
                configurePredefinedValue(textPlusFiveShortcut, predefinedValues[ZERO])
            }

            textPlusTenShortcut.setOnClickListener {
                configurePredefinedValue(textPlusTenShortcut, predefinedValues[ONE])
            }

            textPlusFiftyShortcut.setOnClickListener {
                configurePredefinedValue(textPlusFiftyShortcut, predefinedValues[TWO])
            }

            textPlusOneHundredShortcut.setOnClickListener {
                configurePredefinedValue(textPlusOneHundredShortcut, predefinedValues[THREE])
            }

            buttonGenerateLink.setOnClickListener {
                requireActivity().hideSoftKeyboard()
                gaSendWhatButton(gaPurposesTypeSale, buttonGenerateLink.getText())
                setContinue()
            }

            txtSuperLink.setOnClickListener {
                requireActivity().browse(PREVENT_FRAUD_LINK)
            }

            buttonAdvancedOption.setOnClickListener {
                onClickAdvancedOptions(it)
            }
        }
    }

    private fun configurePredefinedValue(textView: AppCompatTextView, predefinedValue: Long) {
        binding?.apply {
            if (textInputEditCreateLinkValue.text.isNullOrEmpty()) {
                linkValue = BigDecimal.valueOf(ZERO_DOUBLE)
            }
            gaSendFormValue(gaPurposesTypeSale, textView.text.toString())
            addToLinkValue(BigDecimal.valueOf(predefinedValue))
            textInputEditCreateLinkValue.setText(
                linkValue.toDouble().toPtBrRealStringWithoutSymbol()
            )
        }
    }

    @SuppressLint("CheckResult")
    private fun onClickAdvancedOptions(view: View) {
        val bs = PgLinkOptionsAdvancedBottomSheet.newInstance(quickFilter, paymentLinkDTO)

        bs.onContinue = {
            quickFilter = QuickFilter.Builder().from(quickFilter)
                .sku(it.sku)
                .expiredDate(it.expiredDate)
                .softDescriptor(it.softDescriptor)
                .maximumInstallment(it.maximumInstallment)
                .quantity(it.quantity)
                .build()
            createLinkPaymentPresenter.setFilter(quickFilter)
        }

        logShowAdvancedOptions()
        bs.show(parentFragmentManager, PgLinkOptionsAdvancedBottomSheet::class.java.simpleName)
    }

    private fun setContinue() {
        binding?.apply {
            linkTitle = textInputEditCreateLinkTitle.getText().trim()
            linkValue =
                textInputEditCreateLinkValue.text.toString().trim().textToMoneyBigDecimalFormat()

            UserPreferences.getInstance().token.run {
                createLinkPaymentPresenter.generateLink(this, LinkRequest(linkTitle, linkValue))
            }
        }
    }

    override fun showPeriodicityCharge() {
        binding?.layoutPeriodicyCharge?.apply {
            isRecurrentSale = true
            linearLayoutRecurrentSale.visible()
            constraintLayoutPeriodicy.setOnClickListener(::onPeriodicFieldClick)
            constraintLayoutFinalDate.setOnClickListener(::onFinalDateFieldClick)
        }
    }

    private fun onPeriodicFieldClick(v: View) {
        val pickerBS = PickerBottomSheetFragment.newInstance(
            getString(R.string.text_view_sale_identify_sale_periodicy_title),
            getFrequencyList(),
            lockCollapse = true
        ).apply {
            onItemSelectedListener = object : PickerBottomSheetFragment.OnItemSelectedListener {
                override fun onSelected(selectedItem: Int) {
                    val frequency = getFrequencyList()[selectedItem]
                    val frequencyFilter = TypeSalePeriodicEnum.values().find {
                        it.type == frequency
                    }?.name ?: EMPTY_STRING

                    quickFilter = QuickFilter
                        .Builder()
                        .from(quickFilter)
                        .frequency(frequencyFilter)
                        .build()
                    createLinkPaymentPresenter.setFilter(quickFilter)

                    binding?.apply {
                        layoutPeriodicyCharge.textViewFrequency.text = frequency
                        buttonGenerateLink.isEnabled = validateLinkInputFields()
                    }
                }
            }
        }
        pickerBS.show(
            requireActivity().supportFragmentManager,
            "$TAG || Picker frequency"
        )
    }

    private fun onFinalDateFieldClick(v: View) {
        val cal = chargeDate.toCalendar()
        val dia = CalendarCustom.getDay(cal)
        val mes = CalendarCustom.getMonth(cal)
        val ano = CalendarCustom.getYear(cal)

        CalendarDialogCustom(
            TOMORROW, MAXIMUM_1800_DAYS, SELECTED_LESS_TWO_DAY, dia, mes, ano,
            getString(R.string.recurrent_final_date), context as Context,
            { _, year, monthOfYear, dayOfMonth ->
                chargeDate.setDate(year, monthOfYear, dayOfMonth)

                quickFilter = QuickFilter.Builder().from(quickFilter)
                    .finalRecurrentExpiration(chargeDate.formatDateToAPI())
                    .build()
                createLinkPaymentPresenter.setFilter(quickFilter)

                binding?.apply {
                    layoutPeriodicyCharge.textViewChargeDate.text = chargeDate.formatBRDate()
                    buttonGenerateLink.isEnabled = validateLinkInputFields()
                }
            }, R.style.DialogThemeMeusRecebimentos
        ).show()
    }

    fun getFrequencyList() = TypeSalePeriodicEnum.values().map { it.type }

    override fun onHelpButtonClicked() {
        gaSendButtonTooltip(HELP_CENTER)
        HelpMainActivity.create(
            requireActivity(),
            getString(R.string.text_pg_lk_help_title),
            PPL_HELP_ID
        )
    }

    private fun addToLinkValue(valueToBeAdded: BigDecimal) {
        if (linkValue < MAX_VALUE) linkValue = linkValue.add(valueToBeAdded)
    }

    override fun showLoading() {
        binding?.apply {
            frameCreateLinkProgress.visible()
            frameCreateLinkPaymentButton.gone()
            nestedCreateLinKContent.gone()
        }
    }

    override fun hideLoading() {
        binding?.apply {
            frameCreateLinkProgress.gone()
            frameCreateLinkPaymentButton.visible()
            nestedCreateLinKContent.visible()
        }
    }

    override fun errorOnLinkCreation(errorMessage: ErrorMessage) {
        gaSendCallbackGenerateLink(errorMessage)
        logException(errorMessage)
        navigation?.showError(errorMessage)
    }

    override fun linkSuccessfulCreated(createdLink: CreateLinkBodyResponse) {
        gaSendCallbackGenerateLink(null)

        val paymentLink = PaymentLink(
            createdLink.type,
            createdLink.name,
            createdLink.price,
            createdLink.url,
            createdLink.id,
            null,
            ZERO, EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING,
            EMPTY_STRING
        )

        findNavController().navigate(
            CreateLinkPaymentFragmentDirections
                .actionCreateLinkPaymentFragmentToLinkPaymentCreatedFragment(
                    paymentLink, paymentLinkDTO,
                    LinkPaymentCreatedPresenter.LinkType.CREATE.value,
                    true
                )
        )
    }

    override fun showIneligibleUser(errorMessage: ErrorMessage?) {
        gaSendCallbackGenerateLink(errorMessage)
        logException(errorMessage)
        val message = errorMessage?.message ?: getString(R.string.unavailable_service_try_again)
        navigation?.showError(
            getString(R.string.text_title_service_unavailable),
            message,
            getString(R.string.ok),
            R.drawable.img_ineligible_user
        )
    }

    private fun gaSendButtonTooltip(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.HEADER),
                label = listOf(Label.TOOLTIP, labelButton)
            )
        }
    }

    private fun gaSendFormInterection(purposes: String, labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, purposes),
                label = listOf(Label.INTERACAO, labelButton)
            )
        }
    }

    private fun gaSendFormValue(purposes: String, valueButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, purposes),
                label = listOf(valueButton)
            )
        }
    }

    private fun gaSendEnableButton(isEnabledButton: Boolean, labelButton: String) {
        if (isAttached()) {
            if (isEnabledButton) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                    action = listOf(Action.BOTAO, gaPurposesTypeSale),
                    label = listOf(labelButton, LIBERADO)
                )
            }
        }
    }

    private fun gaSendWhatButton(purposes: String, labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, purposes),
                label = listOf(labelButton, CLICK)
            )
        }
    }

    private fun gaSendCallbackGenerateLink(error: ErrorMessage?) {
        val list = ArrayList<String>()
        if (error != null) {
            list.add(ERROR_LOWERCASE)
            list.add(error.httpStatus.toString())
        } else {
            list.add(Label.SUCESSO)
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.CALLBACK, CREATE_NEW_LINK, CHARGE_VALUE),
            label = list
        )
    }

    private fun populateFields(dto: PaymentLinkDTO) {
        dto.productName?.let { itName ->
            binding?.textInputEditCreateLinkTitle?.setText(itName)
        }
        dto.productValue?.let {
            binding?.textInputEditCreateLinkValue?.setText(
                it.toDouble().toPtBrRealStringWithoutSymbol()
            )
        }
        gaPurposesTypeSale = if (paymentLinkDTO?.typeSale?.name == TypeSaleEnum.SEND_PRODUCT.name)
            SEND_PRODUCT
        else
            CHARGE_VALUE
    }

    private fun logScreenView() {
        Analytics.trackScreenView(
            screenName = SCREEN_NAME,
            screenClass = this.javaClass
        )
        ga4.logScreenView(screenPathSetAmount)
    }

    private fun logShowAdvancedOptions() = ga4.logScreenView(screenPathSetCode)

    private fun logException(error: ErrorMessage?) = ga4.logException(screenPathSetAmount, error)

    companion object {
        private val TAG = this::class.java.simpleName
        private const val SCREEN_NAME = "/pagamento-por-link/super-link/identifique-sua-venda"
        private const val TIMEOUT_0_MILLISECONDS = 0L
        private val MIN_VALUE: BigDecimal = BigDecimal.valueOf(0.01)
        private val MAX_VALUE: BigDecimal = BigDecimal.valueOf(20000000.00)
        private const val MIN_TITLE_LENGTH_ALLOWED = 3
        private const val ZERO_EMPTY_VALUE = "0,00"
    }

}