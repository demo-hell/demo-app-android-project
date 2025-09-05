package br.com.mobicare.cielo.login.firstAccess.presentation.ui.createPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.validator.model.Accessibility
import br.com.cielo.libflue.validator.model.Validator
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.SIX
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.TimeUtils
import br.com.mobicare.cielo.commons.utils.hasDozensSequential
import br.com.mobicare.cielo.commons.utils.hasMirroredDigits
import br.com.mobicare.cielo.commons.utils.hasRepeatedDigitsOrDozens
import br.com.mobicare.cielo.commons.utils.isDrawingKeyboard
import br.com.mobicare.cielo.databinding.FragmentFirstAccessCreatePasswordBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.errorNotBooting
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.GA_AUTO_REGISTER_PASSWORD_PATH
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.GA_AUTO_REGISTER_PATH
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.GA_AUTO_REGISTER_SUCCESS_PATH
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorGeneric
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorMessage
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessErrorNotBooting
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.FirstAccessSuccess
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.HideLoading
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState.ShowLoading
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.akamai.botman.CYFMonitor
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FirstAccessCreatePasswordFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentFirstAccessCreatePasswordBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var navigation: CieloNavigation? = null

    private val viewModel: FirstAccessCreatePasswordViewModel by viewModel()
    private val analytics: FirstAccessAnalytics by inject()
    private val args: FirstAccessCreatePasswordFragmentArgs by navArgs()

    private val userCpf by lazy { args.cpfArgs }

    private val isInvalidPassword: Boolean
        get() = binding.passwordValidator.isAnyConditionInvalid()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstAccessCreatePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(this.javaClass, GA_AUTO_REGISTER_PASSWORD_PATH)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMonitor()
        setupNavigation()
        setPasswordValidator()
        setupView()
        setListeners()
        setupObservers()
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(requireActivity().application, BuildConfig.HOST_API)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
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
            validators = listOf(
                Validator(
                    text = getString(R.string.password_requirement_digits),
                    invalidCondition = { text.contains(userCpf.take(SIX)) }
                ),
                Validator(
                    text = getString(R.string.password_requirement_repeated_digits_or_dozens),
                    auxiliaryText = getString(R.string.password_requirement_repeated_digits_or_dozens_sample),
                    invalidCondition = { hasRepeatedDigitsOrDozens(text)}
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

    private fun setupView() {
        configureFieldPasswordConfirm()
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
        val shouldEnableButton = binding.passwordValidator.isAnyConditionInvalid().not() && binding.tifPassword.text.length == SIX && binding.tifPasswordConfirm.text.length == SIX && binding.tifPasswordConfirm.hasError.not()

        binding.btnContinue.apply {
            isButtonEnabled = shouldEnableButton
            contentDescription = if (shouldEnableButton.not()) getString(R.string.first_access_create_password_button_disabled) else EMPTY
        }

    }

    private fun setListeners() {
        binding.apply {
            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnContinue.setOnClickListener {
                if (isFieldsValid()) {
                    navigation?.hideKeyboard()

                    viewModel.sendRequest(
                        numberEc = args.numberEcArgs,
                        cpf = userCpf,
                        email = args.emailArgs,
                        password = tifPassword.textInputEditText.text.toString(),
                        passwordConfirmation = tifPasswordConfirm.textInputEditText.text.toString()
                    )
                }
            }

            tifPassword.setTextChangedListener {
                binding.tifPassword.apply {
                    val inputValue = it.extractedValue

                    if (inputValue.length == SIX) {
                        updatePasswordValidators(inputValue)

                        if (isInvalidPassword) {
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

    private fun setupObservers() {
        viewModel.firstAccessLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is ShowLoading -> onShowLoading()
                is HideLoading -> onHideLoading()
                is FirstAccessSuccess -> successBottomSheet(uiState.firstAccessResult)
                is FirstAccessErrorMessage -> { errorBottomSheet(uiState.message, uiState.code) }
                is FirstAccessErrorNotBooting -> errorNotBooting()
                is FirstAccessErrorGeneric -> genericErrorBottomSheet()
            }
        }
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun successBottomSheet(firstAccessResponse: FirstAccessResponse) {
        doWhenResumed {
            analytics.logScreenView(this.javaClass, GA_AUTO_REGISTER_SUCCESS_PATH)

            navigation?.showCustomHandlerView(
                contentImage = R.drawable.ic_img_email,
                title = getString(R.string.first_access_success_title),
                titleAlignment = View.TEXT_ALIGNMENT_TEXT_START,
                message = getString(
                    R.string.first_access_success_subTitle, firstAccessResponse.email,
                    TimeUtils.convertMinutesToHours(firstAccessResponse.tokenExpirationInMinutes)
                ),
                isShowButtonClose = true,
                callbackClose = {
                    activity?.finishAndRemoveTask()
                },
                isShowFirstButton = true,
                isShowSecondButton = false,
                isShowButtonBack = false,
                labelFirstButton = getString(R.string.text_user_choose_register_button_label),
                callbackFirstButton = {
                    baseLogout()
                })
        }
    }

    private fun errorBottomSheet(message: String?, code: String) {
        val textMessage = message ?: getString(R.string.generic_error_title)

        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.title_alert_create_access),
                    message = textMessage,
                    bt2Title = getString(R.string.entendi),
                    isCancelable = false,
                    titleBlack = false
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )

        analytics.logDisplayContent(screenName = GA_AUTO_REGISTER_PATH, code = code)
    }

    private fun errorNotBooting() {
        requireActivity().errorNotBooting(
            onAction = {
                setupMonitor()
            },
            message = getString(R.string.error_not_booting_first_access_message)
        )
    }

    private fun genericErrorBottomSheet() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_03,
                    title = getString(R.string.first_access_create_password_bottom_sheet_generic_title),
                    message = getString(R.string.first_access_create_password_bottom_sheet_generic_subtitle),
                    bt2Title = getString(R.string.label_tryagain),
                    isCancelable = false,
                    titleBlack = true
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}