package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.ERROR_401
import br.com.mobicare.cielo.commons.constants.ERROR_500
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.recebaMais.*
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domain.Installment
import br.com.mobicare.cielo.recebaMais.presentation.ui.MyDataContract
import br.com.mobicare.cielo.recebaMais.presentation.ui.presenter.UserLoanDataPresenter
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.fragment_receba_mais_meus_dados.*
import kotlinx.android.synthetic.main.include_receba_mais_condicoes_credito.*
import kotlinx.android.synthetic.main.include_receba_mais_contato.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MyDataFragment internal constructor() : BaseFragment(),
    MyDataContract.View, View.OnKeyListener {

    private lateinit var recebaMaisActionListener: ActivityStepCoordinatorListener
    private lateinit var token: String

    //Condições de financiamento recebida da tela de simulação
    private var installment: Installment? = null

    private val presenter: UserLoanDataPresenter by inject { parametersOf(this) }

    private var _firstEditTextEmailValue: String? = null
    private var _isUserTypingPhone = false
    private var _isUserTypingEmail = false

    companion object {
        fun create(
            actionListener: ActivityStepCoordinatorListener, token: String,
            installment: Installment?
        ): MyDataFragment {
            return MyDataFragment().apply {
                this.recebaMaisActionListener = actionListener
                this.token = token
                this.installment = installment
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_receba_mais_meus_dados, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_receba_mais_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_help) {

            sendGaHelpSelected()

            HelpMainActivity.create(
                requireActivity(),
                getString(R.string.text_rm_help_title),
                RM_HELP_ID
            )
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        showLoading()

        recebaMaisActionListener.setTitle("Meus Dados")

        presenter.setView(this)

        installment?.run {
            presenter.setInstallment(this)
        }

        _firstEditTextEmailValue = text_input_email?.text?.toString()

        callPresenterLoadMechant()
        callPresenterLoadBanks()

        configureEmail()
        configurePhone()

        configureButtonNext()
        configureButtonFailureUpdate()
        configureComponent()

        sendGaScreenView()
    }

    override fun onResume() {
        super.onResume()

        presenter.setView(this)
    }


    //region MyDataContract.View

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {

            error?.let {
                sendGaError(error)
            }

            include_error.visibility = View.VISIBLE
            linear_main.visibility = View.GONE
            progress_main.visibility = View.GONE
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {

            sendGaSessaoExpirada()

            recebaMaisActionListener.onLogout()
        }
    }

    override fun showLoading() {
        super.showLoading()
        if (isAttached()) {
            include_error.visibility = View.GONE
            linear_main.visibility = View.GONE
            progress_main.visibility = View.VISIBLE
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            include_error.visibility = View.GONE
            linear_main.visibility = View.VISIBLE
            progress_main.visibility = View.GONE
        }
    }

    override fun merchantSuccess(email: String, phone: String) {
        if (isAttached()) {
            text_input_telefone.text = SpannableStringBuilder.valueOf(phone)
            presenter.validatePhone(text_input_telefone.text.toString())
            if (phone.isNotEmpty()) {
                textInpuLayoutTelefone.error = ""
            }
            text_input_email.text = SpannableStringBuilder.valueOf(presenter.validadeEmail(email))
            if (text_input_email.text.toString().isNotEmpty()) {
                textInpuLayoutTelefone.error = ""
            }
        }
    }

    override fun banksSuccess(banksResponse: BanksResponse) {
        if (isAttached()) {
            configureFragmentDomicilioBancario(banksResponse)
        }
    }

    override fun showMessageDomicilioObrigatorio() {
        if (isAttached()) {
            activity?.showMessage(
                "Por favor, selecione um de seus domicílios bancários para continuar a contratação.",
                "Selecione um domicílio bancário"
            ) {
                setBtnRight(getString(R.string.ok))
                setOnclickListenerRight {
                    scroll_view.scrollTo(0, 0)
                }
            }
        }
    }


    //endregion

    //region Local functions


    private fun callPresenterLoadBanks() {
        presenter.loadBanks()
    }

    private fun callPresenterLoadMechant() {
        var token = ""

        if (UserPreferences.getInstance().token != null) {
            token = UserPreferences.getInstance().token
        }
        presenter.loadMerchant("Bearer " + token, UserPreferences.getInstance().token)
    }

    private fun configureButtonFailureUpdate() {
        buttonUpdate.setOnClickListener {
            if (isAttached()) {
                showLoading()
                callPresenterLoadMechant()
                callPresenterLoadBanks()
            }
        }
    }

    private fun configureButtonNext() {
        buttonNext.setOnClickListener {
            if (presenter.validate()) {
                gaEventNext()
                recebaMaisActionListener.onNextStep(false)
            }
        }
    }

    private fun configureFragmentDomicilioBancario(banksResponse: BanksResponse) {
        val fragment = MyDomicilioBancarioFragment.create(
            selectDomicilio = {
                if (isAttached()) {
                    presenter.validadeBank(it)
                    sendGaEventScreenDomicilio(banksResponse)
                }
            },
            banks = banksResponse.banks,
            bank = installment?.bank
        )

        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_domicilio_bancario, fragment)
        transaction.commitAllowingStateLoss()
    }


    private fun configurePhone() {

        text_input_telefone.setOnFocusChangeListener { _, hasFocus ->
            if (isAttached()) {

                if (!hasFocus) {
                    val phone = text_input_telefone.text.toString().phone()
                    if (phone.length < 10) {
                        textInpuLayoutTelefone.error = getString(R.string.rm_cc_invalid_phone)
                        return@setOnFocusChangeListener
                    }
                    presenter.validatePhone(phone)
                    text_input_telefone.text = SpannableStringBuilder.valueOf(phone)

                    if (_isUserTypingPhone) {
                        //gaSendFormData(textInpuLayoutTelefone.hint.toString())
                    }

                    textInpuLayoutTelefone.error = ""

                    sendGaEventScreen("telefone")
                } else {
                    scroll_view.scrollTo(0, calculatePositionScreen())
                }
            }
        }

        text_input_telefone.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                presenter.validatePhone("")
                textInpuLayoutTelefone.error = getString(R.string.rm_cc_invalid_phone)
            }
        }
    }

    private fun configureEmail() {

        text_input_email.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                scroll_view.scrollTo(0, calculatePositionScreen())
            } else {
                sendGaEventScreen("email")
            }
        }

        text_input_email.setOnKeyListener { p0, p1, p2 ->

            onKey(p0, p1, p2)

        }

        text_input_email.afterTextChangesNotEmptySubscribe {
            if (isAttached()) {
                val email = it.toString().trim()
                if (email.isEmpty()) {
                    textInpuLayoutEmail.error = getString(R.string.rm_cc_invalid_email)
                    return@afterTextChangesNotEmptySubscribe
                }
                if (!ValidationUtils.isEmail(email)) {
                    textInpuLayoutEmail.error = getString(R.string.rm_cc_invalid_email)
                    return@afterTextChangesNotEmptySubscribe
                }

                if (_isUserTypingEmail) {
                    //gaSendFormData(textInpuLayoutEmail.hint.toString())
                    _firstEditTextEmailValue = email
                }

                presenter.validadeEmail(email)
                textInpuLayoutEmail.error = ""

                if (text_input_email.text.toString() != email) {
                    val selectionStart = text_input_email.selectionStart
                    if (selectionStart == text_input_email.length()) {
                        text_input_email.text = SpannableStringBuilder.valueOf(email)
                        text_input_email.setSelection(text_input_email.length())
                    }
                }
            }
        }

        text_input_email.afterTextChangesEmptySubscribe {
            if (isAttached()) {
                presenter.validadeEmail("")
                textInpuLayoutEmail.error = getString(R.string.rm_cc_invalid_email)
            }
        }
    }

    private fun configureComponent() {
        val date = getPayDate()
        val dayDate = getDayDatePay()
        val fee = installment?.simulation?.registrationFee?.toPtBrRealString() ?: run { "" }
        val annualInterestRate =
            installment?.simulation?.annualInterestRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }
        val annualEffectiveCostRate =
            installment?.simulation?.annualEffectiveCostRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }
        val iof = installment?.simulation?.iof?.toPtBrRealString() ?: run { "" }
        val amount = installment?.simulation?.amount?.toPtBrRealString() ?: run { "" }
        val installmentAmount =
            installment?.simulation?.installmentAmount?.toPtBrRealString() ?: run { "" }
        val monthlyInterestRate =
            installment?.simulation?.monthlyInterestRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }
        val totalAmount = installment?.simulation?.totalAmount?.toPtBrRealString() ?: run { "" }
        val monthlyEffectiveCostRate =
            installment?.simulation?.monthlyEffectiveCostRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }

        if (isAttached()) {
            text_credit_reciver.text = amount
            text_credit_parcel.text =
                "${installment?.simulation?.installments}x de $installmentAmount ($monthlyInterestRate% de juros a.m)"
            text_credit_pay.text = date
            text_credit_pay_day.text = "$dayDate de cada mês"
            text_credit_iof.text = "$iof"
            text_credit_fee.text = "$fee"
            text_credit_cust.text = "$monthlyEffectiveCostRate% a.m e $annualEffectiveCostRate% a.a"
            text_credit_total.text = "$totalAmount (percentual total de $annualInterestRate% a.a.)"
        }
    }

    private fun getDayDatePay(): String {
        return installment?.firstInstallmentStartDate?.let {
            it.substring(0, 2)
        } ?: run {
            ""
        }
    }

    private fun getPayDate(): String {
        return installment?.firstInstallmentStartDate?.let {
            it
        } ?: run {
            ""
        }
    }

    private fun calculatePositionScreen(): Int {
        val cardSize = (include_contatos.bottom - include_contatos.top) / 4
        return include_contatos.top + cardSize
    }
    //endregion


    //region Event Ga

    private fun sendGaEventScreenDomicilio(banksResponse: BanksResponse) {
        if (banksResponse.banks.size > 1) {
            sendGaEventScreen("domicilio-bancario")
        }
    }

    private fun sendGaEventScreen(evento: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_MEUS_DADOS_ACTION),
            label = listOf(Label.INTERACAO, evento)
        )

    }

    private fun sendGaHelpSelected() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.HEADER),
            label = listOf(Label.TOOLTIP)
        )
    }

    private fun sendGaSessaoExpirada() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.MENSAGEM, GA_RM_SESSAO_EXPIRADA, ERROR_401)
        )
    }

    private fun sendGaError(error: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.MENSAGEM, "$error", ERROR_500)
        )
    }

    private fun gaEventNext() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_MEUS_DADOS_ACTION),
            label = listOf(Label.BOTAO, BUTTON_PROXIMO)
        )

    }

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_MEUS_DADOS_SCREEN,
            screenClass = this.javaClass
        )
    }

    override fun onKey(view: View?, p1: Int, p2: KeyEvent?): Boolean {

        if (isAttached()) {
            if (!text_input_email.isFocused) {
                _isUserTypingPhone = true
                _isUserTypingEmail = false
            } else {
                _isUserTypingPhone = false
                _isUserTypingEmail = true
            }
        }

        return true
    }

    //endregion


}