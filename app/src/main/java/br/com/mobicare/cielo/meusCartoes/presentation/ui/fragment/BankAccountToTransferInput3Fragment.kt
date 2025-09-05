package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank_2.*
import kotlinx.android.synthetic.main.fragment_input_transfer_to_account_bank_3.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit


class BankAccountToTransferInput3Fragment : BaseFragment(), BankAccountToTransferInputContract.View {


    override fun updateButtonNextState(enabled: Boolean) {
        if (isAdded) {
            textInputLayoutTransferAmount.error = null
            buttonValueNext.isEnabled = enabled
            if (enabled) textViewMinValueError.gone() else textViewMinValueError.visible()
        }
    }

    override fun errorFields() {
    }

    val presenter: BankAccountToTransferInputPresenter by inject {
        parametersOf(this)
    }

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var bankTransferRequest: BankTransferRequest? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.TRANFER_DIRECT_KEY)

    private var userCreditCard: Card? = null
        get() = arguments?.getParcelable(DirectElectronicTransferActivity.USER_CREDIT_CARD)

    private val inputEnabledSubject: Subject<Boolean> = PublishSubject.create()

    var onInputFinishListener: BankAccountToTransferInputFragment
    .OnInputFinishActionListener? = null

    companion object {

        const val MIN_VALUE: Double = 10.00
        const val TIMEOUT: Long = 500

        fun create(): BankAccountToTransferInput3Fragment {
            return BankAccountToTransferInput3Fragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        onInputFinishListener?.onBloquedBackPressed(false)

        val inflatedView = inflater.inflate(R.layout.fragment_input_transfer_to_account_bank_3,
                container, false)


        return inflatedView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        gaEditTextFocus()
    }

    override fun onResume() {
        super.onResume()

        compositeDisposable.add(
            inputEnabledSubject
                .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    updateButtonNextState(it)
                }, {

                })
        )

        configureButtonValue()

        userCreditCard?.run {
            textAvaiableAmount.text = SpannableStringBuilder
                    .valueOf(this.balance.toPtBrRealString())

            text_card_final.text = this.cardNumber
        }


    }

    private fun configureButtonValue() {
        buttonValueNext.setOnClickListener {

            if(Utils.isNetworkAvailable(requireActivity())){
                if (!TextUtils.isEmpty(textValueMoney.text)) {
                    bankTransferRequest?.run transference@ {

                        this.amount = textValueMoney.text.toString()
                                .trim().currencyToDouble()

                        userCreditCard?.run {
                            presenter.beginTransfer(proxyNumber,
                                    this.balance,
                                    this@transference)

                        }
                    }
                }
            }else{

                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }


        }
    }

    override fun initView() {
        if (isAdded) {
            textValueMoney.addTextChangedListener(object : ValorMonetarioWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    super.afterTextChanged(s)
                    s?.let {

                        val screenWidth: Int = textValueMoney.width
                        val variableTextLenght: Int = ((screenWidth / 66) -
                                resources.displayMetrics.density.toInt())

                        when {
                            it.length > variableTextLenght -> textValueMoney.textSize = 40f
                            it.length > variableTextLenght - 3 -> textValueMoney.textSize = 52f
                            else -> textValueMoney.textSize = 66f
                        }

                        inputEnabledSubject.onNext(s.toString().trim().currencyToDouble() >= MIN_VALUE)
                    } ?: run {
                        inputEnabledSubject.onNext(false)
                    }
                }
            })

        }
    }


    override fun initData() {
    }

    override fun showProgress() {
        onInputFinishListener?.onBloquedBackPressed(true)

        if (isAttached()) {
            buttonValueNext.visibility = View.GONE
            frameTransferProgress.visibility = View.VISIBLE
            textValueMoney.isEnabled = false
        }
    }

    override fun hideProgress() {
        onInputFinishListener?.onBloquedBackPressed(false)

        if (isAttached()) {
            buttonValueNext.visibility = View.VISIBLE
            frameTransferProgress.visibility = View.GONE
            textValueMoney.isEnabled = true
        }
    }


    override fun nextStep(transferResponse: TransferResponse) {
        bankTransferRequest?.run {

            onInputFinishListener?.onFinish(this, transferResponse)
        }
    }

    override fun transferSuccess(successMessage: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wrongTransfer(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showError() {
        if (isAttached()) {
            onInputFinishListener?.onBloquedBackPressed(false)

            requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        }
    }

    override fun unavaiableAmount() {
        textInputLayoutTransferAmount.error = getString(R.string.text_unavaiable_amount_message)
    }


    override fun showWrongInputDataError() {
        onInputFinishListener?.onBloquedBackPressed(false)

        requireActivity().showMessage(getString(R.string.text_wrong_input_data_message),
                getString(R.string.text_transfer_dialog_title))
    }

    override fun showUnavaibleServer() {
        onInputFinishListener?.onBloquedBackPressed(false)

        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
    }


    override fun showWrongExpirationDate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showEmptyCvv() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showUnauthorizedAndClose() {
        requireActivity().showMessage(getString(R.string.text_session_timeout_message),
                getString(R.string.text_transfer_error_title)) {
            setBtnRight(getString(R.string.ok))
            setOnclickListenerRight {
                requireActivity().finish()
                Utils.logout(activity as Activity)
            }
        }
    }

    override fun logout(errorMessage: ErrorMessage) {
        if (isAttached()) {
            val screen = requireActivity() as DirectElectronicTransferActivity
            AlertDialogCustom.Builder(requireContext(), getString(R.string.menu_meus_cartoes))
                    .setTitle(R.string.menu_meus_cartoes)
                    .setMessage("Sessão Expirada.")
                    .setBtnRight(getString(R.string.ok))
                    .setCancelable(false)
                    .setOnclickListenerRight {
                        if (!screen.isFinishing) {
                            Utils.logout(requireActivity())
                            screen.finish()
                        }

                    }
                    .show()
        }
    }

    fun gaEditTextFocus() {

        textValueMoney.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textValueMoney?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.text_my_cards_value))
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
