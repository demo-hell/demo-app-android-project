package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.barcode.PaymentsBarCodeScanActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import br.com.mobicare.cielo.meusCartoes.presenter.PaymentContract
import br.com.mobicare.cielo.meusCartoes.presenter.PrepaidPaymentPresenter
import kotlinx.android.synthetic.main.fragment_payment_step_2.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal


class PaymentStep2Fragment : BaseFragment(), PaymentContract.View,
        BillsPaymentActivity.OnBarcodeActionListener {
    override fun responseSucess() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var onFinishActionListener: BillsPaymentActivity.OnFinishInputActionListener? = null

    var userCreditCard: Card? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.USER_CREDIT_CARD)

    var paymentRequest: PrepaidPaymentRequest? = null
        get() = if (field == null) {
            arguments?.getParcelable(BillsPaymentActivity.PREPAID_REQUEST_OBJ_KEY)
        } else {
            field
        }

    var isCapturedByCamera: Boolean? = null
        get() = arguments?.getBoolean(BillsPaymentActivity.IS_CAPTURED_BY_CAMERA)

    val paymentPresenter: PrepaidPaymentPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_step_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFinishActionListener?.onBloquedBackPressed(false)

        initView()
        initData()

        textInputBarCode.afterTextChangesNotEmptySubscribe {
            val t = TypefaceEditTextView.unmask(it.toString())
            val fisrtLetter = t.get(0)
            if (fisrtLetter.toString() == "8") {
                buttonPaymentBarcodeInputNext.isEnabled = if(t.length > 47) true else false
            }else{
                buttonPaymentBarcodeInputNext.isEnabled = if(t.length > 46) true else false
            }
            txtBarCode.run {
                error = ""
            }
        }


        textInputBarCode.afterTextChangesEmptySubscribe {
            buttonPaymentBarcodeInputNext.isEnabled = false
        }

        gaEditTextFocus()
    }

    companion object {

        fun create(): PaymentStep2Fragment {
            return PaymentStep2Fragment()
        }


    }

    override fun initView() {

        if(paymentRequest != null){
            getNumberAndDateBarcode(paymentRequest!!.code)
        }
        //view
        buttonPaymentBarcodeInputNext.setOnClickListener {

            if (Utils.isNetworkAvailable(requireActivity())) {

                //TODO popular objeto paymentRequest e repassar o cartão de crédito

                val barcoWithoudMask = textInputBarCode.text.toString().replace(".","").replace("-","").replace(" ", "")

                val barcodIsValid = paymentPresenter.validationBarcodeValid(barcoWithoudMask)

                if(barcodIsValid){
                    if (paymentRequest == null) {
                        paymentRequest = PrepaidPaymentRequest(BigDecimal.ZERO, "",
                                barcoWithoudMask, BigDecimal.ZERO,
                                isCapturedByCamera as Boolean, "CONSUMPTION", BigDecimal.ZERO)
                    }

                    paymentRequest?.run {
                        onFinishActionListener?.onFinish(this,
                                userCreditCard = userCreditCard)
                    }
                }else{
                    txtBarCode.run {
                        error = getString(R.string.text_invalid_barcode)
                    }
                }

            } else {
                requireContext().showMessage(getString(R.string.title_error_wifi_subtitle),
                        title = getString(R.string.title_error_wifi_title))
            }

        }

        btn_camera.setOnClickListener {

            requireActivity().startActivityForResult(Intent(activity, PaymentsBarCodeScanActivity::class.java),
                    BillsPaymentActivity.BARCODE_SCAN_CODE)
        }

        textInputBarCode.addTextChangedListener(textInputBarCode
                .validateBoletoOrConta(textInputBarCode, btn_camera, this))


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
        requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        requireActivity().finish()
        requireActivity().showExpiredSessionAndLogout()
    }

    override fun onBarcodeCaptured(barcode: String?) {

        if (!barcode.isNullOrEmpty()) {
            if (paymentRequest == null) {
                paymentRequest = PrepaidPaymentRequest(BigDecimal.ZERO,
                        "", barcode, BigDecimal.ZERO,
                        true, "", BigDecimal.ZERO)
            }
            paymentRequest?.run {
                code = barcode
                onFinishActionListener?.onFinish(this, userCreditCard = userCreditCard)
            }
        }
    }


    /**
     * method to insert barcode in the textInputBarCode EditText
     * @param barcode
     * */
    override fun getNumberAndDateBarcode(barcode: String) {
        val barcodeValidation = paymentPresenter.validationBarcode(barcode) + "0"
        Handler().postDelayed({
            textInputBarCode.setText(barcodeValidation)
        }, 100)

    }

    override fun finishCreatePayment(prepaidPaymentResponse: PrepaidPaymentResponse) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        onFinishActionListener?.onBloquedBackPressed(true)
    }

    override fun hideProgress() {
        onFinishActionListener?.onBloquedBackPressed(false)
    }

    override fun logout(errorMessage: ErrorMessage) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun gaEditTextFocus() {

        textInputBarCode.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!textInputBarCode?.getText().toString().trim().isNullOrEmpty()){
                    gaSendInteracao(getString(R.string.txt_barcode_step_2))
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