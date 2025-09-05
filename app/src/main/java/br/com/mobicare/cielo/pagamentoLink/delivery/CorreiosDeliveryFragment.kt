package br.com.mobicare.cielo.pagamentoLink.delivery

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.concrete.canarinho.watcher.CEPTextWatcher
import br.com.concrete.canarinho.watcher.evento.EventoDeValidacao
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CLICK
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUICKER_FILTER
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.CREATE_NEW_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.ENTREGA_CORREIO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.GENERATE_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PESO_DO_PRODUTO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIBERADO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.SEND_PRODUCT
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.presentation.*
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.DeliveryContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.DeliveryPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedFragment
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.CEP
import kotlinx.android.synthetic.main.fragment_correios_delivery.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class CorreiosDeliveryFragment : BaseFragment(), DeliveryContract.View, CieloNavigationListener{

    private var cieloNavigation: CieloNavigation? = null
    private var paymentLinkDTO: PaymentLinkDTO? = null

    val presenter: DeliveryPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun newInstance(extras: Bundle) =
                CorreiosDeliveryFragment().apply { this.arguments = extras }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_correios_delivery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        init()
        initFields()
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(
                requireActivity(),
                getString(R.string.text_pg_lk_help_title),
                PPL_HELP_ID
        )
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.address_toolbar_title_correios))
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.setTextButton(getString(R.string.text_create_link_button_label))
            this.cieloNavigation?.enableButton(false)
            this.cieloNavigation?.setNavigationListener(this)
        }
    }

    private fun init() {
        this.cieloNavigation?.getSavedData()?.let { bundle ->
            bundle.getParcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
                paymentLinkDTO = it
                presenter.setPaymentLinkDTO(it)
            }
            bundle.getSerializable(ARG_PARAM_QUICKER_FILTER)?.let {
                presenter.setFilter(it as QuickFilter)
            }
        }
    }

    private fun initFields() {
        this.cepTextView?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            private val validator: CEPTextWatcher = CEPTextWatcher(object : EventoDeValidacao {
                override fun totalmenteValido(valorAtual: String?) = Unit
                override fun invalido(valorAtual: String?, mensagem: String?) = Unit
                override fun parcialmenteValido(valorAtual: String?) = Unit
            })

            override fun afterTextChanged(s: Editable?) {
                gaSendForm("CEP")
                validator.afterTextChanged(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validator.beforeTextChanged(s, start, count, after)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validator.onTextChanged(s, start, before, count)
                s?.let {
                    this@CorreiosDeliveryFragment.weightTextView?.getText()?.let { itWeight ->
                        val isEnabledButton = (it.isNotEmpty()
                                && it.length == 9) && itWeight.isNotEmpty()
                        gaSendButtonEnable(isEnabledButton)
                        this@CorreiosDeliveryFragment.cieloNavigation
                                ?.enableButton(isEnabledButton)
                    }
                }
            }
        })
        weightTextView?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isUpdate = false
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (!isUpdate) {
                        isUpdate = true
                        this@CorreiosDeliveryFragment.cepTextView?.getText()?.let { itZipCode ->
                            val isEnabledButton = it.isNotEmpty()
                                    && (itZipCode.isNotEmpty() && itZipCode.length == 9)
                            gaSendButtonEnable(isEnabledButton)
                            this@CorreiosDeliveryFragment.cieloNavigation
                                    ?.enableButton(isEnabledButton)
                        }
                        isUpdate = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                gaSendForm(PESO_DO_PRODUTO)
                super.afterTextChanged(s)
            }
        })


        cepTextView.setOnTextViewFocusChanged(
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        if (!cepTextView.getText().trim().isNullOrEmpty() && !cepTextView.getText().trim().equals(getString(R.string.cep_origin))) {
                            gaSendFormInterection(CEP)
                        }

                    }
                })

        weightTextView.setOnTextViewFocusChanged(
                View.OnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        if (!weightTextView.getText().trim().isNullOrEmpty() && !weightTextView.getText().trim().equals(getString(R.string.weight_grams))) {
                            gaSendFormInterection(PESO_DO_PRODUTO)
                        }
                    }
                })

    }

    private fun gaSendFormInterection(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, ENTREGA_CORREIO),
                label = listOf(Label.INTERACAO, labelButton ?: "")
            )
        }
    }

    override fun onButtonClicked(labelButton: String) {
        gaSendWhatButton(labelButton)
        presenter.onNextButtonClicked(weightTextView.getText(), cepTextView.getText())
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendWhatButton(VOLTAR)
        return super.onBackButtonClicked()
    }

    override fun hideLoading() {
        this.cieloNavigation?.showLoading(false)
    }

    override fun showLoading() {
        this.cieloNavigation?.showLoading(true)
    }

    override fun showError(error: ErrorMessage) {
        gaSendCallbackGenerateLink(error)
        this.cieloNavigation?.showError(error)
    }

    override fun linkGenerated(response: CreateLinkBodyResponse, gaDeliveryType: String?) {
        gaSendCallbackGenerateLink(null)

        val paymentLink = PaymentLink(response.type,
            response.name,
            response.price,
            response.url,
            response.id, null, 0, "",""
            ,"","","","","")

        findNavController().navigate(
            CorreiosDeliveryFragmentDirections
                .actionCorreiosDeliveryFragmentToLinkPaymentCreatedFragment(
                    paymentLink, paymentLinkDTO, LinkPaymentCreatedPresenter.LinkType.CREATE.value, true
                )
        )
    }

    override fun showAlert(error: ErrorMessage) {
        gaSendCallbackGenerateLink(error)
        this.cieloNavigation?.showContent(true)
        this.cieloNavigation?.showAlert(message = error.message)
    }

    private fun gaSendForm(field: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, ENTREGA_CORREIO),
                label = listOf(Label.INTERACAO, field)
            )
        }
    }

    private fun gaSendButtonEnable(isEnabled: Boolean) {
        if (isAttached()) {
            if (isEnabled) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                    action = listOf(Action.BOTAO, ENTREGA_CORREIO),
                    label = listOf(GENERATE_LINK, LIBERADO)
                )
            }
        }
    }

    private fun gaSendWhatButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, ENTREGA_CORREIO),
                label = listOf(labelButton, CLICK)
            )
        }
    }

    private fun gaSendCallbackGenerateLink(error: ErrorMessage?) {
        val list = ArrayList<String>()
        list.add(getString(R.string.correios))
        if (error != null) {
            list.add(Label.ERRO)
            list.add(error.httpStatus.toString())
        } else {
            list.add(Label.SUCESSO)
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.CALLBACK, CREATE_NEW_LINK, SEND_PRODUCT),
            label = list
        )
    }
}