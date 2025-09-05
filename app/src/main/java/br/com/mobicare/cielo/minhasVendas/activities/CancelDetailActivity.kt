package br.com.mobicare.cielo.minhasVendas.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.moneyUtils.toPtBrRealString
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.afterTextChangesNotEmptySubscribe
import br.com.mobicare.cielo.commons.utils.currencyToDouble
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper.*
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelSucessBottomSheetFragment
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.RequestCancelApi
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseCancelVenda
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_CANCELLATION_CANCEL
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_receba_mais_failure.*
import kotlinx.android.synthetic.main.layout_cancel_detail.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class CancelDetailActivity : BaseLoggedActivity(), OnCancelamentoContract.View {

    val presenterCancel: CancelamentoPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(supportFragmentManager)
    }

    private val ga4: MySalesGA4 by inject()
    private var currentOtpGenerated: String? = null
    private var isOtpInitialized = false;

    //variable
    lateinit var sale: ResponseBanlanceInquiry
    private var radio: RadioButton? = null
    private var isEligible: Boolean = false

    private var compositeHandler = CompositeDisposableHandler()

    companion object {
        const val DETALHES_CANCELAMENTO = "DETALHES_CANCELAMENTO"
        const val ELEGIBILITY = "ELEGIBILITY"
        fun newIntent(
            context: Context, sale: ResponseBanlanceInquiry, elegibility: Boolean
        ) = Intent(
            context, CancelDetailActivity::class.java
        ).apply {
            putExtra(DETALHES_CANCELAMENTO, sale)
            putExtra(ELEGIBILITY, elegibility)
        }
    }

    /**
     * onCreate
     * @param savedInstanceState
     * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cancel_detail)
        compositeHandler.start()
        //ga
        Analytics.trackScreenView(
            screenName = SCREENVIEW_CANCELAR_VENDA_DETALHES, screenClass = javaClass
        )
        setupToolbar(
            toolbar_include as Toolbar, resources.getString(R.string.cancelamento_detalhes)
        )
        vf_dt_cancel.displayedChild = 1
        loadParams()

        if (isEligible) loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeHandler.destroy()
    }

    private fun loadParams() {
        intent?.extras?.let {
            sale = it.getSerializable(DETALHES_CANCELAMENTO) as ResponseBanlanceInquiry
            isEligible = it.getBoolean(ELEGIBILITY)
        }
    }

    private fun loadData() {
        sale.availableAmount.let { amount ->
            ev_valor_disponivel.setText(amount.toPtBrRealString())
        }
        ev_valor_disponivel.addTextChangedListener(
            ev_valor_disponivel.getMaskMoney(
                ev_valor_disponivel
            )
        )
        populateDetailCancel()
        radioOptions()
        buttonSendCancel()
    }

    private fun setupButtonEnabledValidation() {
        compositeHandler.compositeDisposable.add(ev_valor_disponivel.afterTextChangesNotEmptySubscribe {

            if (it.toString().currencyToDouble().toBigDecimal() > BigDecimal.ZERO && it.toString().trim().currencyToDouble()
                    .toBigDecimal() <= sale.availableAmount.toBigDecimal()
            ) {

                btn_confirmar_cancelamento.isEnabled = true
                btn_confirmar_cancelamento.background = ContextCompat.getDrawable(this, R.drawable.btn_cancel_selector)

            } else {

                btn_confirmar_cancelamento.isEnabled = false
                btn_confirmar_cancelamento.background = ContextCompat.getDrawable(this, R.drawable.btn_cancel_unselector)

            }
        })
    }

    /**
     * método que popula o detalhe da venda
     * no layout
     * */
    private fun populateDetailCancel() {
        if (isAttached()) {
            sale.let {
                sale.saleDate.let { tv_cd_data_value.text = formateDateCancel(it) }
                sale.grossAmount.let { tv_cd_vs_value.text = it.toPtBrRealString() }
                Picasso.get().load(sale.imgCardBrand).into(tv_cd_vs_brand_value)
                sale.paymentTypeDescription.let { tv_cd_formpayment_value.text = it }
                sale.availableAmount.let { tv_cd_vs_disponivel_value.text = it.toPtBrRealString() }
            }
        }
    }

    /**
     * método que ver a pega o click do radio button
     * no onCheckChangeListener
     * */
    @SuppressLint("NewApi")
    private fun radioOptions() {

        if (isAttached()) {
            btn_confirmar_cancelamento.isEnabled = false
            evValorDisponivelStatusDisabled()

            rg_options_cancel.setOnCheckedChangeListener { group, checkedId ->
                btn_confirmar_cancelamento.isEnabled = true
                radio = findViewById(checkedId)
                btn_confirmar_cancelamento.background = ContextCompat.getDrawable(this, R.drawable.btn_cancel_selector)
                verificationStateRadio()

                setupButtonEnabledValidation()
            }
        }
    }

    /**
     * método para verificar o status do radio para
     * habilitar ou desabilitar o campo valor disponível
     * */
    private fun verificationStateRadio() {

        if (isAttached()) {
            when {
                (radio?.text!! == "Valor Total") -> {
                    evValorDisponivelStatusDisabled()
                    sale.availableAmount.let { ev_valor_disponivel.setText(it.toPtBrRealString()) }
                    gaSendCheckBox(radio?.text.toString())
                }

                else -> {
                    evValorDisponivelStatusEnabled()
                    sale.availableAmount.let { ev_valor_disponivel.setText(it.toPtBrRealString()) }
                    ev_valor_disponivel.requestFocus()
                    val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(ev_valor_disponivel, InputMethodManager.SHOW_IMPLICIT)
                    gaSendCheckBox(radio?.text.toString())
                }
            }
        }
    }


    /**
     * método que envia o cancelamento da venda para api
     * */
    private fun buttonSendCancel() {
        if (isAttached()) {
            btn_confirmar_cancelamento.setOnClickListener {
                gaSendCancelEvent()
                vf_dt_cancel.displayedChild = 0
                modalScreenSucessCancel()
            }
        }
    }

    private fun gaSendCancelEvent() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(CANCELAR_VENDAS_EVENT),
            label = listOf(Label.BOTAO, (findViewById<RadioButton>(rg_options_cancel.checkedRadioButtonId)).text.toString().toLowerCasePTBR())
        )
    }

    /**
     * método que mostra o status disabilitado
     * editexto do valor disponível
     * */
    private fun evValorDisponivelStatusDisabled() {
        if (isAttached()) {
            ev_valor_disponivel.isEnabled = false
            ev_valor_disponivel.alpha = 0.5f
        }
    }

    /**
     * método que mostra o status habilitado
     * editexto do valor disponível
     * */
    private fun evValorDisponivelStatusEnabled() {
        if (isAttached()) {
            ev_valor_disponivel.alpha = 1f
            ev_valor_disponivel.isEnabled = true
        }
    }

    /**
     * método que mostra o modal quando a resposta da api dar sucesso
     * */
    private fun modalScreenSucessCancel() {
        val listSales = ArrayList<RequestCancelApi>()

        val value2 = ev_valor_disponivel.text.toString().trim().replace("R$", "").replace(" ", "")
        var v = value2.currencyToDouble()

        var saleRequest = RequestCancelApi(
            sale.grossAmount, sale.saleDate, sale.cardBrandCode, sale.productCode, sale.authorizationCode, sale.nsu, v, currentDateCancel()
        )

        listSales.add(saleRequest)

        validationTokenWrapper.generateOtp(showAnimation = isEligible) { otpCode ->
            presenterCancel.sendVendaToCancel(listSales, otpCode)
        }
    }

    /**
     * método que mostra o modal quando clica no interrogação da topbar
     * */
    fun showAlert(title: String, message: String) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this, getString(R.string.ga_extrato)).setTitle(title).setMessage(message)
                .setBtnRight(getString(android.R.string.ok)).show()
        }
    }

    override fun onError(error: ErrorMessage) {
        if (isAttached()) {
            when {
                isEligible -> {
                    validationTokenWrapper.playAnimationError(error,
                        object : CallbackValidateToken {
                            override fun callbackTokenError() {
                                processError(error)
                            }
                        })
                }

                else -> processError(error)
            }
        }
        exceptionGA4(error)
    }

    private fun processError(error: ErrorMessage) {
        when (error.code.toInt()) {
            403, 420 -> {
                errorLayout.setMessageError(error.errorCode, error.errorMessage)
                vf_dt_cancel.displayedChild = 3
            }
        }
        errorLayout.configureActionClickListener(View.OnClickListener {
            modalScreenSucessCancel()
        })
        buttonUpdate.setOnClickListener {
            vf_dt_cancel.displayedChild = 1
        }
    }

    override fun onSucessVendaCancelada(response: ResponseCancelVenda) {
        gaSendCallbackCancel(response.errorMessage)
        if (isEligible) {
            validationTokenWrapper.playAnimationSuccess(
                object : CallbackValidateToken {
                    override fun callbackTokenSuccess() {
                        if (isAttached()) {
                            vf_dt_cancel.displayedChild = 0
                            val ftsucessBS = CancelSucessBottomSheetFragment()
                            ftsucessBS.show(
                                supportFragmentManager,
                                "CancelSucessBottomSheetFragment"
                            )
                        }
                    }
                })
        } else {
            if (isAttached()) {
                vf_dt_cancel.displayedChild = 0
                val ftsucessBS = CancelSucessBottomSheetFragment()
                ftsucessBS.show(
                    supportFragmentManager, "CancelSucessBottomSheetFragment"
                )
            }
        }

    }

    private fun gaSendCallbackCancel(message: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
            action = listOf(SOLICITACAO_DE_CANCELAMENTO_EVENT),
            label = listOf(Label.MENSAGEM, message)
        )
    }

    /**
     * método para formatar a data no detalhes do cancelamento
     * @param value
     * */
    @SuppressLint("NewApi")
    fun formateDateCancel(value: String): String {
        val currentLocalDt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(value)
        return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(currentLocalDt)
    }


    fun currentDateCancel(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun gaSendCheckBox(name: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, CANCELAR_VENDAS_CATEGORY),
                action = listOf(CANCELAR_VENDAS_EVENT),
                label = listOf(Label.BOTAO, name)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_NAME_CANCELLATION_CANCEL)
    }

    private fun exceptionGA4(error: ErrorMessage?) {
        ga4.logException(SCREEN_NAME_SALES_MADE, errorMessage = error)
    }
}