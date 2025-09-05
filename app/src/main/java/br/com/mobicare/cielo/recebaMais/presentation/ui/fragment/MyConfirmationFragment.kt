package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.*
import android.widget.CompoundButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ERROR_401
import br.com.mobicare.cielo.commons.constants.ERROR_500
import br.com.mobicare.cielo.commons.constants.SUCCESS_200
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.recebaMais.*
import br.com.mobicare.cielo.recebaMais.domain.BanksResponse
import br.com.mobicare.cielo.recebaMais.domain.Installment
import br.com.mobicare.cielo.recebaMais.domains.entities.BankAccount
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest
import br.com.mobicare.cielo.recebaMais.domains.entities.Pid
import br.com.mobicare.cielo.recebaMais.presentation.ui.MyDataContract
import br.com.mobicare.cielo.recebaMais.presentation.ui.activity.RecebaMaisContractActivity
import br.com.mobicare.cielo.recebaMais.presentation.ui.dialog.RecebaMaisSuccessDialog
import br.com.mobicare.cielo.recebaMais.presentation.ui.presenter.UserLoanDataPresenter
import kotlinx.android.synthetic.main.fragment_receba_mais_confirmacao.*
import kotlinx.android.synthetic.main.include_condicao_credito.*
import kotlinx.android.synthetic.main.include_contrato.*
import kotlinx.android.synthetic.main.include_meus_dados.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class MyConfirmationFragment : BaseFragment(), MyDataContract.View {

    private lateinit var recebaMaisActionListener: ActivityStepCoordinatorListener
    private lateinit var token: String
    private lateinit var offerId: String

    private val presenter: UserLoanDataPresenter by inject { parametersOf(this) }

    //Condições de financiamento recebida da tela de simulação
    private var installment: Installment? = null

    companion object {
        fun create(
            actionListener: ActivityStepCoordinatorListener, token: String,
            installment: Installment?,
            offerId: String
        ): MyConfirmationFragment {
            return MyConfirmationFragment().apply {
                this.recebaMaisActionListener = actionListener
                this.token = token
                this.installment = installment
                this.offerId = offerId
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_receba_mais_confirmacao, container, false)
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
        presenter.setView(this)
        requireActivity().hideSoftKeyboard()
        configureComponent()
        recebaMaisActionListener.setTitle("Confirmação")
        initView()

        sendGaScreenView()

        errorHandlerCieloUserLoanConfirmation.errorButton?.setOnClickListener {
            recebaMaisActionListener.onNextStep(true)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        stepCheckConfirmation.animateStepProgress()
    }

    private fun initView() {

        errorHandlerCieloUserLoanConfirmation.gone()
        linear_main.visible()
        progress_main.gone()

        buttonConfirmacao.isEnabled = false
        buttonConfirmacao.alpha = 0.8f

        errorHandlerCieloUserLoanConfirmation.errorButton?.setOnClickListener {
            dateContract()
        }

        cb_read_contract.setOnClickListener {

            if ((it as CompoundButton).isChecked) {

                sendGaCheckContratacao()

                buttonConfirmacao.isEnabled = true
                buttonConfirmacao.alpha = 1f
            } else {
                buttonConfirmacao.isEnabled = false
                buttonConfirmacao.alpha = 0.8f
            }

        }

        read_contract.setOnClickListener {
            requireActivity().startActivity<RecebaMaisContractActivity>()
            sendGaButtonMeusDados()
        }

        buttonConfirmacao.setOnClickListener {
            //dados da contracação
            dateContract()

        }


    }

    private fun dateContract() {

        sendGaButtonContratacao()

        val phone = "${installment?.phone?.areaCode} ${installment?.phone?.number}"
        val email = installment?.email


        val bankMock = BankAccount(
            installment?.bank?.accountNumber!!,
            installment?.bank?.accountDigit!!,
            installment?.bank?.agency!!,
            installment?.bank?.code!!.toInt()
        )

        //val bankMock = BankAccount("10000402", "3", "02344", 34)

        val pid = Pid(bankMock)

        val borrowMock = ContratarEmprestimoRecebaMaisRequest(email!!, phone, offerId, pid)
        presenter.borrow(installment?.token!!, borrowMock)
        showLoading()

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
        val installmentAmount = installment?.simulation?.installmentAmount?.toPtBrRealString()
            ?: run { "" }
        val monthlyInterestRate =
            installment?.simulation?.monthlyInterestRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }
        val totalAmount = installment?.simulation?.totalAmount?.toPtBrRealString() ?: run { "" }
        val monthlyEffectiveCostRate =
            installment?.simulation?.monthlyEffectiveCostRate?.toPtBrRealStringWithoutSymbol()
                ?: run { "" }
        val phone = "${installment?.phone?.areaCode} ${installment?.phone?.number}"
        val email = installment?.email

        if (isAttached()) {
            sc_tv_credito_receber.text = amount
            sc_tv_nome_banco.text = installment?.bank?.name
            sc_tv_agencia.text = installment?.bank?.getAgencyFormatted(requireContext()) ?: ""
            sc_tv_conta.text = installment?.bank?.getAccountFormatted(requireContext()) ?: ""
            sc_tv_num_parcelas.text =
                "${installment?.simulation?.installments}x de $installmentAmount ($monthlyInterestRate% de juros a.m)"
            sc_tv_data_primeira_parcela.text = date
            sc_tv_dia_vencimento.text = "$dayDate de cada mês"
            sc_tv_iof.text = "$iof"
            sc_tv_tarifa_cad.text = "$fee"
            sc_tv_custo_efetivo.text =
                "$monthlyEffectiveCostRate% a.m e $annualEffectiveCostRate% a.a"
            sc_tv_total_financiamento.text =
                "$totalAmount (percentual total de $annualInterestRate% a.a.)"
            sc_tv_phone.text = phone.phone()
            sc_tv_email.text = SpannableStringBuilder.valueOf(email)
            ImageUtils.loadImage(imageView8, installment?.bank?.imageURL)

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

    override fun merchantSuccess(email: String, phone: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun banksSuccess(banksResponse: BanksResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            hideLoading()
            if (isAttached()) {
                sendGaError(it)
                errorHandlerCieloUserLoanConfirmation.visible()

                linear_main.gone()
                progress_main.gone()
            }
        }
    }

    override fun showErrorResponse(error: String) {
        super.showErrorResponse(error)
        if (isAttached()) {
            sendGaErroContratacao(error)
            errorHandlerCieloUserLoanConfirmation.visible()
            linear_main.gone()
            progress_main.gone()
        }
    }

    override fun sucessBorrow() {
        hideLoading()
        if (isAttached()) {

            RecebaMaisSuccessDialog.create {
                if (isAttached()) {
                    recebaMaisActionListener.onNextStep(true)
                }

            }.show(
                childFragmentManager,
                RecebaMaisSuccessDialog::class.java.simpleName
            )
            sendGaSuccessContratacao()
        }
    }

    override fun logout(msg: ErrorMessage?) {
        hideLoading()
        if (isAttached()) {
            sendGaSessaoExpirada()
            recebaMaisActionListener.onLogout()
        }
    }

    override fun showLoading() {
        super.showLoading()
        if (isAttached()) {
            errorHandlerCieloUserLoanConfirmation
                .invisible()
            linear_main.invisible()
            progress_main.visible()
        }
    }

    override fun hideLoading() {
        super.hideLoading()
        if (isAttached()) {
            errorHandlerCieloUserLoanConfirmation.invisible()
            linear_main.visible()
            progress_main.invisible()
        }
    }

    //region Event Ga

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
            label = listOf(Label.MENSAGEM, ERROR_401)
        )
    }

    private fun sendGaError(error: ErrorMessage) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.MENSAGEM, "$error", ERROR_500)
        )

    }

    private fun sendGaCheckContratacao() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.CHECK_BOX, GA_RM_TERMOS_DA_CONTRATACAO)
        )
    }

    private fun sendGaButtonContratacao() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.BOTAO, BUTTON_CONTRATAR)
        )

    }

    private fun sendGaButtonMeusDados() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_MEUS_DADOS_CATEGORY),
            label = listOf(Label.BOTAO, BUTTON_PROXIMO)
        )

    }

    private fun sendGaSuccessContratacao() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.MENSAGEM, SUCESSO, SUCCESS_200)
        )
    }

    private fun sendGaErroContratacao(erro: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.FORMULARIO, GA_RM_CONFIRMATION_CONTRATACAO_ACTION),
            label = listOf(Label.MENSAGEM, erro, ERROR_500)
        )
    }

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_CONFIRMACAO_SCREEN,
            screenClass = this.javaClass
        )
    }
    //endregion

}