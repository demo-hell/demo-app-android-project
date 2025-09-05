package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.BankHolderType
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank.*
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank_2.*
import mehdi.sakout.fancybuttons.FancyButton
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

class BankAccountToTransferInput2Fragment : BaseFragment(), BankAccountToTransferInputContract.View {

    val presenter: BankAccountToTransferInputPresenter by inject {
        parametersOf(this)
    }

    var name: String? = null
    var cpfOrCnpj: String? = null
    var description: String? = null

    var bankTransferRequest: BankTransferRequest? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.TRANFER_DIRECT_KEY)

    var onFinishedListener: BankAccountToTransferInputFragment.OnInputFinishActionListener? = null

    private val enableInputSubject: Subject<Boolean> = PublishSubject.create()

    private var compositeDisp = CompositeDisposable()

    companion object {
        fun create(): BankAccountToTransferInput2Fragment {
            return BankAccountToTransferInput2Fragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }

        compositeDisp.add(enableInputSubject
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isAdded) {
                        val buttonTransferNext = requireActivity()
                                .findViewById<FancyButton>(R.id.buttonTransferIndentifyNext)
                        buttonTransferNext.isEnabled = it
                    }
                }, {
                }))
        enableInputSubject.onNext(false)

        configureInputsValidation()
        configureMaskCpfCnpj()
        configureButtonNext()
        gaEditTextFocus()

    }


    override fun onDestroy() {
        super.onDestroy()

        if (!compositeDisp.isDisposed) {
            compositeDisp.dispose()
        }
    }

    private fun configureMaskCpfCnpj() {
        textInputEditCpfCnpj.addTextChangedListener(textInputEditCpfCnpj
                .validateCpforCnpj(textInputEditCpfCnpj))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_transfer_to_account_bank_2,
                container, false)
    }

    private fun configureButtonNext() {

        buttonTransferIndentifyNext.setOnClickListener {

            bankTransferRequest?.run {
                accountHolderName = textInputEditName.text.toString()
                accountHolderDocument = textInputEditCpfCnpj.text.toString()
                        .removeNonNumbers()
                description = textInputEditDescription.text.toString()


                var bankHolderType: BankHolderType?

                when {
                    ValidationUtils.isCPF(accountHolderDocument) ->
                        bankHolderType = BankHolderType.PF
                    ValidationUtils.isCNPJ(accountHolderDocument) ->
                        bankHolderType = BankHolderType.PJ
                    else -> {
                        textInputLayoutCpfCnpj.error = getString(R.string.text_invalid_cpf_or_cnpj)
                        return@setOnClickListener
                    }
                }

                accountHolderType = bankHolderType.typeName
                description = if (!description.isNullOrBlank()) {
                    description
                } else {
                    ""
                }

                onFinishedListener?.onFinish(this)
            }

        }
    }

    private fun configureInputsValidation() {

        textInputEditName.afterTextChangesNotEmptySubscribe {

            updateButtonNextState(!TextUtils.isEmpty(it) &&
                    !TextUtils.isEmpty((textInputEditCpfCnpj?.text) ?: ""))
        }


        textInputEditName.afterTextChangesEmptySubscribe {

            updateButtonNextState(!TextUtils.isEmpty(it) &&
                    !TextUtils.isEmpty((textInputEditCpfCnpj?.text) ?: ""))

        }


        textInputEditCpfCnpj.afterTextChangesNotEmptySubscribe {

            textInputLayoutCpfCnpj.run {
                error = null
            }

            updateButtonNextState(!TextUtils.isEmpty((textInputEditName?.text) ?: "") &&
                    !TextUtils.isEmpty(it))
        }


        textInputEditCpfCnpj.afterTextChangesEmptySubscribe {

            textInputLayoutCpfCnpj.run {
                error = null
            }

            updateButtonNextState(!TextUtils.isEmpty((textInputEditName?.text) ?: "") &&
                    !TextUtils.isEmpty(it))

        }


        textInputEditDescription.afterTextChangesNotEmptySubscribe {

            updateButtonNextState(!TextUtils.isEmpty((textInputEditName?.text) ?: "") &&
                    !TextUtils.isEmpty((textInputEditCpfCnpj?.text) ?: ""))
        }


        textInputEditDescription.afterTextChangesEmptySubscribe {


            updateButtonNextState(!TextUtils.isEmpty((textInputEditName?.text) ?: "") &&
                    !TextUtils.isEmpty((textInputEditCpfCnpj?.text) ?: ""))

        }

        textInputEditCpfCnpj.setOnFocusChangeListener { _, hasFocus ->

            if (isAdded) {
                if (!hasFocus) {
                    if (!ValidationUtils.isCPF(textInputEditCpfCnpj.text.toString()) &&
                            !ValidationUtils.isCNPJ(textInputEditCpfCnpj.text.toString())) {
                        textInputLayoutCpfCnpj.run {
                            error = getString(R.string.text_invalid_cpf_or_cnpj)
                        }
                    } else {
                        textInputLayoutCpfCnpj.run {
                            error = null
                        }
                    }
                }
            }
        }

        name = textInputEditName.text.toString()
        cpfOrCnpj = textInputEditCpfCnpj.text.toString()
        description = textInputEditDescription.text.toString()

    }

    override fun initView() {
    }

    override fun initData() {

    }


    override fun nextStep(transferResponse: TransferResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun errorFields() {
    }

    override fun updateButtonNextState(enabled: Boolean) {
        enableInputSubject.onNext(enabled)
    }


    override fun showProgress() {
        onFinishedListener?.onBloquedBackPressed(true)
    }

    override fun hideProgress() {
        onFinishedListener?.onBloquedBackPressed(false)
    }

    override fun transferSuccess(successMessage: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wrongTransfer(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unavaiableAmount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showWrongInputDataError() {
        requireActivity().showMessage(getString(R.string.text_wrong_input_data_message),
                getString(R.string.text_transfer_dialog_title))
    }

    override fun showUnavaibleServer() {
        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
    }

    override fun showWrongExpirationDate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmptyCvv() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showUnauthorizedAndClose() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logout(errorMessage: ErrorMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun gaEditTextFocus() {

        textInputEditName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditName?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_ed_name))
                }
            }
        }

        textInputEditCpfCnpj.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditCpfCnpj?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_ed_cpf_cnpj))
                }
            }
        }

        textInputEditDescription.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditDescription?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_ed_description))
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