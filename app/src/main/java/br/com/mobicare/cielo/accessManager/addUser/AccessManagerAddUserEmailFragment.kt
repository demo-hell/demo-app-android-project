package br.com.mobicare.cielo.accessManager.addUser

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.utils.AccessManagerConstants
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.captureEmailDomain
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserEmailBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection
import java.util.*
import kotlin.concurrent.timerTask


class AccessManagerAddUserEmailFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAddUserEmailContract.View {
    private var navigation: CieloNavigation? = null
    private var email: String = EMPTY
    private var cpfIsNull: String? = null
    private val isForeignArg: Boolean by lazy {
        arguments?.getBoolean(AccessManagerConstants.IS_FOREIGN_ARGS, false) ?: false
    }
    private val nationalityCodeArg: String by lazy {
        arguments?.getString(AccessManagerConstants.NATIONALITY_CODE_ARGS) ?: EMPTY
    }
    private val cpfArg: String by lazy {
        arguments?.getString(AccessManagerConstants.CPF_ARGS) ?: EMPTY
    }
    private val roleArg: String by lazy {
        arguments?.getString(AccessManagerConstants.ROLE_ARGS) ?: EMPTY
    }

    private var _binding: FragmentAccessManagerAddUserEmailBinding? = null
    private val binding get() = _binding
    private val presenter: AccessManagerAddUserEmailPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccessManagerAddUserEmailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }
        listenerEmail()
        email = binding?.emailtv?.text?.trim()?.toLowerCasePTBR().orEmpty()
        var typingTimer = Timer()
        val time: Long = 1000
        binding?.emailtv?.doAfterTextChanged {
            typingTimer.cancel()
            typingTimer = Timer()
            typingTimer.schedule(timerTask {
                requireActivity().runOnUiThread {
                        cpfIsNull = if (cpfArg.isEmpty()){
                            null
                        }else{
                            cpfArg
                        }
                    presenter.validateEmail(cpfIsNull, binding?.emailtv?.text.toString(), isForeignArg)
                }
            }, time)
        }
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }
        binding?.nextButtonAddUser?.setOnClickListener {
            findNavController().navigate(
                AccessManagerAddUserEmailFragmentDirections.
                actionAccessManagerAddUserEmailFragmentToAccessManagerAddUserEstablishmentFragment(
                    isForeignArg, nationalityCodeArg, cpfArg, binding?.emailtv?.text.toString(), roleArg)
            )
        }

    }

    private fun listenerEmail() {
        binding?.emailtv?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        binding?.emailtv?.requestFocus()
        requireActivity().showSoftKeyboard(binding?.emailtv)
        binding?.emailtv?.hint = getString(R.string.id_onboarding_update_email_hint)
    }

    private fun setupError(isSuccess: Boolean, error: TextView?, text: String) {
        if (isSuccess) {
            error?.gone()
        } else {
            if ((text == EMPTY).not()) {
                error?.text = text
            } else {
                error?.text = getString(R.string.invalid_email)
            }
            error?.visible()
        }
        enableButton(isSuccess)
    }

    private fun enableButton(validate: Boolean) {
        if (validate) {
            binding?.nextButtonAddUser?.apply {
                setBackgroundResource(R.drawable.blue_button)
                isClickable = true
            }
        } else {
            binding?.nextButtonAddUser?.apply {
                setBackgroundResource(R.drawable.blue_button_disabled)
                isClickable = false
            }
        }
        binding?.nextButtonAddUser?.isEnabled = validate
    }

    override fun showSuccess(result: Any) {
        doWhenResumed(
            action = {
                binding?.emailtv?.text.let { email ->
                    val validate = email?.trim().isNullOrEmpty().not()
                            && ValidationUtils.isEmail(email.toString())
                    setupError(validate, binding?.emailtvValueErrorLabel, EMPTY)
                }
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                Text.OTP
            )
        )
            doWhenResumed(
                action = {
                    setupError(
                        false,
                        binding?.emailtvValueErrorLabel,
                        processErrorMessage(error)
                    )
                },
                errorCallback = { baseLogout() }
            )
    }

    private fun processErrorMessage(error: ErrorMessage?): String {
        var errorMessageAux = getString(R.string.invalid_email)
        if (error?.errorCode == ERROR_CODE_MAIL_DOMAIN_NOT_ALLOWED){
            val emailDomain = captureEmailDomain(binding?.emailtv?.text.toString())

            if (emailDomain.isNotEmpty())
                errorMessageAux = getString(
                    R.string.access_manager_add_user_error_email_restricted,
                    emailDomain
                )
        } else errorMessageAux = messageError(error, requireActivity())

        return errorMessageAux
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}