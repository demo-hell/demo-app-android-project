package br.com.mobicare.cielo.login.firstAccess.presentation.ui.create

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloNavLinksBottomSheet
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.util.ELEVEN
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.AT_SIGN
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.FirstAccessShowTerms
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.spannable.SpannableLink
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.databinding.FragmentFirstAccessCreateBinding
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.GA_AUTO_REGISTER_PATH
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.GA_FIRST_ACCESS_CLIENT_PATH
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.INCORRECT_CPF
import br.com.mobicare.cielo.login.firstAccess.analytics.FirstAccessAnalytics.Companion.INCORRECT_EMAIL
import org.koin.android.ext.android.inject

class FirstAccessCreateFragment : BaseFragment(), CieloNavigationListener {
    private var _binding: FragmentFirstAccessCreateBinding? = null
    private val binding get() = requireNotNull(_binding)
    private var navigation: CieloNavigation? = null

    private val viewModel: FirstAccessCreateViewModel by viewModels()
    private val analytics: FirstAccessAnalytics by inject()
    private val className: Class<Any> = this.javaClass

    private var isCpfValid = false
    private var isEcValid = false
    private var isEmailValid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstAccessCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        setupView()
    }

    override fun onResume() {
        super.onResume()

        with(analytics) {
            logScreenView(className, GA_AUTO_REGISTER_PATH)
            logScreenView(className, GA_FIRST_ACCESS_CLIENT_PATH)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }

    private fun setupListeners() {
        binding.apply {
            ibBack.setOnClickListener {
                activity?.finish()
            }

            btnContinue.setOnClickListener {
                if (isFieldsValid()) {
                    viewModel.keepUserInfo(
                        ec = tifNumberEc.textInputEditText.text.toString(),
                        cpf = tifCpf.textInputEditText.text.toString(),
                        email = tifEmail.textInputEditText.text.toString()
                    )

                    goToCreatePasswordScreen()
                }
            }
        }
    }

    private fun goToCreatePasswordScreen() {
        findNavController().navigate(
            FirstAccessCreateFragmentDirections
                .actionFirstAccessCreateFragmentToFirstAccessCreatePasswordFragment(
                    viewModel.getEc(),
                    Utils.unmask(viewModel.getCpf()),
                    viewModel.getEmail()
                )
        )
    }

    private fun setupView() {
        configureFieldNumberEc()
        configureFieldCpf()
        configureFieldEmail()

        binding.apply {
            tvSubtitle.fromHtml(R.string.first_access_create_access_subtitle)
            tfTermsReadConditions.setText(textOfTermsFormat(), TextView.BufferType.SPANNABLE)
            tfTermsReadConditions.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun textOfTermsFormat(): SpannableStringBuilder {
        return getString(R.string.first_access_create_access_accept_conditions)
            .addSpannable(
                TextAppearanceSpan(
                    requireContext(),
                    R.style.regular_montserrat_14_neutral_500
                )
            ).append(ONE_SPACE)
            .append(
                getString(R.string.first_access_create_access_terms_and_conditions)
                    .addSpannable(
                        TextAppearanceSpan(
                            requireContext(),
                            R.style.semi_bold_montserrat_14_accent_800
                        ),
                        SpannableLink({ showTerms() }, isUnderline = false)
                    )
            )
    }

    private fun showTerms() {
        val links = listOf(
            CieloNavLinksBottomSheet.Link(
                label = getString(R.string.first_access_create_access_terms_contract),
                url = FirstAccessShowTerms.CONTRACT
            ),
            CieloNavLinksBottomSheet.Link(
                label = getString(R.string.first_access_create_access_terms_privacy),
                url = FirstAccessShowTerms.PRIVACY
            ),
            CieloNavLinksBottomSheet.Link(
                label = getString(R.string.first_access_create_access_terms_conditions),
                url = FirstAccessShowTerms.CONDITIONS
            )
        )

        CieloNavLinksBottomSheet.create(
            title = getString(R.string.p2m_title_terms),
            links = links
        ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun isFieldsValid(): Boolean {
        binding.apply {
            return validateFields(
                tifNumberEc.textInputEditText.text.toString(),
                tifCpf.textInputEditText.text.toString(),
                tifEmail.textInputEditText.text.toString()
            )
        }
    }

    private fun validateFields(
        numberEc: String,
        cpf: String,
        email: String
    ) = validateFieldNumberEc(numberEc) && validateFieldCpf(cpf) && validateFieldEmail(email)

    private fun validateButton() {
        binding.btnContinue.isButtonEnabled = isEcValid && isCpfValid && isEmailValid
    }

    private fun validateFieldNumberEc(numberEc: String): Boolean {
        return if (numberEc.isEmpty()) {
            binding.tifNumberEc.setError(getString(R.string.error_empty_number))
            false
        } else {
            true
        }
    }

    private fun validateFieldCpf(cpf: String): Boolean {
        return if (cpf.isEmpty() || ValidationUtils.isCPF(cpf).not()) {
            binding.tifCpf.setError(getString(R.string.first_access_create_access_error_cpf))
            false
        } else {
            true
        }
    }

    private fun validateFieldEmail(email: String): Boolean {
        return if (email.isEmpty() || ValidationUtils.isEmail(email).not()) {
            binding.tifEmail.setError(getString(R.string.first_access_create_access_error_email))
            false
        } else {
            true
        }
    }

    private fun configureFieldNumberEc() {
        binding.tifNumberEc.apply {
            post {
                if (viewModel.getEc().isNotEmpty()) text = viewModel.getEc()
            }

            setTextChangedListener {
                isEcValid = it.extractedValue.length >= FIVE

                validateButton()
            }
        }
    }

    private fun configureFieldCpf() {
        binding.tifCpf.apply {
            post {
                if (viewModel.getCpf().isNotEmpty()) text = viewModel.getCpf()
            }

            setMask(CieloTextInputField.MaskFormat.CPF)

            setTextChangedListener {
                if (it.extractedValue.length < ELEVEN) {
                    isCpfValid = false
                    unsetError()
                    validateButton()
                    return@setTextChangedListener
                }

                if (ValidationUtils.isCPF(it.extractedValue)) {
                    isCpfValid = true
                    unsetError()
                } else {
                    isCpfValid = false
                    analytics.logDisplayContent(GA_AUTO_REGISTER_PATH, INCORRECT_CPF)
                    setError(getString(R.string.first_access_create_access_error_cpf))
                }

                validateButton()
            }
        }
    }

    private fun configureFieldEmail() {
        binding.tifEmail.apply {
            post {
                if (viewModel.getEmail().isNotEmpty()) text = viewModel.getEmail()
            }

            setTextChangedListener {
                if (it.extractedValue.contains(AT_SIGN).not()) {
                    isEmailValid = false
                    unsetError()
                    validateButton()
                    return@setTextChangedListener
                }

                if (ValidationUtils.isEmail(it.extractedValue)) {
                    isEmailValid = true
                    unsetError()
                } else {
                    isEmailValid = false
                    analytics.logDisplayContent(GA_AUTO_REGISTER_PATH, INCORRECT_EMAIL)
                    setError(getString(R.string.first_access_create_access_error_email))
                }

                validateButton()
            }
        }
    }
}