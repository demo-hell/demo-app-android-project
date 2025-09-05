package br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.cielo.libflue.util.text.inputFilters.AlphaNumericInputFilter
import br.com.concrete.canarinho.watcher.ValorMonetarioWatcher
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.CLICK
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUICKER_FILTER
import br.com.mobicare.cielo.commons.constants.ERROR
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.FragmentConfigurationFreightBinding
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.CREATE_NEW_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.VALOR_FRETE
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.FRETE_FIXO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.GENERATE_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIBERADO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.SEND_PRODUCT
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.presentation.*
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.CreateLinkPaymentPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.LinkContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.net.HttpURLConnection

class FreightConfigurationFragment : BaseFragment(), LinkContract.CreateLinkView, CieloNavigationListener {

    private val createLinkPaymentPresenter: CreateLinkPaymentPresenter by inject {
        parametersOf(this)
    }

    private var _binding: FragmentConfigurationFreightBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var navigation: CieloNavigation? = null
    private var paymentLinkDto: PaymentLinkDTO? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentConfigurationFreightBinding
        .inflate(inflater, container, false)
        .apply { _binding = this }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        loadParams()
        configureNameOfFreightEditText()
        configureCostOfFreightEditText()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun configureNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.enableButton(false)
            it.setTextToolbar(getString(R.string.text_fix_freight_label))
            it.setTextButton(getString(R.string.text_create_link_button_label))
            it.setNavigationListener(this)
        }
    }

    private fun loadParams() {
        navigation?.getSavedData()?.let { bundle ->
            bundle.parcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
                paymentLinkDto = it
            }
            bundle.serializable<QuickFilter>(ARG_PARAM_QUICKER_FILTER)?.let {
                createLinkPaymentPresenter.setFilter(it)
            }
        }
    }

    private fun configureNameOfFreightEditText() {
        binding.textInputNameOfFreight.setFilters(
            arrayOf(
                AlphaNumericInputFilter(),
                InputFilter.LengthFilter(INPUT_FREIGHT_NAME_MAX_LENGTH)
            )
        )
    }

    private fun configureCostOfFreightEditText() {
        binding.textInputCostOfFreight.apply {
            setOnTextChangeListener(onFreightCostTextChanged)
            setOnTextViewFocusChanged(onFreightCostFocusChanged)
        }
    }

    private val onFreightCostTextChanged get() = object : CieloTextInputView.TextChangeListener {
        private val validator = ValorMonetarioWatcher.Builder().comSimboloReal().build()

        override fun afterTextChanged(s: Editable?) {
            validator.afterTextChanged(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            validator.beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validator.onTextChanged(s, start, before, count)
            if (s.isNullOrEmpty().not()) {
                gaSendForm()
                val isEnabledButton = s.toString().textToMoneyBigDecimalFormat() > BigDecimal.ZERO
                gaSendButtonEnable(isEnabledButton)
                navigation?.enableButton(isEnabledButton)
            }
        }
    }

    private val onFreightCostFocusChanged get() = View.OnFocusChangeListener { v, hasFocus ->
        if (hasFocus.not()) {
            binding.textInputCostOfFreight.getText().trim().let {
                if (it.isNotEmpty() && it != getString(R.string.text_freight_cost_input_hint)) {
                    gaSendFormInteraction(VALOR_FRETE)
                }
            }
        }
    }

    override fun onButtonClicked(labelButton: String) {
        gaSendWhatButton(GENERATE_LINK)

        paymentLinkDto?.apply {
            costOfFreight = binding.textInputCostOfFreight.getText().textToMoneyBigDecimalFormat()
            nameOfFreight = binding.textInputNameOfFreight.getText().trim()
        }

        createLinkPaymentPresenter.generateLinkWithObjectDelivery(paymentLinkDto)
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendWhatButton(VOLTAR)
        return super.onBackButtonClicked()
    }

    override fun errorOnLinkCreation(errorMessage: ErrorMessage) {
        gaSendCallbackGenerateLink(errorMessage)
        when (errorMessage.httpStatus) {
            HttpURLConnection.HTTP_BAD_REQUEST -> {
                requireActivity().showMessage {
                    setMessage(errorMessage.message)
                    setBtnRight(getString(R.string.ok))
                }
            }
            else -> navigation?.showError(errorMessage)
        }
    }

    override fun linkSuccessfulCreated(createdLink: CreateLinkBodyResponse) {
        gaSendCallbackGenerateLink(null)

        val paymentLink = PaymentLink(
            createdLink.type,
            createdLink.name,
            createdLink.price,
            createdLink.url,
            createdLink.id,
            null,
            ZERO,
            EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE,
            EMPTY_VALUE
        )

        findNavController().navigate(
            FreightConfigurationFragmentDirections
                .actionFreightConfigurationFragmentToLinkPaymentCreatedFragment(
                    paymentLink, paymentLinkDto, LinkPaymentCreatedPresenter.LinkType.CREATE.value, true
                )
        )
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
        navigation?.showContent(true)
    }

    override fun showIneligibleUser(error: ErrorMessage?) {
        val message = error?.message ?: getString(R.string.unavailable_service_try_again)
        navigation?.showIneligibleUser(message)
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(requireActivity(), getString(R.string.text_pg_lk_help_title), PPL_HELP_ID)
    }

    override fun onRetry() {
        this.onButtonClicked()
    }

    private fun gaSendFormInteraction(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, FRETE_FIXO),
                label = listOf(Label.INTERACAO, labelButton)
            )
        }
    }

    private fun gaSendForm() {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO,
                    FRETE_FIXO
                ),
                label = listOf(Label.INTERACAO, VALOR_FRETE)
            )
        }
    }

    private fun gaSendButtonEnable(isEnabled: Boolean) {
        if (isAttached()) {
            if (isEnabled) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                    action = listOf(Action.BOTAO,
                        FRETE_FIXO
                    ),
                    label = listOf(GENERATE_LINK, LIBERADO)
                )
            }
        }
    }

    private fun gaSendWhatButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO,
                    FRETE_FIXO
                ),
                label = listOf(labelButton, CLICK)
            )
        }
    }

    private fun gaSendCallbackGenerateLink(error: ErrorMessage?) {
        val list = ArrayList<String>()
        list.add(FRETE_FIXO)
        if (error != null) {
            list.add(ERROR)
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

    companion object {
        const val INPUT_FREIGHT_NAME_MAX_LENGTH = 128

        fun create(bundle: Bundle?) = FreightConfigurationFragment().apply {
            this.arguments = bundle
        }
    }

}