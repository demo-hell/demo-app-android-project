package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.addArgument
import br.com.mobicare.cielo.commons.utils.addWithTag
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.rangeTo
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.commons.utils.showMessage
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.recebaMais.BUTTON_PROXIMO
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.GA_RM_SIMULE_AGORA_ACTION
import br.com.mobicare.cielo.recebaMais.GA_RM_SIMULE_AGORA_CATEGORY
import br.com.mobicare.cielo.recebaMais.GA_RM_SIMULE_AGORA_SCREEN
import br.com.mobicare.cielo.recebaMais.GA_RM_SIMULE_AGORA_VALOR_DESEJADO_LABEL
import br.com.mobicare.cielo.recebaMais.domain.Installment
import br.com.mobicare.cielo.recebaMais.domain.LoanSimulationResponse
import br.com.mobicare.cielo.recebaMais.domain.Offer
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.activity.RecebaMaisActivity
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.buttonUserLoanSimulationNext
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.editSimulationLoanValue
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.linearLoanSimulationError
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.linearLoanSimulationParams
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.linearPlotsArrowUp
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.linearSimulationLoanSelectedPaymentDay
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationConditions
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationLoanPaymentDayResult
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationLoanPaymentDaySelected
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationLoanPlotsResult
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationLoanPlotsSelected
import kotlinx.android.synthetic.main.fragment_user_loan_simulation.textSimulationLoanTotalResult
import kotlinx.android.synthetic.main.linear_loan_simulation_error.buttonLoanSimulationErrorRetry
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserLoanSimulationFragment : BaseFragment(), UserLoanContract.View.Simulation,
    PickerBottomSheetFragment.OnItemSelectedListener {

    val userLoanPresenter: UserLoanPresenter by inject {
        parametersOf(this)
    }

    private val offer: Offer? by lazy {
        arguments?.getParcelable(RECEBA_MAIS_OFFER) as Offer?
    }


    private val recebaMaisToken: String? by lazy {
        UserPreferences.getInstance().token
    }

    private var selectedInstallment: Installment? = null

    private var startPaymentDayLocalDt: LocalDateTime? = null

    private var allInstallments: List<Installment>? = null

    var onStateListener: OnStateListener? = null


    companion object {

        const val USER_TOKEN = "br.com.cielo.recebaMais.userToken"
        const val RECEBA_MAIS_OFFER = "br.com.cielo.recebaMais.userLoanSimulationFragment.offer"

        const val PICKER_BOTTOM_SHEET_PLOTS =
            "br.com.cielo.recebaMais.userLoanSimulationFragment.pickerBottomSheetPlots"


        fun create(offer: Offer?): UserLoanSimulationFragment {

            val userLoanSimulationFragment = UserLoanSimulationFragment()

            userLoanSimulationFragment.addArgument(RECEBA_MAIS_OFFER, offer)

            return userLoanSimulationFragment
        }

    }

    interface OnStateListener {
        fun onShowLoading()
        fun onHideLoading()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_loan_simulation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        buttonUserLoanSimulationNext.setOnClickListener {
            gaSimulateButtonNextClick(
                buttonUserLoanSimulationNext.text?.toString() ?: ""
            )
            requireActivity().startActivity<RecebaMaisActivity>(
                "token" to arguments?.getString(USER_TOKEN),
                "offer" to arguments?.getParcelable(RECEBA_MAIS_OFFER),
                RecebaMaisActivity.INSTALLMENT_SELECTED to selectedInstallment.apply {
                    this?.firstInstallmentStartDate = startPaymentDayLocalDt
                        ?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                })
        }


        boldUserLoanConditions()

        offer?.run {

            gaEventSimulate(GA_RM_SIMULE_AGORA_VALOR_DESEJADO_LABEL)

            if (this.id != null
                && this.startDatePaymentFirstInstallment != null
                && this.loanLimit != null) {
                userLoanPresenter.simulate(
                    this.id, this.startDatePaymentFirstInstallment,
                    recebaMaisToken!!, this.loanLimit, this.loanLimit
                )
            }
        }

    }

    private fun boldUserLoanConditions() {

        offer?.run {
            textSimulationConditions.text =
                getString(
                    R.string.text_user_loan_simulation_conditions_param_label,
                    (this.loanLimit?.toDouble() ?: 0.0).toPtBrRealStringWithoutSymbol(),
                    "${this.monthlyInterestRate}%"
                )
        }

        val spannableTextConditions = SpannableStringBuilder.valueOf(textSimulationConditions.text)
        val indexOfMoneySymbol = spannableTextConditions.indexOf("R$")

        spannableTextConditions.setSpan(
            StyleSpan(Typeface.BOLD), indexOfMoneySymbol,
            spannableTextConditions.length - 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textSimulationConditions.text = spannableTextConditions


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loanValueWatcher = ValorMonetarioWatcher()
        editSimulationLoanValue.addTextChangedListener(loanValueWatcher)
        linearLoanSimulationParams.visibility = View.VISIBLE

        configureDaySelectionClickAction()

        configurePlotsClickAction()

        configureErrorRetryClickAction()

        sendGaScreen()
    }


    private fun fillDefaultUserLoanValue() {
        if (isAttached()) {
            offer?.run {
                editSimulationLoanValue.text = SpannableStringBuilder
                    .valueOf((this.loanLimit?.toDouble() ?: 0.0).toPtBrRealStringWithoutSymbol())
            }
        }
    }


    private fun configureErrorRetryClickAction() {
        buttonLoanSimulationErrorRetry.setOnClickListener {
            if (isAttached()) {

                val userLoanInputValue = editSimulationLoanValue.text.toString().trim()

                arguments?.getParcelable<Offer>(RECEBA_MAIS_OFFER)?.run {

                    if (!TextUtils.isEmpty(userLoanInputValue)) {
                        gaEventSimulate(GA_RM_SIMULE_AGORA_VALOR_DESEJADO_LABEL)

                        if (this.id != null
                            && this.startDatePaymentFirstInstallment != null
                            && this.loanLimit != null) {

                            userLoanPresenter.simulate(
                                this.id, this.startDatePaymentFirstInstallment
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                recebaMaisToken!!, BigDecimal
                                    .valueOf(userLoanInputValue.currencyToDouble()), this.loanLimit
                            )
                        }
                    }
                }
            }
        }
    }

    private fun configureDaySelectionClickAction() {
        linearSimulationLoanSelectedPaymentDay.setOnClickListener {

            if (isAttached()) {

                offer?.run {

                    val endDtInstallment = LocalDate.parse(
                        this.endDatePaymentFirstInstallment,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    ).atStartOfDay()

                    fillDateList(startPaymentDayLocalDt, endDtInstallment)
                }
                val calendarCustom = PickerBottomSheetFragment
                    .newInstance(
                        getString(R.string.text_picker_bottom_sheet_calendar_title),
                        dateList
                    ).apply {
                        this.onItemSelectedListener = this@UserLoanSimulationFragment
                    }


                calendarCustom.addWithTag(childFragmentManager, "calendar_custom")
            }

        }
    }

    private fun configurePlotsClickAction() {
        linearPlotsArrowUp.setOnClickListener {

            if (isAttached()) {

                val uiInstallments = getInstallmentsToUi()

                val pickerBottomSheetPlots = PickerBottomSheetFragment
                    .newInstance(
                        getString(R.string.text_picker_bottom_sheet_plots_title),
                        uiInstallments
                    ).apply {


                        this.onItemSelectedListener = object :
                            PickerBottomSheetFragment.OnItemSelectedListener {

                            override fun onSelected(selectedItem: Int) {
                                val selectedInstallment = allInstallments?.get(selectedItem)
                                selectedInstallment?.run {
                                    onInstallmentChange(this)
                                }
                            }

                        }
                    }

                pickerBottomSheetPlots.addWithTag(
                    childFragmentManager,
                    PICKER_BOTTOM_SHEET_PLOTS
                )

            }

        }
    }

    private fun validateSimulationFields(): Boolean {
        return (!TextUtils
            .isEmpty(editSimulationLoanValue.text)
                && textSimulationLoanPaymentDaySelected.visibility == View.VISIBLE
                && textSimulationLoanPlotsSelected.visibility == View.VISIBLE)
    }

    private fun onInstallmentChange(installment: Installment) {
        if (isAttached()) {
            buttonUserLoanSimulationNext?.isEnabled = true
            textSimulationLoanPlotsSelected.text =
                getString(
                    R.string.text_parcel_payment_format,
                    installment.simulation.installments.toString()
                )
            this.selectedInstallment = installment

            updatePlots()
        }
    }


    private fun getInstallmentsToUi(): List<String>? {
        return allInstallments?.map {
            getString(
                R.string.text_loan_plot_picker_template,
                it.simulation.installments,
                it.simulation.installmentAmount.toPtBrRealStringWithoutSymbol(),
                "${it.simulation.monthlyInterestRate}%"
            )
        }
    }


    private fun configureEditSimulationLoanValueInput() {

        editSimulationLoanValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                editSimulationLoanValue.removeTextChangedListener(this)
                buttonUserLoanSimulationNext.isEnabled = false
                editSimulationLoanValue.addTextChangedListener(this)
            }

        })

        editSimulationLoanValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                buttonUserLoanSimulationNext.isEnabled = (!TextUtils
                    .isEmpty(editSimulationLoanValue.text)) &&
                        editSimulationLoanValue.text.toString().trim().currencyToDouble() > 1

                if (buttonUserLoanSimulationNext.isEnabled) {
                    offer?.run {

                        val dtSelected = startPaymentDayLocalDt?.format(
                            DateTimeFormatter
                                .ofPattern("yyyy-MM-dd")
                        ) as String

                        val inputedValue = BigDecimal
                            .valueOf(
                                editSimulationLoanValue.text.toString()
                                    .trim().currencyToDouble()
                            )

                        gaEventSimulate(GA_RM_SIMULE_AGORA_VALOR_DESEJADO_LABEL)

                        if (this.id != null && this.loanLimit != null) {
                            userLoanPresenter.simulate(
                                this.id, dtSelected,
                                recebaMaisToken!!, inputedValue, this.loanLimit
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fillDateList(startDt: LocalDateTime?, endDt: LocalDateTime) {


        val dtFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        if (dateList.isEmpty()) {

            dateList.add(dtFormat.format(startDt))

            startDt?.run {
                for (currentDt in this.plusDays(1).rangeTo(endDt)) {
                    if (currentDt.isBefore(endDt.plusDays(1))) {
                        dateList.add(dtFormat.format(currentDt))
                    }
                }
            }

        }


    }


    val dateList = ArrayList<String>()

    /**
     * =================================
     * */


    override fun simulationResult(loanSimulationResponse: LoanSimulationResponse?) {
        hideErrors()
        //TODO construir a tela com os dados retornados

        selectedInstallment = loanSimulationResponse?.installments?.last()

        allInstallments = loanSimulationResponse?.installments

        editSimulationLoanValue.setText(
            selectedInstallment?.simulation?.amount
                ?.toPtBrRealStringWithoutSymbol()
        )

        buttonUserLoanSimulationNext.isEnabled = validateSimulationFields()

        val lastPaymentDayToSelect = startPaymentDayLocalDt ?: calculateLastPaymentDayToPay(
            offer
                ?.startDatePaymentFirstInstallment.toString()
        )

        startPaymentDayLocalDt = lastPaymentDayToSelect

        textSimulationLoanPaymentDaySelected.text = DateTimeFormatter.ofPattern("dd")
            .format(startPaymentDayLocalDt)

        textSimulationLoanPaymentDayResult.text = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .format(lastPaymentDayToSelect)

        textSimulationLoanPlotsSelected.text = getString(
            R.string.text_parcel_payment_format,
            selectedInstallment?.simulation?.installments.toString()
        )

        updatePlots()

        configureEditSimulationLoanValueInput()

    }

    private fun hideErrors() {
        if (isAttached()) {
            linearLoanSimulationParams.visibility = View.VISIBLE
            buttonUserLoanSimulationNext.visibility = View.VISIBLE
            linearLoanSimulationError.visibility = View.GONE
        }
    }

    private fun updatePlots() {
        val numberFormat = NumberFormat.getNumberInstance()
        numberFormat.maximumFractionDigits = 2

        textSimulationLoanPlotsResult.text = getString(
            R.string
                .text_installment_and_loan_info_template, selectedInstallment?.simulation
                ?.installments.toString(), selectedInstallment?.simulation?.installmentAmount
                ?.toPtBrRealStringWithoutSymbol(),
            "${numberFormat.format(selectedInstallment?.simulation?.monthlyInterestRate)}%"
        )

        textSimulationLoanTotalResult.text = getString(
            R.string.text_total_loan_simulation_amount,
            selectedInstallment?.simulation?.totalAmount?.toPtBrRealStringWithoutSymbol()
        )

        textSimulationConditions.text = getString(
            R.string.text_loan_simulation_title_template,
            offer?.loanLimit?.toDouble()?.toPtBrRealStringWithoutSymbol(),
            "${numberFormat.format(selectedInstallment?.simulation?.monthlyInterestRate)}%"
        )

        boldUserLoanConditions()
    }


    private fun calculateLastPaymentDayToPay(firstInstallmentDt: String?): LocalDateTime {
        val dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        firstInstallmentDt?.run {
            return LocalDate.parse(this.removeNonNumbers(), dtFormatter).atStartOfDay()
        }
        return LocalDateTime.now()
    }


    override fun showLoading() {
        onStateListener?.onShowLoading()
    }

    override fun hideLoading() {
        onStateListener?.onHideLoading()

        if (isAttached()) {
            requireActivity().hideSoftKeyboard()
        }
    }

    override fun showLimitExceededError() {

        if (isAttached()) {

            sendGaExceededError()

            buttonUserLoanSimulationNext.isEnabled = false
            requireActivity().showMessage(
                getString(R.string.text_loan_limit_exceeded_error),
                getString(R.string.text_dialog_loan_limit_error_title)
            )
            editSimulationLoanValue.text = null
        }
    }

    override fun showError() {

    }

    override fun onSelected(selectedItem: Int) {

        val itemSelected = dateList[selectedItem]
        textSimulationLoanPaymentDayResult.text = itemSelected

        val dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val selectedPaymentDay = LocalDate.parse(itemSelected, dtFormatter).atStartOfDay()

        startPaymentDayLocalDt = selectedPaymentDay

        offer?.run {
            gaEventSimulate("dia-do-pagamento-das-parcelas")

            if (this.id != null && this.loanLimit != null) {
                userLoanPresenter.simulate(
                    this.id, startPaymentDayLocalDt
                        ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) as String,
                    recebaMaisToken!!, BigDecimal
                        .valueOf(editSimulationLoanValue.text.toString().trim().currencyToDouble()),
                    this.loanLimit
                )
            }
        }

    }

    override fun showNetworkError() {
        onStateListener?.onHideLoading()

        if (isAttached()) {

            sendGaNetworkError()

            linearLoanSimulationParams.visibility = View.GONE
            buttonUserLoanSimulationNext.visibility = View.GONE
            linearLoanSimulationError.visibility = View.VISIBLE

            fillDefaultUserLoanValue()
        }

    }

    //region Event Ga

    private fun gaSimulateButtonNextClick(buttonName: String = "") {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_SIMULE_AGORA_ACTION),
            label = listOf(Label.BOTAO, BUTTON_PROXIMO)
        )

    }

    private fun gaEventSimulate(campo: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_SIMULE_AGORA_ACTION),
            label = listOf(Label.INTERACAO, campo)
        )

    }

    private fun sendGaExceededError() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_SIMULE_AGORA_CATEGORY),
            action = listOf(GA_RM_RECEBA_MAIS),
            label = listOf(Label.MENSAGEM, "limit", "limit-exceeded-error")
        )
    }

    private fun sendGaNetworkError() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_SIMULE_AGORA_CATEGORY),
            action = listOf(GA_RM_RECEBA_MAIS),
            label = listOf(Label.MENSAGEM, "network", "network-error")
        )
    }

    private fun sendGaScreen() {
        Analytics.trackScreenView(
            screenName = GA_RM_SIMULE_AGORA_SCREEN,
            screenClass = this.javaClass
        )
    }

    //endregion

}