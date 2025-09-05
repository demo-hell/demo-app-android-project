package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.deliveryLoggi

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.button.CieloBaseRadioButton
import br.com.cielo.libflue.button.CieloRadioGroup
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUICKER_FILTER
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.DETALHES_DO_PRODUTO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.GENERATE_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIBERADO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PESO_DO_PRODUTO
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ProductDetailDTO
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.linkpgtogerado.PagamentoPorLinkGeradoBottomSheet
import kotlinx.android.synthetic.main.fragment_delivery_loggi_configuration.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class DeliveryLoggiConfigurationFragment : BaseFragment(), DeliveryLoggiConfigurationContract.View,
    CieloNavigationListener{

    val presenter: DeliveryLoggiConfigurationPresenter by inject {
        parametersOf(this)
    }

    private var cieloNavigation: CieloNavigation? = null

    private val avaiableDimensions: List<ProductDetailDTO> = listOf(
        ProductDetailDTO(
            "P", 10,
            10, 10
        ),
        ProductDetailDTO("M", 15, 15, 15),
        ProductDetailDTO("G", 30, 30, 30)
    )

    private var selectedDimensionIndex: Int? = -1

    companion object {
        fun create(bundle: Bundle) =
            DeliveryLoggiConfigurationFragment().apply { this.arguments = bundle }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        weightTextView?.setMaxLength(7)
        radioButtonSmallObjectDimensions
            .setTextValue(getFormatedDimensionsByIndex(0))
        radioButtonMediumObjectDimensions.setTextValue(getFormatedDimensionsByIndex(1))
        radioButtonBigObjectDimensions.setTextValue(getFormatedDimensionsByIndex(2))

        cieloRadioGrouploggiObjectDimensions.setRadioButtonListener(object :
                CieloRadioGroup.RadioButtonListener {
            override fun onItemSelected(button: CieloBaseRadioButton) {
                gaSendNextPackgeSize(button.getTextValue()
                        .replace(" ", "")
                        .replace("cm", ""))
                selectedDimensionIndex = cieloRadioGrouploggiObjectDimensions
                        .getSelectedItemIndex()
                val isEnabledButton = selectedDimensionIndex != -1
                        && weightTextView?.getText()?.isNotEmpty() ?: false
                gaSendButtonEnable(isEnabledButton)
                this@DeliveryLoggiConfigurationFragment.cieloNavigation?.enableButton(
                        isEnabledButton)
            }

        })

        this.weightTextView?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isUpdate = false
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (!isUpdate) {
                        isUpdate = true
                        val isEnabledButton = cieloRadioGrouploggiObjectDimensions
                                ?.getSelectedItemIndex() != -1
                                && it.isNotEmpty()
                        gaSendButtonEnable(isEnabledButton)
                        this@DeliveryLoggiConfigurationFragment.cieloNavigation
                                ?.enableButton(isEnabledButton)
                        isUpdate = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                gaSendWeightProduct(s.toString())
                super.afterTextChanged(s)
            }
        })

        weightTextView.setOnTextViewFocusChanged(
            View.OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    if (!weightTextView?.getText().toString().trim().isNullOrEmpty() && !weightTextView?.getText().toString().trim().equals("Peso em gramas")) {
                        gaSendFormInterection(PESO_DO_PRODUTO)
                    }
                }
            })
    }

    private fun gaSendFormInterection(labelButton: String) {

        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, DETALHES_DO_PRODUTO),
                label = listOf(Label.INTERACAO, labelButton ?: "")
            )
        }
    }

    private fun configuraNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.setTextToolbar(getString(R.string.text_loggi_title_toolbar))
            this.cieloNavigation?.setTextButton(getString(R.string.text_create_link_button_label))
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.enableButton(selectedDimensionIndex != -1)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    private fun loadParams() {
        this.cieloNavigation?.getSavedData()?.let { bundle ->
            bundle.getParcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
                presenter.setPaymentLinkDTO(it)
            }
            bundle.getSerializable(ARG_PARAM_QUICKER_FILTER)?.let {
                presenter.setFilter(it as QuickFilter)
            }
        }
    }

    private fun getFormatedDimensionsByIndex(index: Int): String {
        return getString(
            R.string.text_loggi_object_dimensions_template,
            avaiableDimensions[index].size,
            avaiableDimensions[index].height?.toString(),
            avaiableDimensions[index].width?.toString(),
            avaiableDimensions[index].depth?.toString()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_delivery_loggi_configuration,
            container, false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuraNavigation()
        loadParams()
    }

    override fun onButtonClicked(labelButton: String) {
        requireActivity().hideSoftKeyboard()
        gaSendWhatButton(GENERATE_LINK)
        this.cieloRadioGrouploggiObjectDimensions?.getSelectedItemIndex()?.let { itIndex ->
            this.weightTextView?.getText()?.let { itWeightText ->
                this.presenter.onNextButtonClicked(this.avaiableDimensions[itIndex], itWeightText)
            }
        }
    }

    override fun onRetry() {
        onButtonClicked()
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

    override fun linkGenerated(response: CreateLinkBodyResponse) {
        gaSendCallbackGenerateLink(null)
        PagamentoPorLinkGeradoBottomSheet.newInstance(response)?.apply {
            onDismissListener = object: PagamentoPorLinkGeradoBottomSheet.OnDismissListener {
                override fun onCloseLinkDetailBottomSheet() {
                    if (findNavController().currentDestination?.id == R.id.deliveryLoggiConfigurationFragment) {
                        findNavController()
                                .navigate(R.id.action_deliveryLoggiConfigurationFragment_to_linkPaymentFragment)
                    }
                }
            }
        }.show(this.requireFragmentManager(), "pg_bottom_sheet")
    }

    override fun showAlert(error: ErrorMessage) {
        gaSendCallbackGenerateLink(error)
        this.cieloNavigation?.showContent(true)
        this.cieloNavigation?.showAlert(message = error.message)
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(
            requireActivity(),
            getString(R.string.text_pg_lk_help_title),
            PPL_HELP_ID
        )
    }

    private fun gaSendButtonEnable(isEnabled: Boolean) {
        if (isAttached()) {
            if (isEnabled) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                    action = listOf(Action.BOTAO, Action.PRODUCT_DETAIL),
                    label = listOf(GENERATE_LINK, LIBERADO)
                )
            }
        }
    }

    private fun gaSendNextPackgeSize(packageSize: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.SELECAO, Action.PRODUCT_DETAIL),
                label = listOf(packageSize)
            )
        }
    }

    private fun gaSendWeightProduct(weightProduct: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.FORMULARIO, Action.PRODUCT_DETAIL),
                label = listOf(Label.INTERACAO, weightProduct)
            )
        }
    }

    private fun gaSendWhatButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, Action.PRODUCT_DETAIL),
                label = listOf(labelButton, "clicado")
            )
        }
    }

    private fun gaSendCallbackGenerateLink(error: ErrorMessage?) {
        val list = ArrayList<String>()
        list.add("Loggi")
        if (error != null) {
            list.add("erro")
            list.add(error.httpStatus.toString())
        } else {
            list.add("sucesso")
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
            action = listOf(Action.CALLBACK, "criar novo link", "Enviar um produto"),
            label = list
        )
    }
}