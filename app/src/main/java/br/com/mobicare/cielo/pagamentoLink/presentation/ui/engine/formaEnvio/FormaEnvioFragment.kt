package br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.button.CieloBaseRadioButton
import br.com.cielo.libflue.button.CieloRadioGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUICKER_FILTER
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.commons.utils.spannable.htmlTextFormat
import br.com.mobicare.cielo.databinding.FragmentFormaEnvioBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.domains.ResponsibleDeliveryEnum
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4
import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4.Companion.SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT_SELECT_SHIPPING_METHOD
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FormaEnvioFragment : BaseFragment(), FormaEnvioContract.View, CieloNavigationListener {

    val presenter: FormaEnvioPresenter by inject {
        parametersOf(this)
    }

    private val featureToggle = FeatureTogglePreference.instance

    private var _binding: FragmentFormaEnvioBinding? = null
    private val binding get() = _binding!!

    private var paymentLinkDTO: PaymentLinkDTO? = null
    private var navigation: CieloNavigation? = null
    private val compositeDisposable = CompositeDisposable()

    private val isCorreiosUnavailable
        get() =
            featureToggle.getFeatureTogle(FeatureTogglePreference.LINK_PAGAMENTO_CORREIOS)

    private val ga4: PaymentLinkGA4 by inject()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFormaEnvioBinding
        .inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureNavigation()
        configureViews()
        configureListeners()
        loadParams()
        verifyFeatureToggle()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (compositeDisposable.isDisposed.not()) {
            compositeDisposable.dispose()
        }
        super.onDestroy()
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendWhatButton(VOLTAR)
        return super.onBackButtonClicked()
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.setTextToolbar(getString(R.string.pagamento_link_delivery_options))
                it.setTextButton(getString(R.string.continuar))
                it.showButton(true)
                it.setNavigationListener(this)
                it.showContent(true)
            }
        }
    }

    private fun loadParams() {
        navigation?.getSavedData()?.let { itBundle ->
            itBundle.parcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
                paymentLinkDTO = it
                presenter.setPaymentLinkDTO(it)
                populateFields(it)
            }
            itBundle.serializable<QuickFilter>(ARG_PARAM_QUICKER_FILTER)?.let {
                presenter.setFilter(it)
            }
        }
    }

    private fun configureViews() {
        configureLoggiOptionButton()
        configureCorreiosOptionButton()
        configureFreightOptionButton()
        configureFreeOptionButton()
    }

    private fun configureLoggiOptionButton() {
        binding.apply {
            loggyOptionButtonView.setLayout(R.layout.layout_loggi_option_button)
            instructionsButtonView.apply {
                setText(
                    SpannableString(getString(R.string.text_instructions_button).htmlTextFormat()),
                    TextView.BufferType.SPANNABLE
                )
                gone()
            }
        }
    }

    private fun configureCorreiosOptionButton() {
        binding.correiosOptionButtonView.apply {
            setLayout(R.layout.layout_correios_option_button)
            getLayout()?.findViewById<AppCompatTextView>(R.id.correioTextView)?.apply {
                setText(
                    SpannableString(getString(R.string.text_correios_options).htmlTextFormat()),
                    TextView.BufferType.SPANNABLE
                )
            }
            if (isCorreiosUnavailable) disableCorreiosOptionButton()
        }
    }

    private fun configureFreightOptionButton() {
        binding.freightOptionsButtonView.apply {
            setLayout(R.layout.layout_freight_option_button)
            getLayout()?.findViewById<AppCompatTextView>(R.id.freightTextView)?.apply {
                setText(
                    SpannableString(getString(R.string.text_freight_options).htmlTextFormat()),
                    TextView.BufferType.SPANNABLE
                )
            }
        }
    }

    private fun configureFreeOptionButton() {
        binding.freeOptionsButtonView.apply {
            setLayout(R.layout.layout_free_option_button)
            getLayout()?.findViewById<AppCompatTextView>(R.id.freeTextView)?.apply {
                setText(
                    SpannableString(getString(R.string.text_free_options).htmlTextFormat()),
                    TextView.BufferType.SPANNABLE
                )
            }
        }
    }

    private fun populateFields(dto: PaymentLinkDTO) {
        dto.responsibleDelivery?.let {
            when (it) {
                ResponsibleDeliveryEnum.LOGGI -> {
                    binding.loggyOptionButtonView.callOnClick()
                    presenter.onChoiceButtonClicked(FormaEnvioPresenter.LOGGY_DELIVERY_TYPE)
                }
                ResponsibleDeliveryEnum.CORREIOS -> {
                    binding.correiosOptionButtonView.callOnClick()
                    presenter.onChoiceButtonClicked(FormaEnvioPresenter.CORREIOS_DELIVERY_TYPE)
                }
                ResponsibleDeliveryEnum.CUSTOM -> {
                    binding.freightOptionsButtonView.callOnClick()
                    presenter.onChoiceButtonClicked(FormaEnvioPresenter.CUSTOM_DELIVERY_TYPE)
                }
                ResponsibleDeliveryEnum.FREE_SHIPPING -> {
                    binding.freeOptionsButtonView.callOnClick()
                    presenter.onChoiceButtonClicked(FormaEnvioPresenter.FREE_DELIVERY_TYPE)
                }
            }
            navigation?.apply {
                showButton(true)
                enableButton(true)
            }
        }
    }

    private fun verifyFeatureToggle() {
        binding.apply {
            loggyOptionButtonView.visible(
                featureToggle.getFeatureTogle(FeatureTogglePreference.SUPERLINK_ENTREGA_LOGGI)
            )
            correiosOptionButtonView.visible(
                featureToggle.getFeatureTogle(FeatureTogglePreference.SUPERLINK_ENTREGA_CORREIOS)
            )
            freightOptionsButtonView.visible(
                featureToggle.getFeatureTogle(FeatureTogglePreference.SUPERLINK_ENTREGA_FRETE_FIXO)
            )
        }
    }

    private fun configureListeners() {
        binding.apply {
            radioGroupView.setRadioButtonListener(object : CieloRadioGroup.RadioButtonListener {
                override fun onItemSelected(button: CieloBaseRadioButton) {
                    presenter.onChoiceButtonClicked(button.tag as String)
                }
            })

            instructionsButtonView.setOnClickListener {
                presenter.onInstructionButtonClicked()
            }
        }
    }

    override fun onButtonClicked(labelButton: String) {
        requireActivity().hideSoftKeyboard()
        gaSendWhatButton(labelButton)
        presenter.onNextButtonClicked()
    }

    override fun onRetry() = presenter.onNextButtonClicked()

    override fun showButton(resId: Int) {}

    override fun goToNextStep(
        navDirections: NavDirections,
        dto: PaymentLinkDTO,
        quickFilter: QuickFilter?
    ) {
        navigation?.saveData(Bundle().apply {
            putParcelable(ARG_PARAM_PAYMENT_LINK_DTO, dto)
            putSerializable(ARG_PARAM_QUICKER_FILTER, quickFilter)
        })

        findNavController().navigate(navDirections)
    }

    override fun showLoggyState() {
        requireActivity().hideSoftKeyboard()
        binding.instructionsButtonView.visible()
        updateNavigationButton(R.string.btn_continuar)
    }

    override fun showCorreiosState() {
        binding.instructionsButtonView.gone()
        updateNavigationButton(R.string.btn_continuar)
    }

    override fun showFreightState() {
        binding.instructionsButtonView.gone()
        updateNavigationButton(R.string.btn_continuar)
    }

    override fun showFreeState() {
        requireActivity().hideSoftKeyboard()
        updateNavigationButton(R.string.text_pg_generate_link)
    }

    override fun enableNextButton(isEnabled: Boolean) {
        navigation?.enableButton(isEnabled)
    }

    override fun hideLoading() {
        navigation?.showLoading(false)
    }

    override fun showLoading() {
        navigation?.showLoading(true)
    }

    override fun showError(error: ErrorMessage) {
        navigation?.showError(error)
    }

    override fun linkSuccessfulCreated(createdLink: CreateLinkBodyResponse) {
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
            FormaEnvioFragmentDirections
                .actionFormaEnvioFragmentToLinkPaymentCreatedFragment(
                    paymentLink,
                    paymentLinkDTO,
                    LinkPaymentCreatedPresenter.LinkType.CREATE.value,
                    true
                )
        )
    }

    override fun errorOnLinkCreation(errorMessage: ErrorMessage) {
        logException(errorMessage)
        navigation?.showError(errorMessage)
    }

    override fun showAlert(error: ErrorMessage) {
        navigation?.apply {
            showContent(true)
            showAlert(message = error.message)
        }
    }

    override fun closeWindow() {
        requireActivity().finish()
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(
            requireActivity(),
            getString(R.string.text_pg_lk_help_title),
            PPL_HELP_ID
        )
    }

    private fun updateNavigationButton(@StringRes text: Int) {
        navigation?.apply {
            setTextButton(getString(text))
            enableButton(true)
        }
    }

    private fun disableCorreiosOptionButton() {
        binding.apply {
            correiosOptionButtonView.setOptionEnabled(false)
            tvCorreiosUnavailable.visible()
        }
    }

    override fun gaSendTypeDelivery(deliveryType: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, Action.DELIVERY_TYPE),
                label = listOf(deliveryType)
            )
        }
    }

    private fun gaSendWhatButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, Action.DELIVERY_TYPE),
                label = listOf(labelButton, Action.CLICK)
            )
        }
    }

    private fun logScreenView() {
        Analytics.trackScreenView(
            screenName = SCREEN_NAME,
            screenClass = this.javaClass
        )
        ga4.logScreenView(SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT_SELECT_SHIPPING_METHOD)
    }

    private fun logException(errorMessage: ErrorMessage?) {
        ga4.logException(SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT_SELECT_SHIPPING_METHOD, errorMessage)
    }

    companion object {
        private const val SCREEN_NAME =
            "/pagamento-por-link/super-link/identifique-sua-venda/opcoes-de-envio"

        fun create(bundle: Bundle?) = FormaEnvioFragment().apply {
            arguments = bundle
        }
    }
}