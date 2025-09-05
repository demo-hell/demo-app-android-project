package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.component.selectBottomSheet.SelectBottomSheet
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.bottom_sheet_new_password.*
import kotlinx.android.synthetic.main.fl_fragment_add_account_01.*
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BankAccountToTransferInputFragment : BaseFragment(), CreditCardsContract.BankAccountView {

    private var bankSelected: Bank? = null

    private var banksBuffer: List<Bank>? = null

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var isInputsFilled: Subject<Boolean> = PublishSubject.create()

    private var bankChekingAccountSelected: Boolean = true

    var finishActionListener: OnInputFinishActionListener? = null

    interface OnInputFinishActionListener {

        fun onBloquedBackPressed(isBloqued: Boolean)

        fun onFinish(
            bankTransferRequest: BankTransferRequest,
            transferResponse: TransferResponse? = null
        )

    }

    val presenter: BankAccountPresenter by inject {
        parametersOf(this)
    }

    companion object {

        fun create(): BankAccountToTransferInputFragment {
            return BankAccountToTransferInputFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonDirectElectronicTransferNext.isEnabled = false

        finishActionListener?.onBloquedBackPressed(false)

        buttonTransferNextEnabled()
        configureInputFieldsValidators()
        gaEditTextFocus()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.fragment_input_transfer_to_account_bank,
            container, false
        )
    }


    override fun onResume() {
        super.onResume()

        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        configureAccountTypeButton()
        configureInputFieldsValidators()
        configureNextButton()

    }

    override fun onStart() {
        super.onStart()
        presenter.onResume()
        presenter.allAgencies()
    }


    private fun configureNextButton() {

        presenter.validateEmptyFields(
            this.bankSelected,
            textInputEditAgencyNumber.text.toString(),
            textInputEditAccount.text.toString(),
            textInputEditAccountDigit.text.toString()
        )

        compositeDisposable.add(isInputsFilled
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                buttonDirectElectronicTransferNext.isEnabled = it
            }, {

            })
        )


        buttonDirectElectronicTransferNext.setOnClickListener {
            this.bankSelected?.let { itBankSelect ->
                presenter.nextTransferStep(
                    itBankSelect,
                    textInputEditAgencyNumber.text.toString(),
                    textInputEditAccount.text.toString(),
                    textInputEditAccountDigit.text.toString(),
                    buttonCheckingAccountType.isSelected
                )
            }
        }

    }

    private fun buttonTransferNextEnabled() {
        compositeDisposable.add(isInputsFilled
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                buttonDirectElectronicTransferNext.isEnabled = it
            }, {

            })
        )
    }

    private fun fillSpinnerBanks() {
        banksBuffer?.run {

            val banks = ArrayList<SelectItem<Bank>>()
            val banksMapped = this.toTypedArray().map { SelectItem("${it.code} - ${it.name}", it) }
            banks.addAll(banksMapped)
            val bottomSheet = SelectBottomSheet
                .Builder<Bank>()
                .title("Lista de bancos disponíveis")
                .hintSearchBar("Buscar por banco")
                .isShowSearchBar(true)
                .list(banks)
                .listener(object : SelectBottomSheet.OnItemListener {
                    override fun onItemSelected(item: Any) {
                        gaSendInteracao(ES_ACESSO_BANCO)
                        this@BankAccountToTransferInputFragment.onBankSelected(item as Bank)
                    }
                })
                .build()
            bankLayout?.setOnClickListener {
                bottomSheet.showBottomSheet(
                    this@BankAccountToTransferInputFragment.childFragmentManager)
            }
        }
    }

    private fun onBankSelected(bank: Bank) {
        this.tvBankHint?.visible()
        this.bankName?.text = bank.name
        this.bankSelected = bank
    }


    private fun configureInputFieldsValidators() {

        (textInputEditAgencyNumber as TypefaceEditTextView)
            .afterTextChangesNotEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }

        (textInputEditAgencyNumber as TypefaceEditTextView)
            .afterTextChangesEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }


        (textInputEditAccount as TypefaceEditTextView)
            .afterTextChangesNotEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }

        (textInputEditAccount as TypefaceEditTextView)
            .afterTextChangesEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }


        (textInputEditAccountDigit as TypefaceEditTextView)
            .afterTextChangesNotEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }


        (textInputEditAccountDigit as TypefaceEditTextView)
            .afterTextChangesEmptySubscribe {
                presenter.validateEmptyFields(
                    this.bankSelected,
                    (textInputEditAgencyNumber?.text ?: "").toString(),
                    (textInputEditAccount?.text ?: "").toString(),
                    (textInputEditAccountDigit?.text ?: "").toString()
                )
            }
    }


    override fun updateEnabledNext(notEmptyInputs: Boolean) {
        isInputsFilled.onNext(notEmptyInputs)
    }


    private fun configureAccountTypeButton() {

        if (isAttached()) {
            buttonCheckingAccountType.isSelected = bankChekingAccountSelected
            buttonSavingAccoutType.isSelected = !bankChekingAccountSelected
        }

        buttonCheckingAccountType.setOnClickListener {
            presenter.toggleAccountType(true)
            gaSendInteracao(getString(R.string.text_checking_account_type_label))
        }

        buttonSavingAccoutType.setOnClickListener {
            presenter.toggleAccountType(false)
            gaSendInteracao(getString(R.string.text_savings_account_type_label))
        }

    }

    override fun showLoading() {
        finishActionListener?.onBloquedBackPressed(true)
    }

    override fun hideLoading() {
        finishActionListener?.onBloquedBackPressed(false)
    }

    override fun showError(errorMessage: ErrorMessage) {
        if (isAttached()) {
            finishActionListener?.onBloquedBackPressed(false)

            requireActivity().showMessage(errorMessage.message, errorMessage.title) {
                setBtnRight(getString(R.string.ok))
                setOnclickListenerRight {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun fillSpinnerBanks(banks: List<Bank>) {

        this.banksBuffer = banks

        if (isAdded) {
            fillSpinnerBanks()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        presenter.onDestroy()
    }


    override fun toggleAccountType(checkingAccountType: Boolean) {
        bankChekingAccountSelected = checkingAccountType
        if (isAttached()) {
            buttonCheckingAccountType.isSelected = checkingAccountType
            buttonSavingAccoutType.isSelected = !checkingAccountType
        }
    }


    override fun finishStep(bankTransactionData: BankTransferRequest) {
        finishActionListener?.onFinish(bankTransactionData)
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

    fun gaEditTextFocus() {

        textInputEditAgencyNumber.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditAgencyNumber?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_agency_number_hint))
                }
            }
        }

        textInputEditAccount.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditAccount?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_account_hint))
                }
            }
        }

        textInputEditAccountDigit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditAccountDigit?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.esqueci_senha_digito_conta_hint))
                }
            }
        }
    }

    private fun gaSendInteracao(nameField: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CARTOES),
                action = listOf(MEUS_CARTOES_TED),
                label = listOf(Label.INTERACAO, nameField)
            )
        }
    }
}