package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import br.com.mobicare.cielo.meusCartoes.presenter.PaymentContract
import br.com.mobicare.cielo.meusCartoes.presenter.PrepaidPaymentPresenter
import kotlinx.android.synthetic.main.fragment_payment_step_2.*
import kotlinx.android.synthetic.main.fragment_payment_step_3.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


class PaymentStep3Fragment : BaseFragment(), PaymentContract.View {

    var onFinishActionListener: BillsPaymentActivity.OnFinishInputActionListener? = null

    val paymentPresenter: PrepaidPaymentPresenter by inject {
        parametersOf(this)
    }

    private var paymentRequest: PrepaidPaymentRequest? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.PREPAID_REQUEST_OBJ_KEY)

    private var userCreditCard: Card? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.PREPAID_USER_CREDIT_CARD)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_step_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onFinishActionListener?.onBloquedBackPressed(false)
        initView()
        initData()
    }

    companion object {

        fun create(): PaymentStep3Fragment {
            return PaymentStep3Fragment()
        }
    }

    override fun initView() {
        //masck
        textInputValue.addTextChangedListener(textInputValue.getMaskMoney(textInputValue))
        textInputDiscount.addTextChangedListener(textInputValue.getMaskMoney(textInputDiscount))
        textInputEditInterestTax.addTextChangedListener(textInputValue.getMaskMoney(textInputEditInterestTax))
        //validation field
        textInputValue.addTextChangedListener(
                textInputValue.validateFieldStep3(txtValue,  textInputValue, textInputPersonOrCompany,
                        buttonPaymentDetailNext, getString(R.string.error_value_less_than_10)))
        textInputPersonOrCompany.addTextChangedListener(
                textInputPersonOrCompany.validateFieldStep3(txtValue,  textInputValue, textInputPersonOrCompany,
                        buttonPaymentDetailNext, getString(R.string.error_value_less_than_10)))


        val value = paymentPresenter.validationValueCollectionOrTicket(paymentRequest!!.code)
        Handler().postDelayed({
            textInputValue.setText(value)
        }, 100)


        buttonPaymentDetailNext.setOnClickListener {

            if (Utils.isNetworkAvailable(requireActivity())) {

                paymentRequest?.run paymentRequest@ {
                    this.amount = textInputValue.text.toString().moneyToBigDecimalValue()

                    if (!TextUtils.isEmpty(textInputPersonOrCompany.text)) {
                        this.beneficiaryName = textInputPersonOrCompany.text.toString()
                    }

                    if (!TextUtils.isEmpty(textInputDiscount.text)) {
                        this.discount = textInputDiscount.text.toString()
                                .moneyToBigDecimalValue()
                    }

                    if (!TextUtils.isEmpty(textInputEditInterestTax.text)) {
                        this.penalty = textInputEditInterestTax.text.toString()
                                .moneyToBigDecimalValue()
                    }

                    userCreditCard?.run {
                        paymentPresenter.createPayment(cardProxy = this.proxyNumber,
                                paymentRequest = this@paymentRequest)
                    }
                }
            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }

        }

    }

    override fun initData() {
        //api

    }

    override fun showUnauthorizedAndClose() {
        requireActivity().showExpiredSessionAndLogout()
    }

    override fun showWrongInputDataError() {
        requireActivity().showMessage(getString(R.string.text_wrong_input_data_message),
                getString(R.string.text_payment_error_title))
    }

    override fun showUnavaibleServer() {
        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        requireActivity().finish()
    }

    override fun showError(errorMessage: ErrorMessage) {
        if (isAttached()) {
            requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        }
    }

    override fun getNumberAndDateBarcode(barcode: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun finishCreatePayment(prepaidPaymentResponse: PrepaidPaymentResponse) {
        onFinishActionListener?.onFinish(paymentRequest as PrepaidPaymentRequest,
                prepaidPaymentResponse, userCreditCard)
    }

    override fun showProgress() {
        onFinishActionListener?.onBloquedBackPressed(true)

        buttonPaymentDetailNext.visibility = View.GONE
        frameTransferProgressP.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        onFinishActionListener?.onBloquedBackPressed(false)

        buttonPaymentDetailNext.visibility = View.VISIBLE
       frameTransferProgressP.visibility = View.GONE
    }
    override fun responseSucess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun logout(msg: ErrorMessage) {
        if (isAttached()) {
            val screen = requireActivity() as BillsPaymentActivity
            AlertDialogCustom.Builder(requireContext(), getString(R.string.menu_meus_cartoes))
                    .setTitle(R.string.menu_meus_cartoes)
                    .setMessage("Sessão Expirada.")
                    .setBtnRight(getString(R.string.ok))
                    .setOnclickListenerRight {
                        if (!screen.isFinishing) {
                            Utils.logout(this!!.activity!!)
                            screen.finish()
                        }

                    }
                    .show()
        }
    }

    fun gaEditTextFocus() {

        textInputValue.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputValue?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.edt_value_step_3))
                }
            }
        }
        textInputDiscount.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputDiscount?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.edt_discount_step_3))
                }
            }
        }
        textInputEditInterestTax.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputEditInterestTax?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.edt_date_paymento_step_3))
                }
            }
        }
        textInputPersonOrCompany.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputPersonOrCompany?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.edt_person_or_company_step_3))
                }
            }
        }
    }

    private fun gaSendInteracao(nameField: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, MEUS_CARTOES),
                action = listOf(MEUS_CARTOES_PG_CONTA),
                label = listOf(Label.INTERACAO, nameField)
            )
        }
    }

}