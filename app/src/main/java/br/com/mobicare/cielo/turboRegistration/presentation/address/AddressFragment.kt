package br.com.mobicare.cielo.turboRegistration.presentation.address

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.DASH
import br.com.mobicare.cielo.commons.constants.EIGHT
import br.com.mobicare.cielo.commons.constants.NINE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.numbersFilter
import br.com.mobicare.cielo.databinding.FragmentAddressBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.domain.model.Address
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError

class AddressFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val registrationViewModel: RegistrationUpdateViewModel by activityViewModels()
    private var navigation: CieloNavigation? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doWhenResumed {
            TurboRegistrationAnalytics.screenViewSelfRegistrationAddress()
        }
        setupNavigation()
        addObserver()
        makeScreen()
        setListeners()
    }

    private fun makeScreen() {
        binding.inputCep.apply {
            this.setMask(CieloTextInputField.MaskFormat.CEP)
            this.textInputEditText.doAfterTextChanged {
                if (it.toString().replace(DASH.toRegex(), EMPTY).length == EIGHT) {
                    this.unsetError()
                    registrationViewModel.getAddressByCep(it.toString().replace(DASH.toRegex(), EMPTY))
                } else {
                    this.setError(getString(R.string.error_empty_cep))
                }
                validateButton()
            }
            this.textInputEditText.setOnFocusChangeListener { _, focused ->
                if (focused.not() && this.textInputEditText.text?.replace(DASH.toRegex(), EMPTY)?.length != EIGHT) {
                    this.setError(getString(R.string.error_empty_cep))
                }
                validateButton()
            }
        }
        binding.inputAddress.apply {
            this.setValidators(
                CieloTextInputField.Validator(
                    rule = { it.extractedValue.length > TWO },
                    errorMessage = getString(R.string.error_empty_address),
                    onResult = { isValid, _ ->
                        if (isValid) validateButton()
                    }
                )
            )
            this.validationMode = CieloTextInputField.ValidationMode.FOCUS_CHANGED
        }
        binding.inputNumber.apply {
            this.setValidators(
                CieloTextInputField.Validator(
                    rule = { it.extractedValue.isNotEmpty() && (it.extractedValue.toIntOrNull() ?: ZERO) > ZERO },
                    onResult = { isValid, _ ->
                        if (isValid.not() && binding.cbNoNumber.isChecked.not()) {
                            this.setError(getString(R.string.error_empty_number))
                        } else {
                            this.unsetError()
                        }
                        validateButton()
                    }
                )
            )
            this.validationMode = CieloTextInputField.ValidationMode.TEXT_CHANGED
            this.textInputEditText.setOnFocusChangeListener { _, focused ->
                if (focused.not() && (this.textInputEditText.text.toString().toIntOrNull() ?: ZERO) <= ZERO && binding.cbNoNumber.isChecked.not()) {
                    this.setError(getString(R.string.error_empty_number))
                }
                validateButton()
            }
        }
        binding.inputNeighborhood.apply {
            this.setValidators(
                CieloTextInputField.Validator(
                    rule = { it.extractedValue.length > TWO },
                    errorMessage = getString(R.string.error_empty_neighborhood),
                    onResult = { isValid, _ ->
                        if (isValid) validateButton()
                    }
                )
            )
            this.validationMode = CieloTextInputField.ValidationMode.FOCUS_CHANGED
        }
        binding.inputCity.apply {
            this.setValidators(
                CieloTextInputField.Validator(
                    rule = { it.extractedValue.length > TWO },
                    errorMessage = getString(R.string.error_empty_city),
                    onResult = { isValid, _ ->
                        if (isValid) validateButton()
                    }
                )
            )
            this.validationMode = CieloTextInputField.ValidationMode.FOCUS_CHANGED
            this.textInputEditText.numbersFilter()
        }
        binding.inputState.apply {
            this.setValidators(
                CieloTextInputField.Validator(
                    rule = { it.extractedValue.length == TWO },
                    errorMessage = getString(R.string.error_empty_state),
                    onResult = { isValid, _ ->
                        if (isValid) validateButton()
                    }
                )
            )
            this.validationMode = CieloTextInputField.ValidationMode.FOCUS_CHANGED
            this.textInputEditText.numbersFilter()
        }
    }

    private fun addObserver() {
        registrationViewModel.addressLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is RegistrationResource.Loading -> onLoadSearching()
                is RegistrationResource.Success -> onAddressReceived(resource.data)
                is RegistrationResource.Error -> onErrorReceived()
                RegistrationResource.Empty -> {}
            }
        }
    }

    private fun onAddressReceived(address: Address) {
        fillFields(address)
        binding.progressLayout.root.gone()
    }

    private fun onLoadSearching() {
        binding.apply {
            progressLayout.root.visible()
            inputCep.unsetError()
            inputAddress.unsetError()
            inputNumber.unsetError()
            inputNeighborhood.unsetError()
            inputCity.unsetError()
            inputState.unsetError()
        }
    }

    private fun onErrorReceived() {
        binding.apply {
            progressLayout.root.gone()
            inputCep.setError(getString(R.string.error_invalid_cep))
            resetFields()
        }
        validateButton()
    }

    private fun fillFields(address: Address? = null) {
        binding.apply {
            inputCep.textInputEditText.setText(registrationViewModel.addressRequest?.zipCode ?: address?.zipCode ?: EMPTY)
            inputAddress.text = registrationViewModel.addressRequest?.streetAddress ?: address?.streetAddress ?: EMPTY
            inputNeighborhood.text = registrationViewModel.addressRequest?.neighborhood ?: address?.neighborhood ?: EMPTY
            inputComplement.text = registrationViewModel.addressRequest?.streetAddress2 ?: address?.streetAddress2 ?: EMPTY
            inputNumber.text = registrationViewModel.addressRequest?.number ?: address?.number ?: EMPTY
            inputNumber.isInputEnabled = cbNoNumber.isChecked.not()
            inputCity.text = registrationViewModel.addressRequest?.city ?: address?.city ?: EMPTY
            inputCity.isInputEnabled = inputCity.textInputEditText.text.isNullOrBlank()
            inputState.text = registrationViewModel.addressRequest?.state ?: address?.state ?: EMPTY
            inputState.isInputEnabled = inputState.textInputEditText.text.isNullOrBlank()
        }
        validateButton()
    }

    private fun setListeners() {
        binding.apply {
            cbNoNumber.setOnCheckedChangeListener { _, isChecked ->
                inputNumber.unsetError()
                if (isChecked) {
                    inputNumber.textInputEditText.text = null
                    inputNumber.isInputEnabled = false
                } else {
                    inputNumber.isInputEnabled = true
                }
                validateButton()
            }
        }

        binding.btSearchCep.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(COURIER_URL)
                )
            )
        }

        binding.btContinue.setOnClickListener {
            if (isAllFieldsValid()) {
                sendAddress()
                NavHostFragment.findNavController(this).navigate(AddressFragmentDirections.actionNavAddressToNavMonthlyIncome())
            }
        }
    }

    private fun sendAddress() {
        binding.apply {
            registrationViewModel.setAddress(
                inputCep.textInputEditText.text.toString(),
                inputAddress.textInputEditText.text.toString(),
                inputComplement.textInputEditText.text.toString(),
                inputNumber.textInputEditText.text.toString(),
                inputNeighborhood.textInputEditText.text.toString(),
                inputCity.textInputEditText.text.toString(),
                inputState.textInputEditText.text.toString(),
            )
        }
    }

    private fun isAllFieldsValid(): Boolean {
        binding.apply {
            return validateFields(
                inputCep.textInputEditText.text.toString(),
                inputAddress.textInputEditText.text.toString(),
                inputNumber.textInputEditText.text.toString(),
                inputNeighborhood.textInputEditText.text.toString(),
                inputCity.textInputEditText.text.toString(),
                inputState.textInputEditText.text.toString()
            )
        }
    }

    private fun validateFields(
        cep: String,
        address: String,
        number: String,
        neighborhood: String,
        city: String,
        state: String
    ): Boolean {
        var isValid = true

        binding.apply {
            if (cep.length < NINE) {
                inputCep.setError(getString(R.string.error_empty_cep))
                isValid = false
            }

            if (address.isBlank()) {
                inputAddress.setError(getString(R.string.error_empty_address))
                isValid = false
            }

            if (inputNumber.isInputEnabled) {
                if (number.isBlank()) {
                    inputNumber.setError(getString(R.string.error_empty_number))
                    isValid = false
                }
            }

            if (neighborhood.isBlank()) {
                inputNeighborhood.setError(getString(R.string.error_empty_neighborhood))
                isValid = false
            }

            if (city.isBlank()) {
                inputCity.setError(getString(R.string.error_empty_city))
                isValid = false
            }

            if (state.isBlank()) {
                inputState.setError(getString(R.string.error_empty_state))
                isValid = false
            }
        }
        return isValid
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(
                title = getString(R.string.title_your_data),
                isCollapsed = false,
                subtitle = getString(R.string.subtitle_whats_your_address)
            )
            navigation?.showBackButton(isShow = true)
            navigation?.onStepChanged(RegistrationStepError.ADDRESS.ordinal)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    private fun resetFields() {
        binding.apply {
            inputAddress.textInputEditText.text = null
            inputNumber.textInputEditText.text = null
            inputNeighborhood.textInputEditText.text = null
            inputCity.textInputEditText.text = null
            inputState.textInputEditText.text = null
            inputComplement.textInputEditText.text = null
            cbNoNumber.isChecked = false
            inputState.isInputEnabled = true
            inputCity.isInputEnabled = true
            inputNumber.isInputEnabled = true
        }
    }

    private fun fieldsHasError(): Boolean {
        binding.apply {
            return inputCep.hasError
                    || inputAddress.hasError
                    || (inputNumber.hasError && cbNoNumber.isChecked.not())
                    || inputNeighborhood.hasError
                    || inputCity.hasError
                    || inputState.hasError
        }
    }

    private fun hasFieldsEmpty(): Boolean {
        binding.apply {
            return inputCep.textInputEditText.text.isNullOrBlank()
                    || inputAddress.textInputEditText.text.isNullOrBlank()
                    || (inputNumber.textInputEditText.text.isNullOrBlank() && cbNoNumber.isChecked.not())
                    || inputNeighborhood.textInputEditText.text.isNullOrBlank()
                    || inputCity.textInputEditText.text.isNullOrBlank()
                    || inputState.textInputEditText.text.isNullOrBlank()
        }
    }

    private fun validateButton() {
        binding.btContinue.isButtonEnabled = fieldsHasError().not() && hasFieldsEmpty().not()
    }

    companion object {
        const val COURIER_URL = "https://buscacepinter.correios.com.br/app/endereco/index.php"
    }
}