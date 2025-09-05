package br.com.mobicare.cielo.forgotMyPassword.presentation.insertInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.BiometricTokenNavigationFlowActivity
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_IS_LOGIN_FLOW
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_SELFIE_SDK
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_TOKEN_SELFIE_SCREEN
import br.com.mobicare.cielo.biometricToken.constants.BiometricTokenConstants.ARG_BIOMETRIC_USERNAME
import br.com.mobicare.cielo.commons.constants.AT_SIGN
import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.FACE_ID
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.containsOnlyNumbers
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentForgotMyPasswordInsertInfoBinding
import br.com.mobicare.cielo.extensions.documentWithoutMask
import br.com.mobicare.cielo.extensions.errorNotBooting
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.CHANGE_PASSWORD
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.INCORRECT_DATA
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_NAME_FORGOT_PASSWORD
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_NAME_LOGIN
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_VIEW_FORGOT
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_VIEW_FORGOT_SELFIE_GENERIC_ERROR
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.SCREEN_VIEW_FORGOT_SELFIE_TIPS
import br.com.mobicare.cielo.forgotMyPassword.analytics.ForgotMyPasswordGA4.Companion.WARNING
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState.Error
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState.ErrorAkamai
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState.Loading
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState.Success
import com.akamai.botman.CYFMonitor
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgotMyPasswordInsertInfoFragment : BaseFragment(), CieloNavigationListener {
    private var binding: FragmentForgotMyPasswordInsertInfoBinding? = null
    private val mViewModel: ForgotMyPasswordInsertInfoViewModel by viewModel()
    private var navigation: CieloNavigation? = null
    private val analyticsGA4: ForgotMyPasswordGA4 by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMonitor()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentForgotMyPasswordInsertInfoBinding.inflate(
            inflater,
            container,
            false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        observe()
    }

    override fun onResume() {
        super.onResume()
        analyticsGA4.logScreenView(SCREEN_VIEW_FORGOT)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onButtonClicked(labelButton: String) {
        requireActivity().hideSoftKeyboard()
        analyticsGA4.logClick(screenName = SCREEN_NAME_LOGIN, contentName = CHANGE_PASSWORD)

        val value = binding?.itUser?.text?.trim() ?: EMPTY
        mViewModel.sendRequestRecoveryPassword(if (valueIsCPF(value)) value.documentWithoutMask() else value)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.txt_label_button_forgot_my_password_insert_info))
            navigation?.enableButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.itUser?.apply {
            setCpfEcEmailMask {
                if (it.documentWithoutMask().containsOnlyNumbers() && it.length >= ELEVEN) {
                    validateCpf(it)
                    return@setCpfEcEmailMask
                }

                if (it.contains(AT_SIGN)) {
                    validateEmail(it)
                    return@setCpfEcEmailMask
                }

                navigation?.enableButton(false)
                binding?.itUser?.unsetError()
            }
        }
    }

    private fun observe() {
        mViewModel.forgotPasswordUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is Loading -> navigation?.showLoading(true)
                is Success -> onSuccessForgotMyPassword(uiState.data)
                is Error -> handleErrorResponse()
                is ErrorAkamai -> {
                    navigation?.showLoading(false)
                    requireActivity().errorNotBooting(
                        onAction = {
                            setupMonitor()
                        },
                        message = getString(R.string.txt_message_error_not_booting_forgot_my_password)
                    )
                }
            }
        }
    }

    private fun handleErrorResponse() {
        analyticsGA4.logDisplayContent(
            screenName = SCREEN_NAME_FORGOT_PASSWORD,
            description = INCORRECT_DATA,
            contentType = WARNING
        )

        showResponse(
            image = R.drawable.ic_07,
            title = R.string.txt_message_error_invalid_data_title,
            message = R.string.txt_message_error_invalid_data_message,
            buttonMessage = R.string.try_again
        ) {
            binding?.apply {
                showOriginalContentScreen()
            }
        }
    }

    private fun showOriginalContentScreen() {
        binding?.apply {
            includeResponse.root.gone()
            groupFields.visible()
            navigation?.showButton(true)
            navigation?.showToolbar(true)
        }
    }

    private fun handleSuccessResponse() {
        analyticsGA4.logScreenView(ForgotMyPasswordGA4.SCREEN_VIEW_FORGOT_CONFIRM_EMAIL)
        showResponse(
            image = R.drawable.img_21_email,
            title = R.string.txt_title_success_forgot_my_password,
            message = R.string.txt_message_success_forgot_my_password,
            buttonMessage = R.string.txt_label_button_success_forgot_my_password
        ) {
            requireActivity().finish()
        }
    }

    private fun showResponse(
        @DrawableRes image: Int,
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes buttonMessage: Int,
        buttonAction : () -> Unit
    ) {
        binding?.includeResponse?.apply {
            requireActivity().hideSoftKeyboard()

            navigation?.showLoading(false)
            navigation?.showToolbar(false)
            navigation?.showButton(false)

            binding?.groupFields.gone()
            root.visible()

            ivImage.setImageResource(image)
            tvTitle.text = getString(title)
            tvMessage.text = getString(message)
            button.text = getString(buttonMessage)

            button.setOnClickListener {
                buttonAction()
            }
        }
    }

    private fun onSuccessForgotMyPassword(data: ForgotMyPassword?){
        navigation?.showLoading(false)
        data?.let {
            if (it.nextStep == FACE_ID) {
                mViewModel.deleteUserInformation()
                requireActivity().startActivity<BiometricTokenNavigationFlowActivity>(
                    ARG_BIOMETRIC_TOKEN_SELFIE_SCREEN to true,
                    ARG_BIOMETRIC_SELFIE_SDK to it.faceIdPartner,
                    ARG_BIOMETRIC_USERNAME to it.userName,
                    ARG_BIOMETRIC_SCREEN_NAME to SCREEN_VIEW_FORGOT_SELFIE_TIPS,
                    ARG_BIOMETRIC_SCREEN_NAME_EXCEPTION to SCREEN_NAME_FORGOT_PASSWORD,
                    ARG_BIOMETRIC_SCREEN_NAME_GENERIC_ERROR to SCREEN_VIEW_FORGOT_SELFIE_GENERIC_ERROR,
                    ARG_BIOMETRIC_IS_LOGIN_FLOW to true
                )
            } else {
                handleSuccessResponse()
            }
        }
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(requireActivity().application, BuildConfig.HOST_API)
    }

    private fun valueIsCPF(value: String): Boolean {
        return value.documentWithoutMask().containsOnlyNumbers()
    }

    private fun validateCpf(cpf: String) {
       if (ValidationUtils.isCPF(cpf)) {
           handleInputStatus(true)
       } else {
           handleInputStatus(false, R.string.first_access_create_access_error_cpf)
       }
   }

    private fun validateEmail(email: String) {
        if (ValidationUtils.isEmail(email)) {
            handleInputStatus(true)
        } else {
            handleInputStatus(false, R.string.input_error_email)
        }
    }

    private fun handleInputStatus(enableButton: Boolean, @StringRes errorMessage: Int? = null) {
        errorMessage?.let { message ->
            navigation?.enableButton(enableButton)
            binding?.itUser?.setError(getString(message))
        } ?: run {
            navigation?.enableButton(enableButton)
            binding?.itUser?.unsetError()
        }
    }
}