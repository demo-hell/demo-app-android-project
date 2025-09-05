package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.payments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceEditTextView
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.codebar.CodebarUtils
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.Authorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferAuthorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.PaymentBottomSheetFragment
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountConfirmTransferenceContract
import br.com.mobicare.cielo.meusCartoes.presenter.PaymentContract
import br.com.mobicare.cielo.meusCartoes.presenter.PrepaidPaymentPresenter
import kotlinx.android.synthetic.main.fragment_payment_step_4.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*


class PaymentStep4Fragment: BaseFragment(), PaymentContract.View,
        BankAccountConfirmTransferenceContract.View {

    var textInputEditCvv:TypefaceEditTextView ?= null
    var textInputEditDt:TypefaceEditTextView ?= null


    override fun responseSucess() {

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_PAGAMENTO_CONTAS, Action.CALLBACK),
            label = listOf(SUCESSO, getString(R.string.txt_title_msg_sucess_payment))
        )

        val intent = Intent("card_sent_success")
        intent.putExtra("titleCard", getString(R.string.txt_title_msg_sucess_payment))
        intent.putExtra("descriptionCard", getString(R.string.txt_subtitle_msg_sucess_payment))
        intent.putExtra("buttonCard", getString(R.string.txt_button_msg_sucess_payment))
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(requireActivity().baseContext).sendBroadcast(intent)

        val screen = requireActivity() as BillsPaymentActivity
        screen.finish()
    }

    var paymentBottomSheetFragment: PaymentBottomSheetFragment? = null

    var onFinishActionListener: BillsPaymentActivity.OnFinishInputActionListener? = null

    val paymentPresenter: PrepaidPaymentPresenter by inject {
        parametersOf(this)
    }

    private var userCreditCard: Card? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.PREPAID_USER_CREDIT_CARD)

    private var paymentRequest: PrepaidPaymentRequest? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.PREPAID_REQUEST_OBJ_KEY)

    private var paymentResponse: PrepaidPaymentResponse? = null
        get() = arguments?.getParcelable(BillsPaymentActivity.PREPAID_RESPONSE_OBJ_KEY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment_step_4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onFinishActionListener?.onBloquedBackPressed(false)

        configureTextPerfomed()
        initView()
        initData()
    }

    companion object {

        fun create(): PaymentStep4Fragment {
            return PaymentStep4Fragment()
        }
    }

    override fun initView() {

        requireActivity().hideSoftKeyboard()
        //view
        paymentResponse?.run {
            txtDetailPaymentValue.text = this.totalAmount.toDouble().toPtBrRealString()
        }

        paymentRequest?.run {
            txtPaymentBarcode.text = CodebarUtils().getDigitableCodeFrom(this.code).codebarLine
            txtPaymentCompany.text = this.beneficiaryName
        }

        //set widget
        txtDTPaymentValue.text = getDateCurrency()

        val dateMaturity = paymentPresenter.validationValueDateMaturity(paymentRequest!!.code)
        val banckName = paymentPresenter.validationNameBank(paymentRequest!!.code)
        val agreement = paymentPresenter.validationNameAgreement(paymentRequest!!.code)


        txtPaymentBankValue.text = if(!banckName.isNullOrEmpty()) banckName else agreement
        typefaceTextView13.visibility = if(dateMaturity != null) View.VISIBLE else View.INVISIBLE
        txtDTMaturityValue.visibility = if(dateMaturity != null) View.VISIBLE else View.INVISIBLE

        txtDTMaturityValue.text = paymentPresenter.validationValueDateMaturity(paymentRequest!!.code)
        txtPaymentCompany.text = paymentRequest!!.beneficiaryName


        buttonMakePayment.setOnClickListener {
            paymentBottomSheetFragment = PaymentBottomSheetFragment.newInstance(this)
            paymentBottomSheetFragment!!.show(requireActivity().supportFragmentManager,
                    "payment_bottom_sheet")
        }

    }

    override fun initData() {
        //api
    }

    override fun confirmTransference(textInputEditDt: TypefaceEditTextView,
                                     textInputEditCvv: TypefaceEditTextView) {
        this.textInputEditCvv = textInputEditCvv
        this.textInputEditDt = textInputEditDt

        val authorization = Authorization(textInputEditCvv.text.toString(), textInputEditDt.text.toString())
        val transfer = TransferAuthorization(authorization)
        paymentPresenter.confirmPayment(userCreditCard?.proxyNumber, paymentResponse?.id, transfer)

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
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CONTA_DIGITAL),
            action = listOf(CONTA_DIGITAL_PAGAMENTO_CONTAS, Action.CALLBACK),
            label = listOf(ERRO, errorMessage.errorMessage, errorMessage.errorCode)
        )
        if (isAttached()) {
            onFinishActionListener?.onBloquedBackPressed(false)
            requireActivity().showMessage(getString(R.string.text_unavaiable_server_message))
        }
    }

    override fun getNumberAndDateBarcode(barcode: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getDateCurrency(): String{
        //set widget
        val formataData = SimpleDateFormat("dd/MM/yyyy")
        val data = Date()
        return formataData.format(data)
    }

    private fun configureTextPerfomed() {
        val text = SpannableStringBuilder()

        text.append(getString(R.string.text_my_cards_detail_box2_title_1_pay).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_2).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlackAcountBoss)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_3).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_4).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_5).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlackAcountBoss)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_6).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlack11spNormal)))

        text.append(" ")

        text.append(getString(R.string.text_my_cards_detail_box2_title_7_pay).addSpannable(
                TextAppearanceSpan(requireActivity(), R.style.TextBlack11spNormal)))
        text_my_cards_detail_box2_title_pay.text = text
    }

    override fun finishCreatePayment(prepaidPaymentResponse: PrepaidPaymentResponse) {
        TODO("Can't be called here")
    }
    override fun showProgress() {
        onFinishActionListener?.onBloquedBackPressed(true)
        if (isAdded) {
            changeDialogShowLoading(View.VISIBLE, View.GONE)
            paymentBottomSheetFragment!!.isCancelable = false
            this.textInputEditDt!!.isEnabled = false
            this.textInputEditCvv!!.isEnabled = false
        }
    }

    override fun hideProgress() {
        onFinishActionListener?.onBloquedBackPressed(false)
        if (isAdded) {
            changeDialogShowLoading(View.GONE, View.VISIBLE)
            Handler().postDelayed({
                paymentBottomSheetFragment!!.isCancelable = true
                this.textInputEditDt!!.isEnabled = true
                this.textInputEditCvv!!.isEnabled = true
            }, 2000)


        }
    }

    private fun changeDialogShowLoading(progressVisibility: Int, buttonTransferVisibility: Int) {
        paymentBottomSheetFragment!!.changeDialogShowLoading(progressVisibility, buttonTransferVisibility)
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
                            Utils.logout(this.requireActivity())
                            screen.finish()
                        }

                    }
                    .show()
        }
    }
}