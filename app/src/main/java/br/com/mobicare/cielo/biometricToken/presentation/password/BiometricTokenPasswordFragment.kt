package br.com.mobicare.cielo.biometricToken.presentation.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.validator.model.Accessibility
import br.com.cielo.libflue.validator.model.Validator
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.hasDozensSequential
import br.com.mobicare.cielo.commons.utils.hasMirroredDigits
import br.com.mobicare.cielo.commons.utils.hasRepeatedDigitsOrDozens
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.isDrawingKeyboard
import br.com.mobicare.cielo.databinding.FragmentBiometricTokenPasswordBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.PASSWORD
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_NAME_FORGOT_PASSWORD
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_VIEW_FORGOT_SELFIE_SUCCESS
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.WARNING
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class BiometricTokenPasswordFragment : BaseFragment(), CieloNavigationListener,
    BiometricTokenPasswordContract.View {

    private var _binding: FragmentBiometricTokenPasswordBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val presenter: BiometricTokenPasswordPresenter by inject {
        parametersOf(this)
    }
    private var navigation: CieloNavigation? = null
    private val args: BiometricTokenPasswordFragmentArgs by navArgs()
    private val userName: String? by lazy {
        args.argbiometricusername
    }
    private val faceIdToken: String? by lazy {
       args.argbiometrictoken
    }
    private val analyticsGA4: ForgotMyPasswordGA4 by inject()

    private val isInvalidPassword: Boolean
        get() = binding.passwordValidator.isAnyConditionInvalid()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentBiometricTokenPasswordBinding.inflate(
        inflater,
        container,
        false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        initView()
        analyticsGA4.logScreenView(ForgotMyPasswordGA4.SCREEN_VIEW_FORGOT_REDEFINITION_2)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun initView() {
        setPasswordValidator()
        configureFieldPasswordConfirm()
        setListeners()
    }

    private fun setPasswordValidator() {
        binding.passwordValidator.apply {
            validatorTitle = getString(R.string.password_requirement_title)
            accessibility = Accessibility(
                invalidMainTextDescription = getString(R.string.invalid_password_accessibility_base_text),
                validMainTextDescription = getString(R.string.valid_password_accessibility_base_text)
            )

            updatePasswordValidators(binding.tifPassword.text)
        }
    }

    private fun updatePasswordValidators(text: String) {
        binding.passwordValidator.apply {
            val usernameIsCpf = ValidationUtils.isCPF(userName)

            validators = listOf(
                Validator(
                    text = getString(R.string.password_requirement_digits),
                    invalidCondition = { if (usernameIsCpf) text.contains(userName.orEmpty().take(SIX)) else false }
                ),
                Validator(
                    text = getString(R.string.password_requirement_repeated_digits_or_dozens),
                    auxiliaryText = getString(R.string.password_requirement_repeated_digits_or_dozens_sample),
                    invalidCondition = { hasRepeatedDigitsOrDozens(text) }
                ),
                Validator(
                    text = getString(R.string.password_requirement_dozens_sequential),
                    auxiliaryText = getString(R.string.password_requirement_dozens_sequential_sample),
                    invalidCondition = { hasDozensSequential(text) }
                ),
                Validator(
                    text = getString(R.string.password_requirement_mirrored_digits),
                    auxiliaryText = getString(R.string.password_requirement_mirrored_digits_sample),
                    invalidCondition = { hasMirroredDigits(text) }
                ),
                Validator(
                    text = getString(R.string.password_requirement_drawing_keyboard),
                    auxiliaryText = getString(R.string.password_requirement_drawing_keyboard_sample),
                    invalidCondition = { isDrawingKeyboard(text) }
                )
            )
        }
    }

    private fun setListeners() {
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnContinue.setOnClickListener {
                if (isFieldsValid()) {
                    requireActivity().hideSoftKeyboard()

                    navigation?.showLoading(true)
                    val password = tifPassword.text.ifBlank { return@setOnClickListener }
                    presenter.resetPassword(userName.orEmpty(), faceIdToken.orEmpty(), password)
                }
            }

            tifPassword.setTextChangedListener {
                binding.tifPassword.apply {
                    val inputValue = it.extractedValue

                    if (inputValue.length == SIX) {
                        updatePasswordValidators(inputValue)

                        if (isInvalidPassword) {
                            analyticsGA4.logDisplayContent(
                                screenName = SCREEN_NAME_FORGOT_PASSWORD,
                                description = PASSWORD,
                                contentType = WARNING
                            )

                            setError(getString(R.string.first_access_create_access_error_password))
                            tifPasswordConfirm.isInputEnabled = false
                        } else {
                            tifPasswordConfirm.isInputEnabled = true
                            unsetError()
                        }
                    } else {
                        binding.passwordValidator.clearValidatorErrors()
                        unsetError()
                        tifPasswordConfirm.isInputEnabled = false
                    }

                    validateButtonContinue()
                }
            }
        }
    }

    private fun configureFieldPasswordConfirm() {
        binding.tifPasswordConfirm.apply {
            setValidators(
                CieloTextInputField.Validator(
                    rule = { binding.tifPassword.textInputEditText.text.toString() == it.extractedValue },
                    errorMessage = getString(R.string.first_access_create_access_error_confirm_password),
                    onResult = { isValid, _ ->
                        if (isValid)
                            unsetError()
                        else
                            setError(getString(R.string.first_access_create_access_error_confirm_password))

                        validateButtonContinue()
                    }
                )
            )
            validationMode = CieloTextInputField.ValidationMode.TEXT_CHANGED
        }
    }

    private fun validateButtonContinue() {
        val inputPasswordLength = binding.tifPassword.text.length
        val inputPasswordConfirmLength = binding.tifPasswordConfirm.text.length

        val shouldEnableButton = binding.passwordValidator.isAnyConditionInvalid().not() && inputPasswordLength == SIX && inputPasswordConfirmLength == SIX && binding.tifPasswordConfirm.hasError.not()

        binding.btnContinue.apply {
            isButtonEnabled = shouldEnableButton
            contentDescription = if (shouldEnableButton.not()) getString(R.string.first_access_create_password_button_disabled) else EMPTY
        }
    }

    private fun isFieldsValid(): Boolean {
        binding.apply {
            return validateFields(
                tifPassword.textInputEditText.text.toString(),
                tifPasswordConfirm.textInputEditText.text.toString()
            )
        }
    }

    private fun validateFields(
        password: String,
        passwordConfirm: String
    ): Boolean {
        var isValid = true

        binding.apply {
            if (password.isEmpty()) {
                tifPassword.setError(getString(R.string.first_access_create_access_error_password))
                isValid = false
            }
            if (passwordConfirm.isEmpty()) {
                tifPasswordConfirm.setError(getString(R.string.first_access_create_access_error_confirm_password_required))
                isValid = false
            } else if (password != passwordConfirm) {
                tifPasswordConfirm.setError(getString(R.string.first_access_create_access_error_confirm_password))
                isValid = false
            }
        }
        return isValid
    }

    private fun handleResponse(
        @DrawableRes image: Int,
        title: String,
        message: String,
        buttonText: String,
        onButtonClicked: () -> Unit
    ) {
        navigation?.showLoading(false)

        binding.includeResponse.apply {
            handleResponseLayout(showResponseLayout = true)

            ivImage.setImageResource(image)
            tvTitle.text = title
            tvMessage.text = message
            button.text = buttonText

            button.setOnClickListener {
                onButtonClicked()
            }
        }
    }

    override fun changePasswordSuccess() {
        analyticsGA4.logScreenView(SCREEN_VIEW_FORGOT_SELFIE_SUCCESS)

        handleResponse(
            R.drawable.img_14_estrelas,
            getString(R.string.biometric_token_change_password_success_title),
            getString(R.string.biometric_token_change_password_success_message),
            getString(R.string.text_user_choose_register_button_label)
        ) {
            baseLogout()
        }
    }

    override fun changePasswordError(error: ErrorMessage?) {
        handleResponse(
            R.drawable.img_10_erro,
            error?.title.orEmpty(),
            error?.message.orEmpty(),
            getString(R.string.txt_btn_error)
        ) {
            handleResponseLayout(showResponseLayout = false)
        }

       logError(error)
    }

    private fun logError(error: ErrorMessage?) {
        analyticsGA4.logException(
            screenName = SCREEN_NAME_FORGOT_PASSWORD,
            description = if (error?.errorMessage.isNullOrEmpty()) error?.errorCode.orEmpty() else error?.errorMessage.orEmpty(),
            code = error?.code.orEmpty()
        )
    }

    private fun handleResponseLayout(showResponseLayout: Boolean) {
        binding.apply {
            groupFields.visible(showResponseLayout.not())
            includeResponse.root.visible(showResponseLayout)
        }
    }
}