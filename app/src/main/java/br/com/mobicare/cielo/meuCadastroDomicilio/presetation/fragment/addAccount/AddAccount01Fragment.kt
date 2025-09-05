package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.TIME_CLICK
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.component.selectBottomSheet.SelectBottomSheet
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.meuCadastroDomicilio.MEU_CADASTRO_DOMICILIO_DESTINATION
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.Destination
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.FlagTransferActionListener
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.activity.AddAccountEngineActivity
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4.ScreenView.SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountPresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fl_fragment_add_account_01.*
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroGA4 as ga4

/**
 * create by Enzo Teles
 * */
class AddAccount01Fragment : BaseFragment(), FlagTransferActionListener,
    View.OnFocusChangeListener, View.OnClickListener, CreditCardsContract.BankAccountView {

    var listAccount: ArrayList<String>? = null
    val banks = ArrayList<SelectItem<br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank>>()
    lateinit var actionListner: ActivityStepCoordinatorListener
    lateinit var listener: AddAccountEngineActivity

    var bundleBank: Bundle? = null
    var bank: Destination? = null
    var bankSelected: br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank? = null

    private var isUserTokenOnWhitelist = false

    val presenter: BankAccountPresenter by inject {
        parametersOf(this)
    }


    companion object {
        private const val TITLEACCOUNT = "selecione o tipo de conta"
        private const val TITLEBANK = "selecione seu banco"
        fun newInstance(actionListner: ActivityStepCoordinatorListener) =
            AddAccount01Fragment().apply {
                this.actionListner = actionListner
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(
        R.layout.fl_fragment_add_account_01, container,
        false
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.onButtonSelected(false)
        actionListner.setTitle("Adicionar conta")
        initView()
        presenter.checkEnrollment()
        gaEditTextFocus()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun logScreenView() {
        if (isAttached()){
            ga4.logScreenView(SCREEN_VIEW_MY_PROFILE_ACCOUNT_ADD_ACCOUNT)
        }
    }

    @SuppressLint("NewApi")
    private fun initView() {
        listener.showBottomBar()
        presenter.onResume()
        presenter.allAgencies()
        showLoading()
        listAccount = ArrayList<String>().apply {
            add("Conta corrente")
            add("Conta simples")
            add("Conta poupança")
            add("Entidades Públicas")
        }

        debounceClickButton(this.ev_ac_type_bank, ::showBankBottomSheet)

        //onclicklistener
        ev_ac_type_account.setOnClickListener(this)
        buttonUpdate.setOnClickListener(this)
        //onFoccusListener
        ev_ac_type_account.setOnFocusChangeListener(this)
        ev_ac_type_bank.setOnFocusChangeListener(this)
        //disable keybord
        ev_ac_type_account.showSoftInputOnFocus = false
        ev_ac_type_account.setTextIsSelectable(true)
        ev_ac_type_bank.showSoftInputOnFocus = false
        ev_ac_type_bank.setTextIsSelectable(true)


        //listener para validar butão
        val buttonListener = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //ev_ac_type_conta, ev_ac_type_digito, ev_ac_type_agencia
                if (!ev_ac_type_account?.text.toString().trim().isEmpty()
                    && !ev_ac_type_agencia?.text.toString().trim().isEmpty()
                    && !ev_ac_type_conta?.text.toString().trim().isEmpty()
                    && !ev_ac_type_bank?.text.toString().trim().isEmpty()
                    && !ev_ac_type_digito?.text.toString().trim().isEmpty()
                ) {
                    listener.onButtonSelected(true)
                } else {
                    listener.onButtonSelected(false)
                }
            }

        }
        //onTextChanged
        ev_ac_type_conta.addTextChangedListener(buttonListener)
        ev_ac_type_digito.addTextChangedListener(buttonListener)
        ev_ac_type_agencia.addTextChangedListener(buttonListener)
        ev_ac_type_account.addTextChangedListener(buttonListener)
        ev_ac_type_bank.addTextChangedListener(buttonListener)


    }

    private fun debounceClickButton(view: View, openView: () -> Unit) {
        RxView.clicks(view)
            .throttleFirst(TIME_CLICK, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                openView()
            }, {

            })
    }

    private fun populateBank() {
        val dig = ev_ac_type_agencia.text.toString()
        //val digCustom = dig.substring(dig.length - 1)

        val code = this.bankSelected?.code ?: ""

        bank = Destination(
            ev_ac_type_conta.text.toString(), ev_ac_type_digito.text.toString(),
            ev_ac_type_agencia.text.toString(), "",
            code, false
        )

    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.ev_ac_type_account -> {
                if (hasFocus) {
                    initComponent(TITLEACCOUNT, listAccount, 1)
                }
            }
            R.id.ev_ac_type_bank -> {
                if (hasFocus) {
                    showBankBottomSheet()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ev_ac_type_account -> {
                initComponent(TITLEACCOUNT, listAccount, 1)
            }
            R.id.ev_ac_type_bank -> {
                showBankBottomSheet()
            }
            R.id.buttonUpdate -> {
                initView()
            }
        }
    }

    private fun showBankBottomSheet() {
        SelectBottomSheet
            .Builder<br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank>()
            .title("Lista de bancos disponíveis")
            .hintSearchBar("Buscar por banco")
            .isShowSearchBar(true)
            .list(banks)
            .listener(object : SelectBottomSheet.OnItemListener {
                override fun onItemSelected(item: Any) {
                    val bank = item as br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank
                    gaSendInteracao(ES_ACESSO_BANCO)
                    this@AddAccount01Fragment.bankSelected = bank
                    onSelectedItem("${bank.code} - ${bank.name}", 2)
                }
            })
            .build()
            .showBottomSheet(this.childFragmentManager)
    }

    private fun initComponent(title: String, list: ArrayList<String>?, type: Int) {
        if (isAttached())
            try {
                PickerBottomSheetFragment.newInstance(title, list).apply {
                    this.onItemSelectedListener = object :
                        PickerBottomSheetFragment.OnItemSelectedListener {

                        override fun onSelected(selectedItem: Int) {
                            val selectedInstallment = list?.get(selectedItem)
                            selectedInstallment?.run {
                                onSelectedItem(this, type)
                                gaSendInteracao(ES_ACESSO_TIPO_CONTA)
                            }
                        }
                    }
                }.show(childFragmentManager, tag)
            } catch (ex: Exception) {
                ex.message?.logFirebaseCrashlytics()
            }
    }

    /**
     * método para popular o edittext na tela
     * */
    private fun onSelectedItem(account: String, type: Int) {
        when (type) {
            1 -> {
                ev_ac_type_account.setText(account)
                ev_ac_type_account.setFocusable(false)
            }
            2 -> {
                ev_ac_type_bank.setText(account)
                ev_ac_type_bank.setFocusable(false)
            }
        }

    }

    /**
     * método que pega os bancos da api
     * */
    override fun fillSpinnerBanks(banks: List<br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank>) {
        hideLoading()
        this.banks.addAll(banks.toTypedArray().map { SelectItem("${it.code} - ${it.name}", it) })
    }

    /**
     * método que envia o banco para o segundo passo
     * */
    override fun validade(bundle: Bundle?) {
        populateBank()

        bundleBank = Bundle().apply {
            bank?.let {
                putParcelable(MEU_CADASTRO_DOMICILIO_DESTINATION, it)
            }
            putBoolean(
                AddAccountEngineActivity.IS_USER_MFA_WHITELIST,
                isUserTokenOnWhitelist
            )
        }

        actionListner.onNextStep(false, bundleBank)

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showLoading() {
        layout_man_add_account.gone()
        frameProgress.visible()
        dc_error.gone()
    }

    override fun hideLoading() {
        layout_man_add_account.visible()
        frameProgress.gone()
        dc_error.gone()
    }


    override fun showError(errorMessage: ErrorMessage) {
        layout_man_add_account.gone()
        frameProgress.gone()
        dc_error.visible()
    }


    override fun toggleAccountType(checkingAccountType: Boolean) {
    }

    override fun updateEnabledNext(notEmptyInputs: Boolean) {
    }

    override fun finishStep(bankTransactionData: BankTransferRequest) {
    }

    override fun showUnauthorizedAndClose() {
        requireActivity().showMessage(
            getString(R.string.text_session_timeout_message),
            getString(R.string.text_transfer_error_title)
        ) {
            setBtnRight(getString(R.string.ok))
            setOnclickListenerRight {
                requireActivity().finish()
                Utils.logout(activity as Activity)
            }
        }
    }

    override fun showUnavailableServer() {
        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        requireActivity().finish()
    }


    override fun enrollmentError() {
        isUserTokenOnWhitelist = false
    }

    override fun userEnrollmentEligible() {
        isUserTokenOnWhitelist = true
    }

    fun gaEditTextFocus() {

        ev_ac_type_agencia.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!ev_ac_type_agencia?.getText().toString().trim().isNullOrEmpty()) {
                    gaSendInteracao(getString(R.string.esqueci_senha_agencia_hint))
                }
            }
        }

        ev_ac_type_conta.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!ev_ac_type_conta?.getText().toString().trim().isNullOrEmpty()) {
                    gaSendInteracao(getString(R.string.text_account_hint))
                }
            }
        }

        ev_ac_type_digito.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!ev_ac_type_digito?.getText().toString().trim().isNullOrEmpty()) {
                    gaSendInteracao(getString(R.string.esqueci_senha_digito_conta_hint))
                }
            }
        }
    }

    private fun gaSendInteracao(nameField: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CADASTRO),
                action = listOf(MEUS_CADASTRO_ADICIONAR),
                label = listOf(Label.INTERACAO, nameField)
            )
        }
    }
}