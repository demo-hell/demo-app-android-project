package br.com.mobicare.cielo.accessManager.addUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.utils.AccessManagerConstants
import br.com.mobicare.cielo.centralDeAjuda.search.HelpCenterSearchActivity
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.helpers.EditTextHelper
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.CPF_LENGTH
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserCpfBinding
import br.com.mobicare.cielo.extensions.clearCNPJMask
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*
import kotlin.concurrent.timerTask


class AccessManagerAddUserCpfFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAddUserCpfContract.View {
    private var navigation: CieloNavigation? = null

    private val isForeignArg: Boolean by lazy {
        arguments?.getBoolean(AccessManagerConstants.IS_FOREIGN_ARGS, false) ?: false
    }
    private val nationalityCodeArg: String by lazy {
        arguments?.getString(AccessManagerConstants.NATIONALITY_CODE_ARGS) ?: EMPTY
    }
    private val roleArg: String by lazy {
        arguments?.getString(AccessManagerConstants.ROLE_ARGS) ?: EMPTY
    }

    var cpf: String = EMPTY
    private var _binding: FragmentAccessManagerAddUserCpfBinding? = null
    private val binding get() = _binding
    private val presenter: AccessManagerAddUserCpfPresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccessManagerAddUserCpfBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        updateView()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
    }

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                findNavController().popBackStack(R.id.accessManagerHomeFragment, false)
            }

            cpftv.apply {
                hint = getString(R.string.cpf_hint)
                EditTextHelper.cpfField(cpftv)
                requestFocus()
                setBackgroundResource(R.drawable.bg_shape_rounded_unselected_blue_textview)
                requireActivity().showSoftKeyboard(cpftv)
                var typingTimer = Timer()
                doAfterTextChanged { _ ->
                    typingTimer.cancel()
                    typingTimer = Timer()
                    typingTimer.schedule(timerTask {
                        requireActivity().runOnUiThread {
                            text?.let {
                                if (it.length >= CPF_LENGTH) {
                                    presenter.validateCpf(text.toString())
                                }
                            }
                        }
                    }, HelpCenterSearchActivity.FINISH_TYPING_INTERVAL)
                }
            }

            cbWithoutCpf.setOnClickListener {

                if (cbWithoutCpf.isChecked) {
                    cpftv.setBackgroundResource(R.color.box_gray)
                    cpftv.contentDescription =
                        getString(R.string.access_manager_cpf_foreign_description_checked)
                    clearCpf()
                    cpftv.isEnabled = false
                    enableButton(true)
                } else {
                    cpftv.setBackgroundResource(R.drawable.bg_flui_rounded_border)
                    cpftv.contentDescription =
                        getString(R.string.access_manager_cpf_foreign_description_unchecked)
                    cpftv.isEnabled = true
                    enableButton(false)
                }
            }

            nextButtonAddUser.setOnClickListener {
                cpf = if (cbWithoutCpf.isChecked) EMPTY else cpftv.text.toString().clearCNPJMask()

                findNavController().navigate(
                    AccessManagerAddUserCpfFragmentDirections.actionAccessManagerAddUserCpfFragmentToAccessManagerAddUserEmailFragment(
                        isForeignArg, nationalityCodeArg, cpf, roleArg
                    )
                )
            }
        }
    }

    private fun updateView() {
        var titleScreen = getString(R.string.access_manager_addUser_cpf_title)
        var subTitleScreen = getString(R.string.access_manager_addUser_cpf_desc)

        binding?.apply {
            cbWithoutCpf.gone()
            cbWithoutCpf.isChecked = false
            tvWithoutCpfDescription.gone()

            if (isForeignArg) {
                titleScreen = getString(R.string.access_manager_cpf_foreign_title)
                subTitleScreen = getString(R.string.access_manager_cpf_foreign_subtitle)

                cbWithoutCpf.visible()
                tvWithoutCpfDescription.visible()
            }

            tvTitle.text = titleScreen
            tvSubtitle.text = subTitleScreen
        }
    }

    fun isValid(value: String?): Boolean {
        return value?.length == CPF_LENGTH
                && ValidationUtils.isCPF(value)
    }

    private fun setupError(isSuccess: Boolean, error: TextView?, text: String) {
        if (isSuccess) {
            error?.gone()
        } else {
            if ((text == EMPTY).not()) {
                error?.text = text
            } else {
                error?.text = getString(R.string.invalid_cpf)
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
        binding?.apply {
            doWhenResumed(
                action = {
                    if (isValid(cpftv.text.toString())) {
                        nextButtonAddUser.isEnabled =
                            cpftv.text.toString()
                                .onlyDigits() != IDOnboardingFlowHandler.userStatus.cpf
                        cpftvValueErrorLabel.gone()
                        setupError(true, cpftvValueErrorLabel, EMPTY)
                    } else {
                        setupError(false, cpftvValueErrorLabel, EMPTY)
                        if (nextButtonAddUser.isEnabled) {
                            nextButtonAddUser.isEnabled = false
                        }
                    }
                },
                errorCallback = { baseLogout() }
            )
        }
    }

    override fun showErrorLabel(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        doWhenResumed(
            action = {
                setupError(
                    false,
                    binding?.cpftvValueErrorLabel,
                    messageError(error, requireActivity())
                )
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showErrorGeneric(error: ErrorMessage?) {
        doWhenResumed(
            action = {
                enableButton(false)
                binding?.cpftvValueErrorLabel.gone()
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.id_onboarding_bs_title_error_generic),
                    message = messageError(error, requireActivity()),
                    bt2Title = getString(R.string.text_try_again_label),
                    bt2Callback = {
                        false
                    },
                    isPhone = false
                )
            }
        )
    }

    override fun onErrorCpfDuplicated() {
        doWhenResumed(
            action = {
                enableButton(false)
                binding?.cpftvValueErrorLabel.gone()
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.duplicated_invite),
                    message = getString(
                        R.string.bottom_sheet_duplicated_invite_mensage,
                        binding?.cpftv?.text.toString()
                    ),
                    bt2Title = getString(R.string.resend_invites_screen),
                    bt2Callback = {
                        goToResendInviteScreen()
                        false
                    },
                    isPhone = false
                )
            }
        )
    }

    private fun goToResendInviteScreen() {
        findNavController().navigate(
            AccessManagerAddUserCpfFragmentDirections.actionAccessManagerAddUserCpfFragmentToAccessManagerExpiredInvitationFragment()
        )
    }

    private fun clearCpf() {
        binding?.apply {
            cpftv.text?.clear()
            cpftvValueErrorLabel.apply {
                gone()
                text = EMPTY
            }
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}