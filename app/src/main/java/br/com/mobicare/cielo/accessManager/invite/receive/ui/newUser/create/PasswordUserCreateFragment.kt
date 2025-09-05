package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.inputtext.CieloTextInputView
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceivePresenter
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.databinding.FragmentPasswordCreateBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.errorNotBooting
import com.akamai.botman.CYFMonitor
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PasswordUserCreateFragment : InviteReceiveBaseFragment() {

    private var _binding: FragmentPasswordCreateBinding? = null
    private val binding get() = _binding
    private var isPasswordValid = false
    private var isConfirmationValid = false

    private val presenter: InviteReceivePresenter by inject {
        parametersOf(this)
    }

    val args: PasswordUserCreateFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMonitor()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPasswordCreateBinding.inflate( inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            btBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            setupPasswordTextInput()
            setupConfirmationTextInput()
            setupNextButton()
        }
    }

    private fun setupMonitor() {
        CYFMonitor.initialize(requireActivity().application, BuildConfig.HOST_API)
    }

    private fun setupPasswordTextInput() {
        binding?.apply {
            tiPassword.setOnTextViewFocusChanged { _, hasFocus ->
                if (hasFocus.not()) {
                    validatePasswordAndConfirmation(tiPassword.getText(), tiConfirmation.getText())
                    setupVisibility()
                }
            }
            tiPassword.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (tvTypedValueErrorLabel.isVisible or btnNext.isEnabled) {
                        validatePasswordAndConfirmation(
                            s?.toString().orEmpty(),
                            tiConfirmation.getText()
                        )
                        setupVisibility()
                    }
                }

            })
        }
    }

    private fun validatePasswordAndConfirmation(password: String, confirmation: String) {
        isPasswordValid = ValidationUtils.isValidPasswordLogin(password)
        isConfirmationValid = password == confirmation
    }

    private fun setupConfirmationTextInput() {
        binding?.apply {
            tiConfirmation.setOnTextChangeListener(object : CieloTextInputView.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    isConfirmationValid = s?.toString().equals(tiPassword.getText())
                    setupVisibility()
                }
            })
        }
    }

    private fun setupNextButton() {
        binding?.apply {
            btnNext.setOnClickListener {
                val password = tiPassword.getText().ifBlank { return@setOnClickListener }
                presenter.createUser(password, args.invitetokenargs, args.invitedetailsargs)
            }
        }
    }

    private fun setupVisibility() {
        binding?.apply {
            tvTypedValueErrorLabel.visibility = if (isPasswordValid) View.GONE else View.VISIBLE
            btnNext.isEnabled = isPasswordValid && isConfirmationValid
        }
    }

    override fun showLoading() {
        doWhenResumed {
            navigation?.showLoading(true, R.string.access_manager_creating_user)
        }
    }

    override fun onErrorNotBooting() {
        requireActivity().errorNotBooting(
            onAction = {
                setupMonitor()
            },
            message = getString(R.string.error_not_booting_forgot_password_message)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }
}