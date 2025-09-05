package br.com.mobicare.cielo.pagamentoLink.delivery.address

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.MachineInstallAddressObj
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Action.CONTINUAR
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_PAYMENT_LINK_DTO
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_QUICKER_FILTER
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.EditTextHelper.Companion.phoneMaskFormatter
import br.com.mobicare.cielo.commons.helpers.EditTextHelper.Companion.zipCodeMaskFormatter
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LIBERADO
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.LOGGI_DADOS_DE_COLETA
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics.Companion.PAGAMENTO_POR_LINK
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pagamentoLink.presentation.PPL_HELP_ID
import br.com.mobicare.cielo.recebaMais.presentation.ui.component.PickerBottomSheetFragment
import kotlinx.android.synthetic.main.activity_collect_address_fragment.*

class CollectAddressFragment : BaseFragment(), CollectAddressView, CieloNavigationListener {

    private lateinit var paymentLinkDTO: PaymentLinkDTO
    private var quickFilter: QuickFilter? = null
    private var phoneNumberString = ""
    private var zipCodeString = ""
    private var isPhoneNumberFirst = true
    private var isZipcodeFirst = true
    private lateinit var presenter: CollectAddressPresenter

    private var cieloNavigation: CieloNavigation? = null


    companion object {
        fun newInstance(extras: Bundle) = CollectAddressFragment().apply { arguments = extras }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_collect_address_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        configureNavigation()
    }

    override fun onStart() {
        super.onStart()
        initTextWatcher()
        loadArguments()
    }

    override fun onResume() {
        Analytics.trackScreenView(
            screenName = "/pagamento-por-link/super-link/identifique-sua-venda/opcoes-de-envio/entrega-loggi/dados-da-coleta-loggi",
            screenClass = this.javaClass
        )
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        this.presenter.onPause()
    }

    fun init() {
        this.presenter = CollectAddressPresenterImpl(this)
        initFields(false)
    }

    private fun configureNavigation() {
        if (requireActivity() is CieloNavigation) {
            this.cieloNavigation = requireActivity() as CieloNavigation
            this.cieloNavigation?.showButton(true)
            this.cieloNavigation?.setTextToolbar(getString(R.string.address_toolbar_title))
            this.cieloNavigation?.setTextButton(getString(R.string.coil_button_next))
            this.cieloNavigation?.enableButton(false)
            this.cieloNavigation?.setNavigationListener(this)
            this.cieloNavigation?.showContent(true)
        }
    }

    private fun loadArguments() {
        this.cieloNavigation?.getSavedData()?.let { bundle ->
            bundle.getParcelable<PaymentLinkDTO>(ARG_PARAM_PAYMENT_LINK_DTO)?.let {
                paymentLinkDTO = it
                populateFields()
            }
            bundle.getSerializable(ARG_PARAM_QUICKER_FILTER)?.let {
                quickFilter = it as QuickFilter
            }
        }
    }

    private fun initBottomSheetAddressType(title: String, list: List<String>?) {
        val pickerBS = PickerBottomSheetFragment.newInstance(title, list).apply {
            this.onItemSelectedListener = object :
                    PickerBottomSheetFragment.OnItemSelectedListener {

                override fun onSelected(selectedItem: Int) {
                    val selectedInstallment = list?.get(selectedItem)
                    selectedInstallment?.run {
                        presenter.validateAddressType(this)
                    }
                }
            }
        }
        pickerBS.show(requireFragmentManager(),
                pickerBS.javaClass.simpleName)
    }

    override fun setAddressTypeGone(type: String) {
        inputAddressType.setText(type)
        addressTypeOthers.gone()
    }

    override fun setEnableAddressTypeOthers(type: String) {
        inputAddressType.setText(type)
        addressTypeOthers.visible()
    }

    override fun getAddressType(addressTypes: List<String>) {
        initBottomSheetAddressType(getString(R.string.address_address_type), addressTypes)
    }

    override fun callValidate(view: CieloTextInputView, errorString: String?) {
        view.setError(errorString)
    }

    override fun onAddressSucess(addressResponse: CepAddressResponse) {
        if (isAttached()) {
            val addressStreet = addressResponse.addresses?.firstOrNull()?.address ?: ""
            val neighborhoodAddress = addressResponse.addresses?.firstOrNull()?.neighborhood ?: ""
            val cityAddress = addressResponse.addresses?.firstOrNull()?.city ?: ""
            val stateAddress = addressResponse.addresses?.firstOrNull()?.state ?: ""
            address.setText(addressStreet)
            neighborhood.setText(neighborhoodAddress)
            city.setText(cityAddress)
            state.setText(stateAddress)
            initFields(addressStreet.isEmpty() || neighborhoodAddress.isEmpty())
        }
    }

    override fun onHelpButtonClicked() {
        HelpMainActivity.create(requireActivity(), getString(R.string.text_pg_lk_help_title), PPL_HELP_ID)
    }

    private fun putValueOnDTO() {
        paymentLinkDTO.phone = this.phoneNumberString
        paymentLinkDTO.address = MachineInstallAddressObj(
                address.getText(),
                number.getText(),
                inputAddressType.getText(),
                zipCode.getText(),
                complement.getText(),
                city.getText(),
                neighborhood.getText(),
                state.getText())
    }

    override fun onAddressError(error: ErrorMessage) {
        initFields(true)
        this.cieloNavigation?.showError(error)
    }

    override fun onAddressNotFound() {
        initFields(true)
    }

    override fun onButtonClicked(labelButton: String) {
        this.paymentLinkDTO?.let { payDto ->
            if (validateFields()) {
                gaSendWhatButton(labelButton)
                putValueOnDTO()
                this.cieloNavigation?.saveData(Bundle().apply {
                    this.putParcelable(ARG_PARAM_PAYMENT_LINK_DTO, paymentLinkDTO)
                    this.putSerializable(ARG_PARAM_QUICKER_FILTER, quickFilter)
                })
                this.findNavController()
                        .navigate(CollectAddressFragmentDirections
                                .actionCollectAddressFragmentToDeliveryLoggiConfigurationFragment())
            }
        }
    }

    override fun onRetry() {
        this.presenter.onGetAddress(zipCodeString)
    }

    override fun onBackButtonClicked(): Boolean {
        gaSendWhatButton(VOLTAR)
        return super.onBackButtonClicked()
    }

    private fun initTextWatcher() {
        phoneNumber.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isUpdate = false
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (!isUpdate) {
                        isUpdate = true
                        val phoneNumberMask = phoneMaskFormatter(it.toString())
                        val phoneNumberString = phoneNumberMask.formattedText.string
                        phoneNumber.setText(phoneNumberString)
                        phoneNumber.setSelection(phoneNumberString.length)

                        this@CollectAddressFragment.phoneNumberString = phoneNumberString
                                .replace("(", "")
                                .replace(") ", "")
                        isUpdate = false
                    }
                }
                checkRequiredFields()
            }
        })
        zipCode.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            var isUpdate = false
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (!isUpdate) {
                        isUpdate = true
                        val zipCodeMask = zipCodeMaskFormatter(it.toString())
                        val zipCodeString = zipCodeMask.formattedText.string
                        zipCode.setText(zipCodeString)
                        zipCode.setSelection(zipCodeString.length)

                        this@CollectAddressFragment.zipCodeString = zipCodeString
                                .replace("-", "")
                        isUpdate = false
                    }
                    if (this@CollectAddressFragment.zipCodeString.length == 8)
                        presenter.onGetAddress(zipCodeString)
                }
                checkRequiredFields()
            }
        })
        zipCode.setOnTextViewFocusChanged(View.OnFocusChangeListener { v, hasFocus ->
            if (!isZipcodeFirst && (!hasFocus && this.zipCodeString.length < 8
                            || TextUtils.isEmpty(this.zipCodeString))) {
                callValidate(zipCode, resources.getString(R.string.address_cep_error))
            } else {
                callValidate(zipCode, null)
                isZipcodeFirst = false
            }
        })
        phoneNumber.setOnTextViewFocusChanged(View.OnFocusChangeListener { v, hasFocus ->
            if (!isPhoneNumberFirst && (!hasFocus && this.phoneNumberString.length < 10
                            || TextUtils.isEmpty(this.phoneNumberString))) {
                callValidate(phoneNumber, resources.getString(R.string.address_phone_error))
            } else
                callValidate(phoneNumber, null)
            isPhoneNumberFirst = false
        })
        this.inputAddressType?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
        this.address?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
        this.number?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
        this.neighborhood?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
        this.state?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
        this.city?.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                checkRequiredFields()
            }
        })
    }

    private fun initFields(enable: Boolean) {
        address.isEnabled = enable
        neighborhood.isEnabled = enable
        city.isEnabled = enable
        state.isEnabled = enable

        viewAddressType.setOnClickListener {
            presenter.getAddressType()
        }

        this.titleCollectAddressTextView?.requestFocus()
    }

    private fun validateFields(): Boolean {
        var isValidate = true
        if (address.getText().isEmpty()) {
            isValidate = false
            callValidate(address, "Endereço obrigatório")
        } else callValidate(address, null)

        if (number.getText().isEmpty()) {
            isValidate = false
            callValidate(number, "Número obrigatório")
        } else callValidate(number, null)

        if (neighborhood.getText().isEmpty()) {
            isValidate = false
            callValidate(neighborhood, "Bairro obrigatório")
        } else callValidate(neighborhood, null)

        if (city.getText().isEmpty()) {
            isValidate = false
            callValidate(city, "Cidade obrigatória")
        } else callValidate(city, null)

        if (state.getText().isEmpty()) {
            isValidate = false
            callValidate(state, "Estado obrigatório")
        } else callValidate(state, null)

        if (phoneNumber.getText().isEmpty()) {
            isValidate = false
            callValidate(phoneNumber, "Telefone obrigatório")
        } else callValidate(phoneNumber, null)

        if (inputAddressType.getText().isEmpty()) {
            isValidate = false
            callValidate(inputAddressType, "Tipo de endereço obrigatório")
        } else callValidate(inputAddressType, null)

        if (addressTypeOthers.isVisible()
                && addressTypeOthers.getText().isEmpty()) {
            isValidate = false
            callValidate(addressTypeOthers, "Tipo de endereço obrigatório")
        } else callValidate(addressTypeOthers, null)

        if (zipCode.getText().isEmpty()) {
            isValidate = false
            callValidate(zipCode, "Cep obrigatório")
        } else callValidate(zipCode, null)
        this.cieloNavigation?.enableButton(true)
        gaSendNextButtonEnable(isValidate)
        return isValidate
    }

    private fun populateFields() {
        this.paymentLinkDTO.phone?.let {
            this.phoneNumber?.setText(it)
        }
        this.paymentLinkDTO.address?.let {
            this.zipCode?.setText(it.zipcode)
            this.number?.setText(it.numberAddress)
            this.inputAddressType?.setText(it.addressType)
            this.complement?.setText(it.referencePoint)
            this.address?.setText(it.streetAddress)
            this.city?.setText(it.city)
            this.state?.setText(it.state)
            this.neighborhood?.setText(it.neighborhood)
        }
        checkRequiredFields()
    }

    private fun checkRequiredFields() {
        var isEnableButton = true
        if (this.phoneNumber.getText().isNullOrEmpty() || this.phoneNumberString.length < 10) {
            isEnableButton = false
        } else if (this.zipCode.getText().isNullOrEmpty() || this.zipCodeString.length < 8) {
            isEnableButton = false
        } else if (this.inputAddressType.getText().isNullOrEmpty()) {
            isEnableButton = false
        } else if (this.address.getText().isNullOrEmpty()) {
            isEnableButton = false
        } else if (this.number.getText().isNullOrEmpty()) {
            isEnableButton = false
        } else if (this.neighborhood.getText().isNullOrEmpty()) {
            isEnableButton = false
        } else if (this.state.getText().isNullOrEmpty()) {
            isEnableButton = false
        } else if (this.city.getText().isNullOrEmpty()) {
            isEnableButton = false
        }
        gaSendNextButtonEnable(isEnableButton)
        this.cieloNavigation?.enableButton(isEnableButton)
    }

    private fun gaSendNextButtonEnable(isEnabled: Boolean) {
        if (isAttached()) {
            if (isEnabled) {
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                    action = listOf(Action.BOTAO, LOGGI_DADOS_DE_COLETA),
                    label = listOf(CONTINUAR, LIBERADO)
                )
            }
        }
    }

    private fun gaSendWhatButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, PAGAMENTO_POR_LINK),
                action = listOf(Action.BOTAO, Action.DELIVERY_TYPE),
                label = listOf(labelButton, "clicado")
            )
        }
    }
}
